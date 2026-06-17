import { FiledFormat, TextAreaEditable } from '@/components/Format'

const ALL_COLUMNS = [
  {
    title: '客户名称',
    dataIndex: 'clientId',
    width: 250,
    editable: false,
    render: (val, { clientName }) => <FiledFormat title={clientName} />,
  },
  {
    title: '客户权限类型',
    dataIndex: 'authorityLevel',
    matchOption: 'clientLevel',
    editable: false,
  },
  {
    title: '客户所属主办',
    dataIndex: 'belongSponsorId',
    render: (val, { belongSponsorName }) => <FiledFormat title={belongSponsorName} />,
    editable: false,
  },
  {
    title: '客户所属部门',
    dataIndex: 'belongDeptId',
    render: (val, { belongDeptName }) => <FiledFormat title={belongDeptName} />,
    editable: false,
  },
  {
    title: '申请人',
    dataIndex: 'applyName',
    editable: false,
  },
  {
    title: '申请人所在部门',
    dataIndex: 'applyDeptName',
    editable: false,
  },
  {
    title: '申请原因',
    dataIndex: 'applyReason',
    span: 2,
    requiredMark: true,
    editable: TextAreaEditable({ required: true }),
    render: (val) => <FiledFormat title={val} hasToolTip={false} />,
  },
]
export default ALL_COLUMNS
