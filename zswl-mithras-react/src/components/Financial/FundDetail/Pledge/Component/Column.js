import { InputNumberEditable, AmountEditable } from '@/components/Format'
import { OrgSelect } from '@/components'
import { formatPercent, amountFormat, rules, hasValue } from '@/utils'
import { ContractSelect, ProjSelect } from '.'
import { Form, Select } from '@zswl/components'
import { Input } from 'antd'

const { Item } = Form

export const getColumns = ({ financingId }) => [
  {
    title: '业务部门',
    dataIndex: 'bizDeptId',
    required: true,
    width: 150,
    element: <OrgSelect functionCode="selectorgs-groupCreditReview" />,
    rules: [rules.required()],
    render: (val, { bizDeptName }) => bizDeptName,
  },
  {
    title: '项目名称',
    dataIndex: 'projReviewId',
    required: true,
    width: 150,
    rules: [rules.required()],
    element: (
      <Item dependencies={['bizDeptId']} noStyle>
        {({ getFieldValue }) => {
          const bizDeptId = getFieldValue('bizDeptId')
          // if (!bizDeptId) {
          //   return <Select disabled placeholder="请先选择业务部门"></Select>
          // }
          return (
            <Item name="projReviewId" label="">
              <ProjSelect financingId={financingId} bizDeptId={bizDeptId} />
            </Item>
          )
        }}
      </Item>
    ),
  },
  {
    title: '合同编号',
    dataIndex: 'contractCode',
    required: true,
    width: 150,
    rules: [rules.required()],
    element: (
      <Item dependencies={['projReviewId', 'bizDeptId']} noStyle>
        {({ getFieldValue }) => {
          const projId = getFieldValue('projReviewId')
          const bizDeptId = getFieldValue('bizDeptId')
          if (!bizDeptId || !projId) {
            return <Select disabled placeholder="请先选择项目"></Select>
          }
          return (
            <Item name="contractCode" label="">
              <ContractSelect financingId={financingId} projId={projId} />
            </Item>
          )
        }}
      </Item>
    ),
    render: (val, { projName }) => projName,
  },
  {
    title: '业务类型',
    dataIndex: 'bizType',
    requiredMark: true,
    width: 150,
    matchOption: 'bizType',
    element: (
      <Item dependencies={['contractCode']} noStyle>
        {({ getFieldValue, setFieldValue }) => {
          const contractCode = getFieldValue('contractCode')
          const bizDeptId = getFieldValue('bizDeptId')
          if (!bizDeptId) {
            return <Input disabled placeholder="请先选择合同编号" />
          }
          return (
            <Item name="contractCode" label="">
              <div>233</div>
            </Item>
          )
        }}
      </Item>
    ),
  },
  {
    title: '合同金额(元)',
    dataIndex: 'contractAmount',
    width: 150,
    editable: (val) =>
      AmountEditable(val, 'serviceChargeAmount', { required: false, disabled: false }),
    render: (val) => amountFormat(formatPercent(val)),
  },
  {
    title: '合同期限',
    dataIndex: 'contractStartDate',
    requiredMark: true,
    width: 150,
    editable: () => InputNumberEditable({ required: true }),
    render: (val) => val,
  },
  {
    title: '保证金金额(元)',
    dataIndex: 'remainingUnpaidPrincipal',
    width: 150,
    editable: (val) =>
      AmountEditable(val, 'remainingUnpaidPrincipal', { required: false, disabled: true }),
    render: (val) => amountFormat(formatPercent(val)),
  },
]
