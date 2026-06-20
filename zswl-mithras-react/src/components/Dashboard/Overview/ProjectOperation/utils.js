import ArrowDown from '../../assets/arrow_down.svg'
import ArrowLeft from '../../assets/arrow_left.svg'
import ArrowRight from '../../assets/arrow_right.svg'

export const stepConfig = [
  {
    stageName: '访客',
    iconType: 'icon-fangke',
    borderColor: '#FF5962',
    arrowComp: <ArrowRight />,
    arrowStyle: { top: 105, right: -22 },
  },
  {
    stageName: '立项',
    iconType: 'icon-lixiang',
    borderColor: '#3377FF',
    arrowComp: <ArrowRight />,
    arrowStyle: { top: 105, right: -22 },
  },
  {
    stageName: '尽调',
    iconType: 'icon-jindiao',
    borderColor: '#FF9845',
    arrowComp: <ArrowRight />,
    arrowStyle: { top: 105, right: -22 },
  },
  {
    stageName: '评审',
    iconType: 'icon-pingshen',
    borderColor: '#35D2A2',
    arrowComp: <ArrowDown />,
    arrowStyle: { top: 210, left: '50%' },
  },
  {
    stageName: '评审通过未创建合同',
    iconType: 'icon-hetong',
    borderColor: '#FFCA69',
    arrowComp: <ArrowLeft />,
    arrowStyle: { top: 105, left: -22 },
  },
  {
    stageName: '签约',
    iconType: 'icon-qianyue',
    borderColor: '#2BC6FF',
    arrowComp: <ArrowLeft />,
    arrowStyle: { top: 105, left: -22 },
  },
  {
    stageName: '付款',
    iconType: 'icon-fukuan',
    borderColor: '#985FF7',
    arrowComp: <ArrowLeft />,
    arrowStyle: { top: 105, left: -22 },
  },
  {
    stageName: '投放',
    iconType: 'icon-toufang',
    borderColor: '#43A8C7',
    arrowComp: null,
    arrowStyle: null,
  },
]
