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
    search: true,
    width: 280,
    render: (value) => <FiledFormat value={value} />,
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
    title: '本月收入金额(不含税)(元)',
    dataIndex: 'incomeWithoutTaxSum',
    width: 200,
    align: 'right',
    render: (value) => <AmountFormat value={value} />,
  },
  {
    title: '本月收入金额(含税)(元)',
    dataIndex: 'incomeSum',
    width: 200,
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
    matchOption: 'overdueTypeEnum',
    search: true,
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
