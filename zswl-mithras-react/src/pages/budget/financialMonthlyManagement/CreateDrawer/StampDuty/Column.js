import { ClientSelect } from '@/components'
import { FiledFormat, AmountFormat } from '@/components/Format'
import { Tag } from 'antd'

export const POJ_COLUMNS = [
  {
    title: '月份',
    dataIndex: 'yearAndMonth',
    render: (value) => <FiledFormat value={value} />,
  },
  {
    title: '客户名称',
    dataIndex: 'clientId',
    search: {
      element: <ClientSelect canJump={false} />,
      functionCode: 'monthlyMangeClientFunctionCode',
    },
    render: (value, { clientName }) => <FiledFormat value={clientName} />,
  },
  {
    title: '合同编号',
    search: true,
    dataIndex: 'contractCode',
    render: (value) => <FiledFormat value={value} />,
  },
  {
    title: '本月计提印花税(元)',
    dataIndex: 'stampDuty',
    align: 'right',
    render: (value) => <AmountFormat value={value} />,
  },
  {
    title: '是否确认',
    dataIndex: 'isEffect',
    render: (text) => {
      return <Tag color={text ? 'green' : 'gray'}>{text ? '已确认' : '未确认'}</Tag>
    },
  },
]

export const FIN_COLUMNS = [
  {
    title: '月份',
    dataIndex: 'yearAndMonth',
    render: (value) => <FiledFormat value={value} />,
  },
  {
    title: '融资渠道',
    dataIndex: 'organizationName',
    render: (value) => <FiledFormat value={value} />,
  },
  {
    title: '融资编号',
    dataIndex: 'financingCode',
    render: (value) => <FiledFormat value={value} />,
  },
  {
    title: '本月计提印花税(元)',
    dataIndex: 'stampDuty',
    align: 'right',
    render: (value) => <AmountFormat value={value} />,
  },
  {
    title: '是否确认',
    dataIndex: 'isConfirmed',
    render: (text) => {
      return <Tag color={text ? 'green' : 'gray'}>{text ? '已确认' : '未确认'}</Tag>
    },
  },
]
