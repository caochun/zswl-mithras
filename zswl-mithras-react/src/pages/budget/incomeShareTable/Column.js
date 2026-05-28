import { AmountColumn, MatchOptionColumn, InputColumn, FiledFormat } from '@/components/Format'
import { ClientSelect } from '@/components'
import { Select } from '@zswl/components'
import { Input } from 'antd'

const ALL_COLUMNS = (path) => [
  InputColumn({
    title: '借据编号',
    dataIndex: 'receiptCode',
    actions({ receiptCode, receiptId }) {
      return [{ name: receiptCode, to: `${path}/detail/${receiptId}?receiptCode=${receiptCode}` }]
    },
  }),
  InputColumn({
    title: '客户名称',
    width: 250,
    dataIndex: 'clientId',
    editable: {
      element: <ClientSelect canJump={false} />,
      functionCode: 'clientListIncomeShareTable',
    },
    render: (value, { clientName }) => <FiledFormat title={clientName}></FiledFormat>,
  }),
  InputColumn({
    title: '项目名称',
    width: 250,
    dataIndex: 'projName',
    editable: {
      element: <Input />,
    },
  }),
  InputColumn({
    title: '合同编号',
    width: 250,
    dataIndex: 'contractCode',
    editable: {
      element: <Input></Input>,
    },
  }),
  MatchOptionColumn({
    title: '合同状态',
    dataIndex: 'contractStatus',
    matchOption: 'contractStatus',
    editable: {
      element: <Select options="contractStatus"></Select>,
    },
  }),
  InputColumn({
    title: '起租日期',
    dataIndex: 'startRentTime',
  }),
  MatchOptionColumn({
    title: '收入分摊方式',
    dataIndex: 'incomeConfirmType',
    matchOption: 'incomeConfirmTypeEnum',
  }),
  MatchOptionColumn({
    title: '是否逾期',
    dataIndex: 'overdueType',
    matchOption: 'overdueTypeEnum',
    editable: {
      element: <Select options="overdueTypeEnum"></Select>,
    },
  }),
  InputColumn({
    title: '逾期开始日期',
    dataIndex: 'overdueStartTime',
  }),
  AmountColumn({
    title: '含税收入合计',
    dataIndex: 'incomeSum',
  }),
  AmountColumn({
    title: '不含税收入合计',
    dataIndex: 'incomeWithoutTaxSum',
  }),
  AmountColumn({
    title: '已确认收入合计',
    dataIndex: 'confirmedIncomeSum',
  }),
  AmountColumn({
    title: '未确认收入合计',
    dataIndex: 'unconfirmedIncomeSum',
  }),
  AmountColumn({
    title: '月度含税收入合计',
    dataIndex: 'monthlyIncomeSum',
  }),
  AmountColumn({
    title: '月度不含税收入合计',
    dataIndex: 'monthlyIncomeWithoutTaxSum',
  }),
  AmountColumn({
    title: '月度已确认不含税收入合计',
    dataIndex: 'monthlyConfirmedIncomeWithoutTaxSum',
  }),
]
export default ALL_COLUMNS
