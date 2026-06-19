import ftpYieldApi from '@/api/budget/ftpYield/ftpYieldApi'
import {
  FiledFormat,
  AmountColumn,
  MatchOptionColumn,
  DateColumn,
  FounderColumn,
} from '@/components/Format'
import { Select } from '@zswl/components'

const ALL_COLUMNS = [
  { title: '融资编号', dataIndex: 'financingCode', search: true },
  {
    title: '融资机构',
    dataIndex: 'organizationId',
    search: {
      element: (
        <Select
          getPopupContainer={() => document.body}
          options={(organizationName) => ftpYieldApi.postOrganizationList({ organizationName })}
          allowClear
          fieldNames={{ label: 'organizationName', value: 'id' }}
        />
      ),
    },
    render: (val, { organizationName }) => organizationName?.join('、') || '-',
  },
  AmountColumn({ title: '融资金额（元）', dataIndex: 'financingAmount' }),
  AmountColumn({ title: 'FTP 收益率（%）', dataIndex: 'ftpYieldRate' }),
  AmountColumn({ title: '当月FTP收益（元）', dataIndex: 'ftpIncomeCurrentMonth' }),
  AmountColumn({ title: '当年累计FTP收益（元）', dataIndex: 'ftpIncomeCurrentYear' }),
  FounderColumn({
    title: '资金主办',
    dataIndex: 'fundManagerId',
    renderField: 'fundManagerName',
    params: { job: 'moneymanager' },
    // functionCode,
    search: true,
  }),
  DateColumn({ title: '更新日期', dataIndex: 'updateTime' }),
  { title: '合计', dataIndex: 'total' },

  { title: '日期', dataIndex: 'interestDate' },
  AmountColumn({ title: '本日剩余本金(元)', dataIndex: 'remainingPrincipal' }),
  AmountColumn({
    title: 'FTP收益日利率(%)',
    dataIndex: 'ftpYieldRateDay',
    precision: 4,
    needSmallNumber: false,
  }),
  AmountColumn({ title: 'FTP收益(元)', dataIndex: 'ftpIncome' }),
  AmountColumn({ title: '当年累计FTP收益(元)', dataIndex: 'ftpIncomeCurrentYear' }),
]

export default ALL_COLUMNS
