import { dateRangeTransform } from '@/utils/transform'
import { rangePresets, rules } from '@/utils'
import { FiledFormat } from '@/components/Format'
import { FounderSelect } from '@/components'
import { BankAccount } from '@/components/Form'
import { Columns } from '@zswl/components/es/Table'
import AmountRange from '@/components/AmountRange'
import { FormAmount } from '@/components/Form'

const ALL_COLUMNS: Columns<any> = [
  { title: '机构名称', dataIndex: 'organizationName', fixed: 'left' },
  // 开户银行,银行账号,账户性质,是否贷款账户,账户状态,开户时间,币种,账户余额,备注
  {
    title: '开户银行',
    dataIndex: 'accountBank',
    width: 300,
    search: {
      dataIndex: 'bankName',
    },
    editable: {
      element: <BankAccount />,
      required: true,
    },
  },
  {
    title: '银行账号',
    dataIndex: 'accountNumber',
    width: 280,
    render: (val) => BankAccount.Format({ value: val }),
  },
  {
    title: '户名',
    dataIndex: 'accountName',
    width: 240,
  },
  { title: '账户性质', dataIndex: 'accountType', matchOption: 'baseDataBankAccountTypeEnum' },
  { title: '是否贷款账户', dataIndex: 'isLoan', search: true, matchOption: 'yesOrNo' },
  { title: '账户状态', dataIndex: 'accountStatus', matchOption: 'baseDataBankAccountStatusEnum' },
  { title: '开户时间', dataIndex: 'openingDate' },
  { title: '币种', dataIndex: 'currency', matchOption: 'currencyType' },
  {
    title: '账户余额',
    dataIndex: 'accountBalance',
    search: {
      element: <AmountRange />,
      itemProps: {
        transform: (val) => {
          const [start, end] = val || []
          return {
            accountBalance: undefined,
            accountBalanceFrom: start && start * 10000,
            accountBalanceTo: end && end * 10000,
          }
        },
      },
    },
    width: 200,
    align: 'right',
    render: (val) => <FormAmount.Format value={val} />,
  },
  { title: '备注', dataIndex: 'remark' },
  {
    title: '创建人',
    dataIndex: 'createUserId',
    editable: {
      element: <FounderSelect params={{ job: 'moneymanager' }} />,
    },
    render: (val, record) => <FiledFormat title={record.createUserName} />,
  },
  {
    title: '创建时间',
    width: 200,
    dataIndex: 'createTime',
    dateFormat: 'yyyy-MM-DD HH:mm:ss',
    search: {
      ranges: rangePresets,
      itemProps: {
        transform: (val) => dateRangeTransform(val, 'createTimeFrom', 'createTimeTo'),
      },
    },
  },
  {
    title: '更新时间',
    width: 200,
    dataIndex: 'updateTime',
    search: {
      ranges: rangePresets,
      itemProps: {
        transform: (val) => dateRangeTransform(val, 'updateTimeFrom', 'updateTimeTo'),
      },
    },
    dateFormat: 'yyyy-MM-DD HH:mm:ss',
  },
]
export default ALL_COLUMNS
