export const levelColor = ['#06EAB2', '#2D66FF', '#2EC5FF', '#FFCA69', '#FF5962']
export const quaraterMap = [
  { value: 1, text: '一' },
  { value: 2, text: '二' },
  { value: 3, text: '三' },
  { value: 4, text: '四' },
]

export const MapStatus = {
  FINISH: 'finish',
  WAIT: 'wait',
  PROCESS: 'process',
}

export const GroupKey = ['REVIEW', 'REVIEW_MEETING', 'RISK_MEETING']

export const InitProcessData = [
  {
    nodeLable: '初分',
    key: 'INIT',
    nodeStatue: 'WAIT',
  },
  {
    nodeLable: '复核',
    key: 'REVIEW',
    nodeStatue: 'WAIT',
  },
  {
    nodeLable: '评审会',
    key: 'REVIEW_MEETING',
    nodeStatue: 'WAIT',
  },
  {
    nodeLable: '风委会',
    key: 'RISK_MEETING',
    nodeStatue: 'WAIT',
  },
]

export const FileCommonParams = {
  chufen: {
    moduleType: 'ASSET_CLASSIFY_REVIEW',
  },
  checkContent: {
    moduleType: 'ASSET_CLASSIFY_REVIEW',
  },
}

export const NodeTypeNames = new Map([
  ['评审会流程', 'AssetClassifyReviewMeetingFlow'],
  ['风委会流程', 'AssetClassifyRiskMeetingFlow'],
  ['董事会流程', 'AssetClassifyBoardFlow'],
])
