import { FiledFormat, AmountFormat, AmountColumn } from '@/components/Format'
import { Tag } from 'antd'

const ALL_COLUMNS = [
  {
    title: '月份',
    dataIndex: 'yearAndMonth',
    render: (value) => <FiledFormat value={value} />,
  },
  {
    title: '资产类型',
    dataIndex: 'propertyTypeDisplay',
    render: (value) => <FiledFormat value={value} />,
  },
  {
    title: '融资渠道',
    dataIndex: 'organizationName',
    search: true,
    render: (value) => <FiledFormat value={value} />,
  },
  {
    title: '融资编号',
    dataIndex: 'financingCode',
    search: true,
    render: (value) => <FiledFormat value={value} />,
  },
  {
    title: '融资金额(元)',
    dataIndex: 'financingAmount',
    align: 'right',
    render: (value) => <AmountFormat value={value} />,
  },
  // {
  //   title: '业务类型',
  //   dataIndex: 'businessType',
  //   matchOption: 'projEstablishBizType',
  // },
  {
    title: '融资利率',
    dataIndex: 'financingRate',
    render: (value) => <AmountFormat value={value} unit="%" />,
  },
  {
    title: '日利率',
    dataIndex: 'dailyRate',
    render: (value) => (
      <AmountFormat value={value} unit="%" precision={4} needSmallNumber={false} />
    ),
  },
  {
    title: '累计计提利息成本(含税)(元)',
    dataIndex: 'totalCapitalCost',
    width: 220,
    align: 'right',
    render: (value) => <AmountFormat value={value} />,
  },
  {
    title: '累计计提利息成本(不含税)(元)',
    dataIndex: 'totalCapitalCostAfterTax',
    align: 'right',
    width: 220,
    render: (value) => <AmountFormat value={value} />,
  },
  AmountColumn({
    title: '当期计提利息成本（含税）（元）',
    dataIndex: 'termCapitalCost',
    width: 220,
  }),
  AmountColumn({
    title: '当期计提利息成本（不含税）（元）',
    dataIndex: 'termCapitalCostAfterTax',
    width: 220,
  }),
  AmountColumn({ title: '钆差金额', dataIndex: 'financingCostDiff', needSmallNumber: false }),
  AmountColumn({
    title: '期初计提利息余额',
    dataIndex: 'beginOfPeriodInterestBalance',
    needSmallNumber: false,
  }),
  AmountColumn({
    title: '期末计提利息余额',
    dataIndex: 'endOfPeriodInterestBalance',
    needSmallNumber: false,
  }),
  {
    title: '是否确认',
    dataIndex: 'isConfirmed',
    render: (text) => {
      return <Tag color={text ? 'green' : 'gray'}>{text ? '已确认' : '未确认'}</Tag>
    },
  },
]
export default ALL_COLUMNS
