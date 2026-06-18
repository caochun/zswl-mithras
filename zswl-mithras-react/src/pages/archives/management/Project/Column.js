import { ClientSelect, FounderSelect, OrgSelect } from '@/components/Select'
import { MatchOptionColumn } from '@/components/Format'
import { Select, Tooltip } from 'antd'
import store from './store'

const ALL_COLUMNS = [
  {
    title: '序号',
    dataIndex: 'index',
    width: 80,
    fixed: 'left',
    render: (val, record, index) => {
      const params = store.table.getParams?.() || {}
      const page = params.pageNum ?? params.page ?? 1
      const pageSize = params.pageSize ?? 20
      return (page - 1) * pageSize + index + 1
    },
  },
  {
    title: '业务部门',
    dataIndex: 'bizDeptName',
    width: 150,
    search: {
      dataIndex: 'bizDeptId',
      element: <OrgSelect />,
    },
    render: (val) => val || '-',
  },
  {
    title: '客户名称',
    dataIndex: 'clientName',
    width: 250,
    search: {
      dataIndex: 'clientId',
      element: <ClientSelect canJump={false} functionCode="materialsdger-proj-clientList" />,
    },
    render: (val) => val || '-',
  },
  {
    title: '项目名称',
    dataIndex: 'projName',
    width: 250,
    search: true,
    render: (val) =>
      val ? (
        <Tooltip title={val} placement="topLeft">
          <span>{val}</span>
        </Tooltip>
      ) : (
        '-'
      ),
  },
  {
    title: '项目编号',
    dataIndex: 'projCode',
    width: 180,
    search: true,
    render: (val) => val || '-',
  },
  {
    title: '合同编号',
    dataIndex: 'contractCode',
    width: 250,
    search: true,
    render: (val) =>
      val ? (
        <Tooltip title={val} placement="topLeft">
          <span>{val}</span>
        </Tooltip>
      ) : (
        '-'
      ),
  },
  MatchOptionColumn({
    title: '项目分类',
    dataIndex: 'riskControlIndustryClassify',
    matchOption: 'riskControlIndustryClassify',
    width: 150,
    search: true,
  }),
  {
    title: '项目主办',
    dataIndex: 'projSponsorUserName',
    width: 120,
    search: {
      dataIndex: 'sponsorUserId',
      element: <FounderSelect params={{ job: 'projmanager' }} />,
    },
    render: (val) => val || '-',
  },
  {
    title: '档案复核人',
    dataIndex: 'materialsReReviewUserName',
    width: 120,
    search: {
      dataIndex: 'materialsReReviewUserId',
      element: (
        <FounderSelect
          functionCode="selectfounder-3"
          params={{ job: 'yunYingGuanLi,yunYingGuanLiReview' }}
        />
      ),
    },
    render: (val) => val || '-',
  },
  {
    title: '流程ID',
    dataIndex: 'processInstanceId',
    width: 180,
    search: true,
    render: (val) => val || '-',
  },
  {
    title: '是否归档超期',
    dataIndex: 'archiveOverDueFlag',
    width: 130,
    search: {
      element: (
        <Select
          allowClear
          placeholder="请选择"
          options={[
            { label: '是', value: '1' },
            { label: '否', value: '0' },
          ]}
        />
      ),
    },
    render: (val) => {
      if (val === '1') return '是'
      if (val === '0') return '否'
      return '-'
    },
  },
  {
    title: '是否补充材料超期',
    dataIndex: 'supplementDocOverdueFlag',
    width: 150,
    search: {
      element: (
        <Select
          allowClear
          placeholder="请选择"
          options={[
            { label: '是', value: '1' },
            { label: '否', value: '0' },
          ]}
        />
      ),
    },
    render: (val) => {
      if (val === '1') return '是'
      if (val === '0') return '否'
      return '-'
    },
  },
  {
    title: '全流程耗时（工作日）',
    dataIndex: 'duration',
    width: 160,
    render: (val) => val ?? '-',
  },
  {
    title: '档案管理初审耗时（工作日）',
    dataIndex: 'materialsManagerReviewDuration',
    width: 200,
    render: (val) => val ?? '-',
  },
  {
    title: '档案管理复核耗时（工作日）',
    dataIndex: 'materialsManagerReReviewDuration',
    width: 200,
    render: (val) => val ?? '-',
  },
  {
    title: '发起时间',
    dataIndex: 'createTime',
    width: 180,
    type: 'rangePicker',
    itemProps: {
      transform: (val) => {
        const [startDataTime, endDataTime] = val || []
        return {
          createTime: undefined,
          createTimeFrom: startDataTime?.format('YYYY-MM-DD'),
          createTimeTo: endDataTime?.format('YYYY-MM-DD'),
        }
      },
    },
    render: (val) => val || '-',
  },
  {
    title: '结束时间',
    dataIndex: 'endTime',
    width: 180,
    type: 'rangePicker',
    itemProps: {
      transform: (val) => {
        const [startDataTime, endDataTime] = val || []
        return {
          endTime: undefined,
          endTimeFrom: startDataTime?.format('YYYY-MM-DD'),
          endTimeTo: endDataTime?.format('YYYY-MM-DD'),
        }
      },
    },
    render: (val) => val || '-',
  },
  {
    title: '应归档日',
    dataIndex: 'dueArchiveDate',
    width: 150,
    render: (val) => val || '-',
  },
  {
    title: '项目主办提交时间',
    dataIndex: 'projectHostSubmitTime',
    width: 180,
    render: (val) => val || '-',
  },
  {
    title: '档案管理初审提交时间',
    dataIndex: 'archiveReviewSubmitTime',
    width: 180,
    render: (val) => val || '-',
  },
  {
    title: '档案管理复核提交时间',
    dataIndex: 'archiveReReviewSubmitTime',
    width: 180,
    render: (val) => val || '-',
  },
  {
    title: '档案管理初审退回次数',
    dataIndex: 'archiveReviewRejectCount',
    width: 180,
    render: (val) => val ?? '-',
  },
  {
    title: '档案管理复核退回次数',
    dataIndex: 'archiveReReviewRejectCount',
    width: 180,
    render: (val) => val ?? '-',
  },
  {
    title: '档案管理初审退回原因',
    dataIndex: 'archiveReviewRejectReason',
    width: 300,
    ellipsis: true,
    render: (val) =>
      val ? (
        <Tooltip title={val} placement="topLeft">
          <span>{val}</span>
        </Tooltip>
      ) : (
        '-'
      ),
  },
  {
    title: '档案管理复核退回原因',
    dataIndex: 'archiveReReviewRejectReason',
    width: 300,
    ellipsis: true,
    render: (val) =>
      val ? (
        <Tooltip title={val} placement="topLeft">
          <span>{val}</span>
        </Tooltip>
      ) : (
        '-'
      ),
  },
]

export default ALL_COLUMNS
