import {
  FiledFormat,
  AmountColumn,
  MatchOptionColumn,
  TextAreaColumn,
  DateColumn,
} from '@/components/Format'

const ALL_COLUMNS = ({ pathname } = {}) => {
  return [
    DateColumn({
      title: '日期',
      dataIndex: 'recordDate',
      search: true,
    }),
    AmountColumn({
      title: 'FTP指引价格',
      dataIndex: 'guidePrice',
      suffix: '%',
    }),
    AmountColumn({
      title: 'FTP是否质押',
      dataIndex: 'pledgePrice',
      suffix: '%',
    }),
    AmountColumn({
      title: 'FTP是否逾期',
      dataIndex: 'overduePrice',
      suffix: '%',
    }),
    AmountColumn({
      title: 'FTP手工调整',
      dataIndex: 'handAdjustment',
      suffix: '%',
    }),
    AmountColumn({
      title: 'FTP考核价格',
      dataIndex: 'assessmentPrice',
      suffix: '%',
    }),

    TextAreaColumn({
      title: 'FTP备注',
      requiredMark: true,
      dataIndex: 'remark',
      width: 300,
    }),
    MatchOptionColumn({
      title: '成本是否已确认',
      dataIndex: 'costIsConfirmed',
      matchOption: 'yesOrNo',
    }),
  ]
}

export default ALL_COLUMNS
