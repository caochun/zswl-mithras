import { rules } from '@/utils'
import { FounderSelect, OrgSelect } from '@/components'
import { FiledFormat, TextAreaEditable } from '@/components/Format'
import { Select, Form } from '@zswl/components'
import { history } from '@zswl/admin'
import { DatePicker, Input } from 'antd'
import moment from 'moment'
import ProjectAllocateList from './ProjectAllocateList'

const ALL_COLUMNS = ({ source, pathname } = {}) => {
  return [
    {
      title: '合同编号',
      width: 280,
      dataIndex: 'contractCode',
      fixed: 'left',
      editable: false,
      actions({ contractCode, id }) {
        return [
          {
            name: contractCode,
            onClick: () => history.push(`${pathname}/detail/${id}?source=${source}`),
          },
        ]
      },
    },
    {
      title: '项目名称',
      dataIndex: 'projName',
      width: 300,
      editable: false,
    },
    {
      title: '业务部门',
      dataIndex: 'belongDeptId',
      render: (val, { belongDeptName }) => <FiledFormat title={belongDeptName} />,
      width: 140,
      requiredMark: true,
      editable: {
        element: <OrgSelect />,
        rules: [rules.required()],
        functionCode: 'kpiProjectAllotSelectOrgs',
      },
    },
    {
      title: '项目主办',
      dataIndex: 'sponsorUserId',
      render: (val, { sponsorUserName }) => <FiledFormat title={sponsorUserName} />,
      width: 130,
      editable: {
        element: <FounderSelect />,
        rules: [rules.required()],
        functionCode: 'kpiProjectAllotSelectFounder',
      },
    },

    {
      title: '项目主办（人员分润比）',
      dataIndex: 'sponsorUserIdWeight',
      // render: (val, { sponsorUserName }) => <FiledFormat title={sponsorUserName} />,
      width: 130,
      editable: {
        element: <FounderSelect placeholder="请选择" />,
        rules: [rules.required()],
      },
    },
    {
      title: '项目协办（人员分润比）',
      dataIndex: 'projCosponsorUserIdWeight',
      // render: (val, { sponsorUserName }) => <FiledFormat title={sponsorUserName} />,
      width: 130,
      editable: {
        element: <FounderSelect placeholder="请选择" />,
        rules: [rules.required()],
      },
    },
    {
      title: '业务部门（人员分润比）',
      dataIndex: 'bizDeptIdWeight',
      // render: (val, { belongDeptName }) => <FiledFormat title={belongDeptName} />,
      width: 140,
      requiredMark: true,
      editable: {
        element: <OrgSelect placeholder="请选择" />,
        rules: [rules.required()],
      },
    },
    {
      title: '项目类别',
      dataIndex: 'projClassify',
      matchOption: 'kpiProjectClassifyEnum',
      requiredMark: true,
      editable: {
        element: <Select options={'kpiProjectClassifyEnum'} />,
        rules: [{ required: true, message: '请选择' }],
      },
    },
    {
      title: '审批状态',
      dataIndex: 'approvalStatus',
      matchOption: 'commonProcessStatus',
      requiredMark: true,
      editable: {
        element: <Select options={'commonProcessStatus'} />,
        rules: [{ required: true, message: '请选择' }],
      },
    },

    {
      title: '项目来源',
      dataIndex: 'projSource',
      // matchOption: 'kpiProjectSourceEnum',
      matchOption: 'kpiProjectSourceDistributionEnum',
      requiredMark: true,
      editable: {
        element: <Select options={'kpiProjectSourceEnum'} />,
        rules: [{ required: true, message: '请选择' }],
      },
    },
    {
      title: '合同起始时间',
      dataIndex: 'contractStartDate',
      editable: false,
    },
    {
      title: '合同终止时间',
      dataIndex: 'contractEndDate',
      editable: false,
    },
    {
      title: '分润比',
      width: 320,
      span: 2,
      dataIndex: 'weightInfoList',
      requiredMark: true,
      editable: ({ weightInfoList }) => {
        return (
          <Form.Item dependencies={[]} noStyle>
            {() => {
              return (
                <ProjectAllocateList
                  listName="weightInfoList"
                  value={weightInfoList}
                  source={source}
                />
              )
            }}
          </Form.Item>
        )
      },

      render: (val) => <ProjectAllocateList.Detail listName="weightInfoList" value={val} />,
    },
    {
      title: '部门分润比',
      width: 320,
      span: 2,
      dataIndex: 'deptWeightInfoList',
      requiredMark: true,
      editable: ({ deptWeightInfoList }) => {
        return (
          <Form.Item dependencies={[]} noStyle>
            {() => {
              return (
                <ProjectAllocateList
                  addText="添加部门"
                  listName="deptWeightInfoList"
                  value={deptWeightInfoList}
                  source={source}
                />
              )
            }}
          </Form.Item>
        )
      },
      render: (val) => <ProjectAllocateList.Detail listName="deptWeightInfoList" value={val} />,
    },
    {
      title: '部门投放分配比',
      width: 320,
      span: 2,
      dataIndex: 'deptLaunchWeightInfoList',
      requiredMark: true,
      editable: ({ deptLaunchWeightInfoList }) => {
        return (
          <Form.Item dependencies={[]} noStyle>
            {() => {
              return (
                <ProjectAllocateList
                  addText="添加部门"
                  listName="deptLaunchWeightInfoList"
                  value={deptLaunchWeightInfoList}
                  source={source}
                />
              )
            }}
          </Form.Item>
        )
      },
      render: (val) => (
        <ProjectAllocateList.Detail listName="deptLaunchWeightInfoList" value={val} />
      ),
    },
    {
      title: '生效月份',
      dataIndex: 'effectMonth',
      requiredMark: true,
      dateFormat: 'yyyy/MM',
      editable: () => {
        return {
          element: <DatePicker picker="month" style={{ width: '100%' }} />,
          rules: [{ required: true, message: '请选择' }],
          transform: (date) => {
            return {
              year: date && moment(date).year(),
              month: date && moment(date).month() + 1,
            }
          },
        }
      },
      render: (value, record) => {
        const { effectYear } = record
        const month = moment(value).month() + 1
        return effectYear ? `${effectYear}年${month}月` : '-'
      },
    },
    {
      title: '团队长人员',
      dataIndex: 'teamLeaderId',
      requiredMark: true,
      editable: {
        element: <FounderSelect params={{ job: 'teamleader' }} />,
        rules: [rules.required()],
        functionCode: 'kpiProjectAllotSelectFounder',
      },
      render: (val, { teamLeaderName }) => <FiledFormat title={teamLeaderName} />,
    },
    {
      title: '项目交接备注',
      dataIndex: 'remark',
      requiredMark: true,
      span: 2,
      editable: TextAreaEditable({ required: true }),
      render: (val) => {
        return val ?? '-'
      },
    },
  ]
}

export default ALL_COLUMNS
