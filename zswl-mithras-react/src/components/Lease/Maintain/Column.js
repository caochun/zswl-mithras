import { FiledFormat } from '@/components/Format'
import { FounderSelect, ClientSelect } from '@/components/Select'
import { App } from '@zswl/components'
import { Input, Tag, Space } from 'antd'

const ALL_COLUMNS = [
  {
    title: '编号',
    dataIndex: 'leaseAuditFlowNumber',
    width: 100,
    editable: {
      element: <Input></Input>,
    },
  },
  {
    title: '项目名称',
    dataIndex: 'projectName',
    width: 300,
    // render: (val) => <FiledFormat title={val} />,
  },
  {
    title: '租赁物状态',
    dataIndex: 'leaseStatus',
    matchOption: 'commonProcessStatus',
  },
  {
    title: '客户名称',
    dataIndex: 'clientId',
    width: 250,
    editable: {
      element: <ClientSelect canJump={false} />,
      functionCode: 'leaseItemManagementClientList',
    },
    render: (val, { clientName }) => clientName,
  },
  {
    title: '合同编号',
    dataIndex: 'contractCode',
    width: 280,
    span: 2,
    render: (value) => {
      const Rows = value?.map((item) => <Tag color="blue">{item}</Tag>)
      return <Space wrap>{Rows}</Space>
    },
  },
  {
    title: '项目编号',
    dataIndex: 'projectCode',
  },
  {
    title: '项目主办',
    dataIndex: 'projectOrganizerId',
    render: (val, { projectOrganizer }) => <FiledFormat title={projectOrganizer} />,
    editable: false,
    editable: {
      element: <FounderSelect />,
      functionCode: 'leaseItemManagementFounderList',
    },
  },
  {
    title: '项目协办',
    dataIndex: 'projectCoOrganizerIds',
    width: 250,
    editable: (value) => ({
      element: <FounderSelect />,
      functionCode: 'leaseItemManagementFounderList',
      initialValue: (value?.projectCoOrganizer || [])?.join('、'),
    }),
    render: (val, { projectCoOrganizer }) => <FiledFormat title={projectCoOrganizer?.join('、')} />,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
  },
  {
    title: '业务类型',
    dataIndex: 'businessType',
  },
  {
    title: '租赁类型',
    dataIndex: 'leaseType',
    render: (value) => {
      const types = value?.map((item) => App.matchOption('leaseType', item)?.label)
      return <FiledFormat title={types?.join('、')} />
    },
  },
  {
    title: '承租人',
    dataIndex: 'tenant',
    render: (value) => {
      const tenants = value?.map((item) => item.clientName)
      return <FiledFormat title={tenants?.join('、')} />
    },
  },
  {
    title: '风控行业分类',
    dataIndex: 'riskControlIndustryType',
  },
  {
    title: '业务部门',
    dataIndex: 'businessDepartment',
  },
  {
    title: '业务部门负责人',
    dataIndex: 'businessDepartmentHead',
  },
]
export default ALL_COLUMNS
