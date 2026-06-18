import { ClientSelect } from '@/components/Select'
import { FiledFormat } from '@/components/Format'
import { getKeyOptionsLabelMapPlus, rules } from '@/utils'
import { Select } from '@zswl/components'

import Api from './api'
import store from './store'

export const getOrgList = async (params, functionCode, isNumber) => {
  const res = await Api.getOrgList(
    {
      type: 1, //1:业务部门，2:领导层
      ...params,
    },
    {
      functionCode,
    }
  )
  const data = res.map(({ label, value, state }) => ({
    label,
    value: isNumber ? +value : value,
    state,
  }))
  return data
}

export function OrgSelect({ params, mode, functionCode = 'selectorgs-groupCreditEstablish', isNumber = true, ...rest }) {
  const { onChange, ...otherRest } = rest ?? {}

  const onSelectChange = (val) => {
    onChange?.(val)
  }

  return (
    <Select
      allowClear
      options={(val) => getOrgList({ name: val, ...params }, functionCode, isNumber)}
      placeholder="请选择！"
      mode={mode}
      onChange={onSelectChange}
      getPopupContainer={() => document.body}
      listHeight={180}
      {...otherRest}
    />
  )
}

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
    title: '项目名称',
    dataIndex: 'projName',
    width: 300,
    fixed: 'left',
    actions({ projName, id, approveStatus, materialsDesc }) {
      return [
        {
          name: projName,
          to: `/archives/otherFilingMaterials/detail/${id}?approveStatus=${approveStatus}&materialsDesc=${materialsDesc}`,
          className: 'z-single-line',
        },
      ]
    },
  },
  {
    title: '项目编号',
    dataIndex: 'projCode',
    width: 150,
  },
  {
    title: '客户名称',
    dataIndex: 'clientId',
    width: 300,
    editable: {
      element: <ClientSelect canJump={false} />,
      functionCode: 'clientlist-adjust',
    },
    render: (val, { clientName }) => <FiledFormat title={clientName} />,
  },
  {
    title: '资料类型',
    dataIndex: 'materialsDesc',
    width: 140,
  },
  {
    title: '发起时间',
    dataIndex: 'startDate',
    width: 200,
    type: 'rangePicker',
    itemProps: {
      transform: (val) => {
        const [startDataTime, endDataTime] = val || []
        return {
          startDate: undefined,
          startDateFrom: startDataTime?.format('yyyy-MM-DD'),
          startDateTo: endDataTime?.format('yyyy-MM-DD'),
        }
      },
    },
  },
  {
    title: '结束时间',
    dataIndex: 'approveDate',
    width: 200,
    type: 'rangePicker',
    itemProps: {
      transform: (val) => {
        const [startDataTime, endDataTime] = val || []
        return {
          approveDate: undefined,
          endDateFrom: startDataTime?.format('yyyy-MM-DD'),
          endDateTo: endDataTime?.format('yyyy-MM-DD'),
        }
      },
    },
  },
  {
    title: '更新时间',
    width: 200,
    dataIndex: 'updateTime',
    type: 'rangePicker',
    itemProps: {
      transform: (val) => {
        const [startDataTime, endDataTime] = val || []
        return {
          updateTime: undefined,
          updateTimeFrom: startDataTime?.format('yyyy-MM-DD 00:00:00'),
          updateTimeTo: endDataTime?.format('yyyy-MM-DD 23:59:59'),
        }
      },
    },
  },
  {
    title: '业务部门',
    dataIndex: 'bizDeptId',
    requiredMark: true,
    width: 250,
    editable: {
      element: <OrgSelect params={{ type: null }} />,
      rules: [rules.required()],
      functionCode: 'selectorgs-adjust',
    },
    render: (val, { deptName }) => <FiledFormat title={deptName} />,
  },
  {
    title: '发起人',
    dataIndex: 'createById',
    matchOption: 'otherFilingStartUseEnum',
    width: 120,
    render: (val, { createByName }) => <FiledFormat title={createByName} />,
  },
  // {
  //   title: '客户名称',
  //   dataIndex: 'clientName',
  //   width: 200,
  // },
  {
    title: '审批状态',
    dataIndex: 'approveStatus',
    matchOption: 'otherFilingApproveStatusEnum',
    width: 160,
    render: (val) => {
      return getKeyOptionsLabelMapPlus('commonProcessStatus')[val] || '-'
    },
  },
]

export default ALL_COLUMNS
