import { MatchOptionColumn } from '@/components/Format'
import { getKeyOptionsLabelMapPlus, hasValue } from '@/utils'

const ALL_COLUMNS = [
  {
    title: '客户编号',
    dataIndex: 'clientCode',
    width: 140,
  },
  {
    title: '客户名称',
    dataIndex: 'clientName',
    width: 250,
    actions({ clientName, clientId, clientType }) {
      return [
        {
          name: clientName,
          to: `/customer/maintain/detail/${clientId}?clientType=${clientType}&flag=info&typeId=create`,
        },
      ]
    },
  },
  {
    title: '业务部门名称',
    dataIndex: 'bizDeptName',
  },
  {
    title: '客户类型',
    dataIndex: 'clientType',
    render: (value) => getKeyOptionsLabelMapPlus('clientType')[value],
  },
  {
    title: '客户主办',
    dataIndex: 'sponsorName',
  },
  {
    title: '检查形式',
    dataIndex: 'checkWay',
    render: (value) => {
      return (
        <span style={['OFFSITE', 'WITHOUT_CHECK'].includes(value) ? { color: 'red' } : {}}>
          {getKeyOptionsLabelMapPlus('afterLeaseCheckWayEnum')[value] || '-'}
        </span>
      )
    },
  },
  MatchOptionColumn({
    title: '检查报告模版',
    dataIndex: 'reportType',
    matchOption: 'afterLeaseCheckReportTypeEnum',
  }),
  {
    title: '协查风控经理',
    dataIndex: 'riskManagerName',
    width: 220,
    render: (value) => value ?? '-',
  },
  {
    title: '检查日期',
    dataIndex: 'checkTime',
    render: (value, record) => {
      if (!value || record?.checkWay === 'OFFSITE') return '-'
      //在协查风控经理填写的检查日期10日后，项目经理若仍未提交租后检查报告，则检查日期字段显示为红色，
      const isOverTime = moment().diff(moment(value), 'days') > 10
      return <span style={{ color: isOverTime && 'red' }}>{value}</span>
    },
  },
  {
    title: '本次是否需要检查',
    dataIndex: 'check',
    render: (v) => {
      return typeof v === 'boolean' ? (
        hasValue(v) ? (
          '是'
        ) : (
          <span style={{ color: 'red' }}>否</span>
        )
      ) : (
        '-'
      )
    },
  },
  {
    title: '审批状态',
    // dataIndex: 'approvalStatus',
    render: ({ approvalStatus, checkWay }) => {
      if (checkWay === 'WITHOUT_CHECK') return '-'
      return getKeyOptionsLabelMapPlus('commonProcessStatus')[approvalStatus] || '-'
    },
  },
]
export default ALL_COLUMNS
