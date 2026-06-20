import { ClientSelect } from '@/components/Select'
import { FiledFormat, AmountFormat, MatchOptionColumn } from '@/components/Format'
import { Tag } from 'antd'

const ALL_COLUMNS = [
  {
    title: '月份',
    dataIndex: 'yearAndMonth',
    render: (value) => <FiledFormat value={value} />,
  },
  {
    title: '客户名称',
    dataIndex: 'clientId',
    width: 280,
    search: {
      element: <ClientSelect canJump={false} />,
      functionCode: 'monthlyMangeClientFunctionCode',
    },
    render: (value, { clientName }) => <FiledFormat value={clientName} />,
  },
  {
    title: '项目名称',
    dataIndex: 'projName',
    width: 280,
    render: (value) => <FiledFormat value={value} />,
  },
  {
    title: '合同编号',
    dataIndex: 'contractCode',
    width: 280,
    render: (value) => <FiledFormat value={value} />,
    search: true,
  },
  {
    title: '借据编号',
    dataIndex: 'receiptCode',
    render: (value) => <FiledFormat value={value} />,
  },
  {
    title: '实际起租日期',
    dataIndex: 'actualLeaseDate',
    render: (value) => <FiledFormat value={value} />,
  },
  {
    title: '本月计提收入金额(不含税)(元)',
    dataIndex: 'incomeWithoutTaxSum',
    align: 'right',
    width: 220,
    render: (value) => <AmountFormat value={value} />,
  },
  {
    title: '本月收入金额(含税)(元)',
    dataIndex: 'incomeSum',
    align: 'right',
    render: (value) => <AmountFormat value={value} />,
  },
  {
    title: '租赁类型',
    dataIndex: 'leaseType',
    matchOption: 'leaseType',
  },
  {
    title: '税率',
    dataIndex: 'taxRate',
    render: (value) => <AmountFormat value={value} unit="%" />,
  },
  MatchOptionColumn({
    title: '当前是否逾期',
    dataIndex: 'overdueType',
    search: true,
    matchOption: 'overdueTypeEnum',
  }),
  {
    title: '最近一期全额偿还租金期次',
    dataIndex: 'theLatestFullRefundRentPeriod',
    width: 220,
    render: (value) => <FiledFormat value={value} />,
  },
  {
    title: '最近一期全额偿还租金应收日期',
    dataIndex: 'theLatestFullRefundRentDate',
    width: 220,
    render: (value) => <FiledFormat value={value} />,
  },
  {
    title: '最近一期全额偿还租金应收剩余本金(元)',
    dataIndex: 'theLatestFullRefundRentCapital',
    align: 'right',
    width: 260,
    render: (value) => <AmountFormat value={value} />,
  },
  {
    title: '合同名义利率',
    dataIndex: 'contractNominalInterestRate',
    render: (value) => <AmountFormat value={value} unit="%" />,
  },
  // {
  //   title: '天数',
  //   dataIndex: 'days',
  //   render: (value) => <FiledFormat value={value} />,
  // },
  {
    title: '是否确认',
    dataIndex: 'isConfirmed',
    render: (text) => {
      return <Tag color={text ? 'green' : 'gray'}>{text ? '已确认' : '未确认'}</Tag>
    },
  },
]
export default ALL_COLUMNS
