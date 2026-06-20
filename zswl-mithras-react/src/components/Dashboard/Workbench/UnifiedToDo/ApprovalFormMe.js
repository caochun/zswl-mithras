import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import { Table, TableStore, SearchBar } from '@zswl/components'
import { FounderSelect } from '@/components/Select'
import { getTableColumns } from '@/utils'
import Api from '@/api/dashboard/unifiedTodo'
import { PRECESS_COLUMNS } from './Column'
import { saveServer } from '@/utils'

// apply 申请中
// revocation 我的撤回
// finish 审批结束

// public enum TodoProcessEnum {
//   APPROVING(1, "我发起的-申请中"),
//   WITHDRAW(2, "我发起的-我的撤回"),
//   FINISH(3, "我发起的-审批结束");
// 我发起的
const { Item } = SearchBar
const Index = () => {
  const columns = getTableColumns(PRECESS_COLUMNS, [
    {
      title: '流程ID',
      actions({ processInstanceId, businessKey, taskId, type }) {
        let url = ''
        if (type == 'APPROVING') {
          url = `/process/application/detail/${processInstanceId}?typeId=approval&businessKey=${businessKey}&diff=processInstanceId&tab=apply`
        } else if (type === 'FINISH') {
          url = `/process/application/detail/${processInstanceId}?typeId=approval&businessKey=${businessKey}&diff=processInstanceId&tab=finish`
        } else if (type === 'WITHDRAW') {
          url = `/process/application/detail/${taskId}?typeId=approval&businessKey=${businessKey}&diff=taskId&tab=revocation`
        }
        return [
          {
            name: processInstanceId,
            to: url,
          },
        ]
      },
    },
    '审批状态',
    '流程类型',
    '表单名称',
    '发起人',
    '当前节点',
    '当前审批人',
    '申请部门',
    '申请时间',
    '客户名称',
    '项目名称',
    '项目编号',
    '合同编号',
  ])

  const tableStore = useMemo(
    () =>
      new TableStore({
        pagination: {
          pageSize: 5,
        },
        request: (tableParams) => {
          return Api.postDashboardToDoMyProcessApply({ ...tableParams })
        },
      }),
    []
  )
  return (
    <Table
      rowKey={({ taskId, processInstanceId }) => `${processInstanceId}_${taskId}`}
      scroll={{ x: true }}
      columns={columns}
      store={tableStore}
      editable={false}
      columnsFilter={'工作台_统一视图_我发起的'}
      onFilter={(key,val) => saveServer('工作台_统一视图_我发起的',val)}
      searchbar={{
        labelCol: { span: 6 },
        items: [
          {
            label: '流程ID',
            name: 'processInstanceId',
            placeholder: '请输入',
          },
          // <Item label="发起人" name="startUserId" key="startUserId">
          //   <FounderSelect
          //     functionCode="selectfounder-3"
          //     params={{ job: undefined }}
          //   ></FounderSelect>
          // </Item>
        ]
      }}

    ></Table>
  )
}

export default observer(Index)
