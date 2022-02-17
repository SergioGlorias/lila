package lila.common

import com.vladsch.flexmark.ext.autolink.AutolinkExtension
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension
import com.vladsch.flexmark.ext.tables.TablesExtension
import com.vladsch.flexmark.html.{ AttributeProvider, HtmlRenderer, IndependentAttributeProviderFactory }
import com.vladsch.flexmark.html.renderer.{ AttributablePart, LinkResolverContext }
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.ast.Node
import com.vladsch.flexmark.util.data.{ MutableDataHolder, MutableDataSet }
import com.vladsch.flexmark.util.html.MutableAttributes
import com.vladsch.flexmark.ast.Link
import java.util.Arrays
import scala.jdk.CollectionConverters._
import lila.base.RawHtml

final class Markdown(
    autoLink: Boolean = true,
    table: Boolean = false,
    strikeThrough: Boolean = false,
    header: Boolean = false,
    blockQuote: Boolean = false,
    list: Boolean = false,
    code: Boolean = false
) {

  private type Key  = String
  private type Text = String
  private type Html = String

  private val extensions = new java.util.ArrayList[com.vladsch.flexmark.util.misc.Extension]()
  if (table) extensions.add(TablesExtension.create())
  if (strikeThrough) extensions.add(StrikethroughExtension.create())
  if (autoLink) extensions.add(AutolinkExtension.create())
  extensions.add(Markdown.NofollowExtension)

  private val options = new MutableDataSet()
    .set(Parser.EXTENSIONS, extensions)
    .set(HtmlRenderer.ESCAPE_HTML, Boolean box true)
    .set(HtmlRenderer.SOFT_BREAK, "<br>")
    // always disabled
    .set(Parser.HTML_BLOCK_PARSER, Boolean box false)
    .set(Parser.INDENTED_CODE_BLOCK_PARSER, Boolean box false)
    .set(Parser.FENCED_CODE_BLOCK_PARSER, Boolean box code)

  // configurable
  if (table) options.set(TablesExtension.CLASS_NAME, "slist")
  if (!header) options.set(Parser.HEADING_PARSER, Boolean box false)
  if (!blockQuote) options.set(Parser.BLOCK_QUOTE_PARSER, Boolean box false)
  if (!list) options.set(Parser.LIST_BLOCK_PARSER, Boolean box false)

  private val immutableOptions = options.toImmutable

  private val parser   = Parser.builder(immutableOptions).build()
  private val renderer = HtmlRenderer.builder(immutableOptions).build()

  private val logger = lila.log("markdown")

  private def mentionsToLinks(markdown: Text): Text =
    RawHtml.atUsernameRegex.replaceAllIn(markdown, "[@$1](/@/$1)")

  // https://github.com/vsch/flexmark-java/issues/496
  private val tooManyUnderscoreRegex             = """(_{4,})""".r
  private def preventStackOverflow(text: String) = tooManyUnderscoreRegex.replaceAllIn(text, "_" * 3)

  def apply(key: Key)(text: Text): Html =
    Chronometer
      .sync {
        try {
          renderer.render(parser.parse(mentionsToLinks(preventStackOverflow(text))))
        } catch {
          case e: StackOverflowError =>
            logger.branch(key).error("StackOverflowError", e)
            text
        }
      }
      .mon(_.markdown.time)
      .logIfSlow(50, logger.branch(key))(_ => s"slow markdown size:${text.size}")
      .result
}

object Markdown {

  private object NofollowExtension extends HtmlRenderer.HtmlRendererExtension {
    override def rendererOptions(options: MutableDataHolder) = ()
    override def extend(htmlRendererBuilder: HtmlRenderer.Builder, rendererType: String) =
      htmlRendererBuilder
        .attributeProviderFactory(new IndependentAttributeProviderFactory {
          override def apply(context: LinkResolverContext): AttributeProvider = NofollowAttributeProvider
        })
        .unit
  }
  private object NofollowAttributeProvider extends AttributeProvider {
    override def setAttributes(node: Node, part: AttributablePart, attributes: MutableAttributes) = {
      if (node.isInstanceOf[Link] && part == AttributablePart.LINK)
        attributes.replaceValue("rel", "nofollow noopener noreferrer").unit
    }
  }

  private val imageRegex = """!\[[^\]]*\]\((.*?)\s*("(?:.*[^"])")?\s*\)""".r

  def imageUrls(markdown: String): List[String] =
    imageRegex.findAllIn(markdown).matchData.map(_ group 1).toList
}
