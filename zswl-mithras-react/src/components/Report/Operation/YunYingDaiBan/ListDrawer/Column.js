import { InputColumn, TextAreaColumn, AmountColumn, MatchOptionColumn } from '@/components/Format'
import { hasValue } from '@/utils'
import { App } from '@zswl/components'

export const ALL_COLUMNS = [
  InputColumn({
    title: '流程类型',
    dataIndex: 'processModelType',
    render: (value) => {
      const options = App.getData().optionsType
      const processModelType = options.processModelType
      const flowTypeList = []
      processModelType.map((item) => {
        flowTypeList.push(...item.children)
      })
      return flowTypeList.find((item) => item.value === value)?.label ?? '-'
    },
  }),
  InputColumn({
    title: '流程ID',
    dataIndex: 'processInstanceId',
  }),
  InputColumn({
    title: '业务类型',
    dataIndex: 'leaseTypesDisplay',
  }),
  InputColumn({
    title: '项目编号',
    dataIndex: 'projCode',
  }),
  InputColumn({
    title: '项目名称',
    dataIndex: 'projName',
  }),
  InputColumn({
    title: '合同编号',
    dataIndex: 'contractCode',
  }),
  InputColumn({
    title: '业务部门',
    dataIndex: 'bizDeptName',
  }),
  InputColumn({
    title: '项目主办',
    dataIndex: 'projSponsorUserName',
  }),
  InputColumn({
    title: '当前审批人',
    dataIndex: 'currentAssignerName',
  }),
  InputColumn({
    title: '当前状态',
    dataIndex: 'processStatusDisplay',
  }),
  InputColumn({
    title: '退回意见',
    dataIndex: 'backRemark',
    render: (value) => {
      if (!value) return '-'
      return <div dangerouslySetInnerHTML={{ __html: value?.replace(/\n/g, '<br/>') }}></div>
    },
  }),
]
