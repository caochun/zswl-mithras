import { AmountColumn, CustomColumn, FiledFormat, MatchOptionColumn } from '@/components/Format'
import { FounderSelect, OrgSelect } from '@/components'
import { BlackInfo } from '@/components/BlackInfo/BlackInfoEntries'

const ALL_COLUMNS = [
  CustomColumn({
    title: '客户名称',
    dataIndex: 'client',
    search: true,
    render: (val, { clientId, clientName }) => (
      <div style={{ display: 'flex', alignItems: 'center' }}>
        <FiledFormat title={clientName} />
        <BlackInfo params={{ clientId }} style={{ marginLeft: 4 }} />
      </div>
    ),
  }),
  AmountColumn({
    title: '风险敞口',
    rename: '风险敞口 (元)',
    dataIndex: 'riskExposure',
    width: 180,
  }),
  AmountColumn({
    title: '逾期租金',
    rename: '逾期租金 (元)',
    dataIndex: 'overdueRent',
    width: 180,
  }),
  AmountColumn({ title: '逾期罚息', rename: '逾期罚息 (元)', dataIndex: 'lateCharge', width: 180 }),
  { title: '当前最大逾期天数', dataIndex: 'curMaxOverdueDays', width: 180 },
  MatchOptionColumn({
    title: '逾期状态',
    dataIndex: 'overdue',
    search: true,
    matchOption: [
      { label: '逾期', value: true },
      { label: '未逾期', value: false },
    ],
  }),
  {
    title: '项目主办',
    dataIndex: 'projectSponsor',
    search: {
      element: (
        <FounderSelect
          functionCode="selectfounder-adjust"
          params={{ job: 'projmanager' }}
        ></FounderSelect>
      ),
    },
    render: (val, { projectSponsorName }) => projectSponsorName,
  },
  {
    title: '业务部门',
    dataIndex: 'bizDept',
    search: {
      element: <OrgSelect />,
      functionCode: 'overdueCollectionOrgSelect',
    },
    render: (val, { bizDeptName }) => bizDeptName,
  },
  { title: '最新进展', dataIndex: 'latestProgress' },
  { title: '最近跟进人', dataIndex: 'processPerson' },
  { title: '最近跟进时间', dataIndex: 'processTime' },

  { title: '合同编号', dataIndex: 'contractCode' },
  { title: '项目名称', dataIndex: 'projName' },
  MatchOptionColumn({
    title: '业务类型',
    dataIndex: 'bizType',
    matchOption: 'projEstablishBizType',
  }),
  AmountColumn({ title: '合同金额', dataIndex: 'contractAmount' }),
  { title: '当前逾期天数', dataIndex: 'overdueDays' },
  AmountColumn({ title: '剩余本金', dataIndex: 'remainPrincipal' }),
  AmountColumn({ title: '剩余保证金', dataIndex: 'remainDeposit' }),
  MatchOptionColumn({
    title: '合同状态',
    dataIndex: 'contractStatus',
  }),
]
export default ALL_COLUMNS
