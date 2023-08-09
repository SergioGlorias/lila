import { RoundOpts, RoundData } from 'round';
import { Ctrl } from './ctrl';
//import { Player, GameData } from 'game';

/*interface RoundApi {
  socketReceive(typ: string, data: any): boolean;
  moveOn: MoveOn;
}*/

const data: RoundData = {
  game: {
    id: 'x7hgwoir',
    variant: { key: 'standard', name: 'Standard', short: 'Std' },
    speed: 'classical',
    perf: 'classical',
    rated: false,
    fen: 'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1',
    turns: 0,
    source: 'friend',
    status: { id: 20, name: 'started' },
    player: 'white',
  },
  player: {
    color: 'white',
    user: {
      id: 'anonymous',
      username: 'Anonymous',
      online: true,
      perfs: {},
    },
    rating: 1628,
    id: '7J3E',
    isGone: false,
    name: 'Anonymous',
    onGame: true,
    version: 0,
  },
  opponent: {
    color: 'black',
    user: {
      id: 'anonymous',
      username: 'Baby Howard',
      online: true,
      perfs: {},
    },
    id: '',
    isGone: false,
    name: 'Baby Howard',
    onGame: true,
    rating: 800,
    version: 0,
    image: '/assets/lifat/bots/images/coral.webp',
  },
  pref: {
    animationDuration: 300,
    coords: 1,
    resizeHandle: 1,
    replay: 2,
    autoQueen: 2,
    clockTenths: 1,
    moveEvent: 2,
    clockBar: true,
    clockSound: true,
    confirmResign: true,
    rookCastle: true,
    highlight: true,
    destination: true,
    enablePremove: true,
    showCaptured: true,
    blindfold: false,
    is3d: false,
    keyboardMove: false,
    voiceMove: false,
    ratings: true,
    submitMove: false,
  },
  steps: [{ ply: 0, san: '', uci: '', fen: 'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1' }],
  /*correspondence: {
    daysPerTurn: 2,
    increment: 0,
    white: 0,
    black: 0,
    showBar: true,
  },*/
  takebackable: true,
  moretimeable: true,
};

export async function makeRounds(ctrl: Ctrl): Promise<SocketSend> {
  const moves: string[] = [];
  console.log(ctrl.dests);
  for (const from in ctrl.dests) {
    moves.push(from + ctrl.dests[from]);
  }
  const opts: RoundOpts = {
    element: document.querySelector('.round__app') as HTMLElement,
    data: { ...data, possibleMoves: moves.join(' ') },
    socketSend: (t: string, d: any) => {
      if (t === 'move') {
        ctrl.userMove(d.u);
      }
    },
    crosstableEl: document.querySelector('.cross-table') as HTMLElement,
    i18n: {},
    onChange: (d: RoundData) => console.log(d),
    local: true,
  };
  return lichess.loadEsm('round', { init: opts });
}
