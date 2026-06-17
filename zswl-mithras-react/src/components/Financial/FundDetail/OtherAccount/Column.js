import BankAccount from '@/components/Form/BankAccount'
import { InputColumn, MatchOptionColumn } from '@/components/Format'

const ALL_COLUMNS = [
  InputColumn({ title: '银行名称', dataIndex: 'clientName' }),
  InputColumn({
    title: '银行账号',
    dataIndex: 'accountNum',
    element: <BankAccount.Item itemStyle={{ marginBottom: 0 }} required />,
    render: (val) => {
      return (
        <div style={val?.isChange ? { color: 'red' } : {}}>
          {BankAccount.Format({ value: val })}
        </div>
      )
    },
  }),
  MatchOptionColumn({
    title: '账户类别',
    dataIndex: 'accountCatetory',
    matchOption: 'fundFinancingAccountTypeEnum',
  }),
  InputColumn({ title: '账户性质', dataIndex: 'accountName' }),
]
export default ALL_COLUMNS
