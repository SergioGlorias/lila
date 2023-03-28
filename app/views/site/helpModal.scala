package views.html.site

import play.api.i18n.Lang

import lila.app.templating.Environment.{ given, * }
import lila.app.ui.ScalatagsTemplate.{ *, given }

object helpModal:

  private def header(text: Frag)          = tr(th(colspan := 2)(p(text)))
  private def row(keys: Frag, desc: Frag) = tr(td(cls := "keys")(keys), td(cls := "desc")(desc))
  private val or                          = tag("or")
  private val kbd                         = tag("kbd")
  private def voice(text: String)         = tag("voice")(s"\"$text\"")

  private def navigateMoves(implicit lang: Lang) = frag(
    header(trans.navigateMoveTree()),
    row(frag(kbd("←"), or, kbd("→")), trans.keyMoveBackwardOrForward()),
    row(frag(kbd("k"), or, kbd("j")), trans.keyMoveBackwardOrForward()),
    row(frag(kbd("↑"), or, kbd("↓")), trans.keyGoToStartOrEnd()),
    row(frag(kbd("0"), or, kbd("$")), trans.keyGoToStartOrEnd()),
    row(frag(kbd("home"), or, kbd("end")), trans.keyGoToStartOrEnd())
  )
  private def flip(implicit lang: Lang)       = row(kbd("f"), trans.flipBoard())
  private def zen(implicit lang: Lang)        = row(kbd("z"), trans.preferences.zenMode())
  private def helpDialog(implicit lang: Lang) = row(kbd("?"), trans.showHelpDialog())
  private def localAnalysis(implicit lang: Lang) = frag(
    row(kbd("l"), trans.toggleLocalAnalysis()),
    row(kbd("space"), trans.playComputerMove()),
    row(kbd("x"), trans.showThreat())
  )

  def round(implicit lang: Lang) =
    frag(
      h2(trans.keyboardShortcuts()),
      table(
        tbody(
          navigateMoves,
          header(trans.other()),
          flip,
          zen,
          helpDialog
        )
      )
    )
  def puzzle(implicit lang: Lang) =
    frag(
      h2(trans.keyboardShortcuts()),
      table(
        tbody(
          navigateMoves,
          header(trans.analysisOptions()),
          localAnalysis,
          row(kbd("n"), trans.puzzle.nextPuzzle()),
          header(trans.other()),
          flip,
          zen,
          helpDialog
        )
      )
    )
  def analyse(isStudy: Boolean)(implicit lang: Lang) =
    frag(
      h2(trans.keyboardShortcuts()),
      table(
        tbody(
          navigateMoves,
          row(frag(kbd("shift"), kbd("←"), or, kbd("shift"), kbd("→")), trans.keyEnterOrExitVariation()),
          row(frag(kbd("shift"), kbd("J"), or, kbd("shift"), kbd("K")), trans.keyEnterOrExitVariation()),
          header(trans.analysisOptions()),
          flip,
          row(frag(kbd("shift"), kbd("I")), trans.inlineNotation()),
          localAnalysis,
          row(kbd("z"), trans.toggleAllAnalysis()),
          row(kbd("a"), trans.bestMoveArrow()),
          row(kbd("e"), trans.openingEndgameExplorer()),
          row(kbd("c"), trans.focusChat()),
          row(frag(kbd("shift"), kbd("C")), trans.keyShowOrHideComments()),
          helpDialog,
          isStudy option frag(
            header(trans.study.studyActions()),
            row(kbd("d"), trans.study.commentThisPosition()),
            row(kbd("g"), trans.study.annotateWithGlyphs()),
            row(kbd("n"), trans.study.nextChapter()),
            row(kbd("p"), trans.study.prevChapter()),
            row(frag((1 to 6).map(kbd(_))), "Toggle glyph annotations")
          ),
          header(trans.mouseTricks()),
          tr(
            td(cls := "mouse", colspan := 2)(
              ul(
                li(trans.youCanAlsoScrollOverTheBoardToMoveInTheGame()),
                li(trans.scrollOverComputerVariationsToPreviewThem()),
                li(trans.analysisShapesHowTo())
              )
            )
          )
        )
      )
    )

  def keyboardMove(implicit lang: Lang) =
    import trans.keyboardMove.*
    frag(
      h2(keyboardInputCommands()),
      table(
        tbody(
          header(performAMove()),
          row(kbd("e2e4"), movePieceFromE2ToE4()),
          row(kbd("5254"), movePieceFromE2ToE4()),
          row(kbd("Nc3"), moveKnightToC3()),
          row(kbd("O-O"), kingsideCastle()),
          row(kbd("O-O-O"), queensideCastle()),
          row(kbd("c8=Q"), promoteC8ToQueen()),
          row(kbd("R@b4"), dropARookAtB4()),
          header(otherCommands()),
          row(kbd("/"), trans.focusChat()),
          row(kbd("clock"), readOutClocks()),
          row(kbd("who"), readOutOpponentName()),
          row(kbd("draw"), offerOrAcceptDraw()),
          row(kbd("resign"), trans.resignTheGame()),
          row(kbd("next"), trans.puzzle.nextPuzzle()),
          row(kbd("upv"), trans.puzzle.upVote()),
          row(kbd("downv"), trans.puzzle.downVote()),
          row(frag(kbd("help"), or, kbd("?")), trans.showHelpDialog()),
          header(tips()),
          tr(
            td(cls := "tips", colspan := 2)(
              ul(
                li(
                  ifTheAboveMoveNotationIsUnfamiliar(),
                  a(target := "_blank", href := "https://en.wikipedia.org/wiki/Algebraic_notation_(chess)")(
                    "Algebraic notation"
                  )
                ),
                li(includingAXToIndicateACapture()),
                li(bothTheLetterOAndTheDigitZero()),
                li(ifItIsLegalToCastleBothWays()),
                li(capitalizationOnlyMattersInAmbiguousSituations()),
                li(toPremoveSimplyTypeTheDesiredPremove())
              )
            )
          )
        )
      )
    )
  def voiceMove(implicit lang: Lang) =
    import trans.keyboardMove.*
    frag(
      h2("Voice commands"),
      table(
        tbody(
          tr(th(p("Instructions"))),
          tr(
            td(cls := "tips")(
              ul(
                li(
                  "Click the microphone to enable voice moves. It glows red when listening."
                ),
                li(
                  "Your voice audio never leaves your device. Moves are sent as plain text just like those made by mouse or touch."
                ),
                li(
                  "You may speak UCI, SAN, piece names, board squares, or phrases like ",
                  strong("\"pawn takes rook\""),
                  " and ",
                  strong("\"takes\""),
                  ". Click ",
                  strong("Show me everything"),
                  " for a full list."
                ),
                li(
                  "Ambiguous commands show colored or numbered arrows. Speak the color or number to choose one, or say ",
                  strong("\"clear\""),
                  " to cancel. Set your arrow style using the hamburger menu."
                ),
                li(
                  "Higher values for the confidence slider result in less ambiguity but an increased chance of mishearing."
                ),
                li(
                  "Up to 8 arrows are shown. At lower confidence settings we include additional moves that sound alike."
                ),
                li(
                  "At present, voice control is only available with standard chess in puzzles and unrated games."
                ),
                li(
                  "The phonetic alphabet is ",
                  strong(
                    "alfa, bravo, charlie, delta, echo, foxtrot, golf, hotel."
                  )
                )
              )
            )
          )
        )
      ),
      div(cls := "commands")(
        table(
          tbody(
            header(performAMove()),
            row(frag(voice("e4"), voice("echo 4")), "Move to e4 or select a piece there"),
            row(voice("knight"), "Move my knight or capture a knight"),
            row(frag(voice("bishop h6"), voice("bishop hotel 6")), "Move bishop to h6"),
            row(voice("queen takes rook"), "Take rook with queen"),
            row(
              frag(voice("c8 promote knight"), voice("charlie 8 knight")),
              "Move c8 promote to knight"
            ),
            row(voice("castle"), "Kingside castle"),
            row(frag(voice("long castle"), voice("queenside castle")), "Queenside castle"),
            row(frag(voice("a7g1"), voice("alfa 7 golf 1")), "Full UCI works too")
          )
        ),
        table(
          tbody(
            header(otherCommands()),
            row(voice("draw"), offerOrAcceptDraw()),
            row(voice("resign"), trans.resignTheGame()),
            row(voice("ooops"), "Request a takeback"),
            row(
              frag(voice("clear"), voice("no")),
              "Clear arrows, selection, or this dialog"
            ),
            row(frag(voice("yes"), voice("confirm")), "Confirm single arrow"),
            row(voice("stop"), "Stop listening"),
            row(voice("next"), trans.puzzle.nextPuzzle()),
            row(voice("help"), trans.showHelpDialog()),
            tr(
              td,
              td(button(cls := "button", id := "all-phrases-button")("Show me everything"))
            )
          )
        )
      )
    )