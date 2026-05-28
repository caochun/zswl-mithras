import { Input } from 'antd'
import { FiledFormat } from '@/components/Format'
import { formatPercent, amountFormat } from '@/utils'
import { Select, Form } from '@zswl/components'
import ProjectAllocateList from './ProjectAllocateList'
const ALL_COLUMNS = () => {
  return [
    {
      title: '合同编号',
      dataIndex: 'contractCode',
      editable: false,
    },
    {
      title: '客户名称',
      dataIndex: 'clientName',
      editable: false,
      render: (val) => <FiledFormat title={val} />,
    },
    {
      title: '剩余可用额度(元)',
      dataIndex: 'remainAvailableQuota',
      editable: false,
      formTooltip:
        '剩余可用额度=项目批复金额 - sum合同金额 + if（额度可循环，sum（已核销的本金、首期租金），0）',
      render: (val) => amountFormat(formatPercent(val)),
    },
    {
      title: '项目名称',
      dataIndex: 'projName',
      editable: false,
      render: (val) => <FiledFormat title={val} />,
    },
    {
      title: '项目编号',
      dataIndex: 'projCode',
      editable: false,
      render: (val) => <FiledFormat title={val} />,
    },
    {
      title: '业务类型',
      dataIndex: 'bizType',
      editable: false,
      matchOption: 'projEstablishBizType',
    },
    {
      title: '租赁类型',
      dataIndex: 'leaseType',
      editable: false,
      matchOption: 'leaseType',
    },
    {
      title: '风控行业分类',
      dataIndex: 'riskControlIndustryClassify',
      editable: false,
      matchOption: 'riskControlIndustryClassify',
    },
    {
      title: '项目分类',
      dataIndex: 'projItem',
      editable: false,
      matchOption: 'projectClassify',
    },
    {
      title: '项目来源',
      dataIndex: 'projSource',
      editable: false,
      matchOption: 'projSourceType',
    },
    {
      title: '资金用途',
      dataIndex: 'fundsPurpose',
      editable: false,
      render: (val) => <FiledFormat title={val} />,
    },
    {
      title: '项目背景',
      dataIndex: 'projBackground',
      span: 2,
      editable: false,
      // render: textAreaShow,
      render: (val) => <FiledFormat title={val} hasToolTip={false} />,
    },
    {
      title: '备注',
      dataIndex: 'remark',
      span: 2,
      editable: {
        element: <Input.TextArea />,
      },
      // render: textAreaShow,
      render: (val) => <FiledFormat title={val} hasToolTip={false} />,
    },
    {
      title: '项目主办',
      dataIndex: 'projSponsorUserId',
      editable: false,
      render: (val, { projSponsorUserName }) => <FiledFormat title={projSponsorUserName} />,
    },
    {
      title: '项目协办',
      dataIndex: 'projCosponsorUserIds',
      editable: false,
      render: (val, { projCosponsorUserNames }) => (
        <FiledFormat title={projCosponsorUserNames?.join(',')} />
      ),
    },
    {
      title: '业务部门',
      dataIndex: 'bizDeptName',
      editable: false,
      render: (val) => <FiledFormat title={val} />,
    },
    {
      title: '业务部门负责人',
      dataIndex: 'bizDeptLeaderName',
      editable: false,
      render: (val) => <FiledFormat title={val} />,
    },
    {
      title: '业务分管领导',
      dataIndex: 'bizDivisionLeaderName',
      editable: false,
      render: (val) => <FiledFormat title={val} />,
    },
    {
      title: '转让方',
      dataIndex: 'assignor',
      editable: false,
      render: (val) => <FiledFormat title={val} />,
    },
    {
      title: '本次申请限额(元)',
      dataIndex: 'remainAvailableQuota',
      editable: false,
      formTooltip: '项目授信金额-存量合同金额的发生额或余额（根据项目额度是否可循环判断）',
      render: (val) => amountFormat(formatPercent(val)),
    },
    {
      title: '保理类型',
      dataIndex: 'factoringType',
      requiredMark: true,
      editable: {
        element: <Select options="factoringType" />,
        rules: [{ required: true, message: '请选择' }],
      },
      matchOption: 'factoringType',
    },
    {
      title: '转让类型',
      requiredMark: true,
      dataIndex: 'zrType',
      editable: {
        element: <Select options="zrType" />,
        rules: [{ required: true, message: '请选择' }],
      },
      matchOption: 'zrType',
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
                  // source={source}
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
                  // source={source}
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
  ]
}

export default ALL_COLUMNS
