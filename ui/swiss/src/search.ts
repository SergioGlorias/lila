import { h, VNode } from 'snabbdom';
import * as licon from 'common/licon';
import { bind, onInsert } from 'common/snabbdom';
import TournamentController from './ctrl';
import { userComplete } from 'common/userComplete';

export function button(ctrl: TournamentController): VNode {
  return h('button.fbt', {
    class: { active: ctrl.searching },
    attrs: { 'data-icon': ctrl.searching ? licon.X : licon.Search, title: 'Search tournament players' },
    hook: bind('click', ctrl.toggleSearch, ctrl.redraw),
  });
}

export function input(ctrl: TournamentController): VNode {
  return h(
    'div.search',
    h('input', {
      attrs: { spellcheck: 'false' },
      hook: onInsert((el: HTMLInputElement) => {
        userComplete({
          input: el,
          swiss: ctrl.data.id,
          tag: 'span',
          focus: true,
          onSelect(r) {
            ctrl.jumpToPageOf(r.id);
            ctrl.redraw();
          },
        });
        $(el).on('keydown', e => {
          if (e.code === 'Enter') {
            const rank = parseInt(e.target.value);
            if (rank > 0) ctrl.jumpToRank(rank);
          }
          if (e.code === 'Escape') {
            ctrl.toggleSearch();
            ctrl.redraw();
          }
        });
      }),
    }),
  );
}
