import { AmountColumn } from '@/components/Format'
import { App } from '@zswl/components'
import { history } from '@zswl/admin'

const INIT_FORMAT = 10000 * 10000
const ALL_COLUMNS = [
  // 统计
  {
    title: '融资渠道',
    dataIndex: 'financialChannel',
    width: 220,
  },
  {
    title: '融资编码',
    dataIndex: 'financialCode',
    actions: ({ financialCode: name, financingType, financingId }) => [
      {
        name,
        onClick: () => {
          if (financingType === 'DIRECT') {
            history.push(`/financial/direct/detail/${financingId}`)
          } else {
            history.push(`/financial/fund/detail/${financingId}`)
          }
        },
      },
    ],
  },
  AmountColumn({
    title: '融资总额（万元）',
    dataIndex: 'financialAmount',
    initFormat: INIT_FORMAT,
    width: 150,
    align: 'right',
  }),
  {
    title: '现金流出时间',
    dataIndex: 'cashOutDate',
    width: 120,
  },
  AmountColumn({
    title: '现金流出总金额（万元）',
    dataIndex: 'cashOutTotal',
    initFormat: INIT_FORMAT,
    align: 'right',
  }),
  AmountColumn({
    title: '现金流出本金（万元）',
    dataIndex: 'cashOutPrincipal',
    initFormat: INIT_FORMAT,
    align: 'right',
  }),
  AmountColumn({
    title: '现金流出利息（万元）',
    dataIndex: 'cashOutInterest',
    initFormat: INIT_FORMAT,
    align: 'right',
  }),
  {
    title: '流动性盈缺',
    dataIndex: 'lackBalance',
    width: 140,
    matchOption: 'trueOrFalseEn',
    render: (val) => {
      const res = App.matchOption('trueOrFalseEn', val)?.label
      return <div style={res === 'Y' ? { color: 'red' } : {}}>{res}</div>
    },
  },
  {
    title: '期限错配',
    dataIndex: 'mismatchBalance',
    width: 100,
    matchOption: 'trueOrFalseEn',
    render: (val) => {
      const res = App.matchOption('trueOrFalseEn', val)?.label
      return <div style={res === 'Y' ? { color: 'red' } : {}}>{res}</div>
    },
  },
  {
    title: '项目名称',
    dataIndex: 'projName',
    width: 280,
    render: (v) => v || '-',
  },
  {
    title: '合同编号',
    dataIndex: 'contractCode',
    width: 280,
    render: (v) => v || '-',
  },
  AmountColumn({
    title: '合同总金额（万元）',
    dataIndex: 'contractAmount',
    initFormat: INIT_FORMAT,
    align: 'right',
  }),
  AmountColumn({
    title: '现金流出利息（万元）',
    dataIndex: 'cashOutInterest',
    initFormat: INIT_FORMAT,
    align: 'right',
  }),
  {
    title: '现金流入时间',
    dataIndex: 'cashInDate',
    render: (v) => v || '-',
  },
  AmountColumn({
    title: '现金流入总金额（万元）',
    dataIndex: 'cashInTotal',
    initFormat: INIT_FORMAT,
    align: 'right',
  }),
  AmountColumn({
    title: '现金流入本金（万元）',
    dataIndex: 'cashInPrincipal',
    initFormat: INIT_FORMAT,
    align: 'right',
  }),
  AmountColumn({
    title: '现金流入利息（万元）',
    dataIndex: 'cashInInterest',
    initFormat: INIT_FORMAT,
    align: 'right',
  }),
]
export default ALL_COLUMNS
