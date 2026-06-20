import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import { Table, TableStore, SearchBar } from '@zswl/components'
import { getTableColumns, saveServer } from '@/utils'
import { FounderSelect } from '@/components/Select'
import Api from '@/api/dashboard/unifiedTodo'
import { PRECESS_COLUMNS } from './Column'

// public enum TodoKeyEnum {
//   WAIT_APPROVE(1, "我收到的-待审批"),
//   APPROVE_RETURN(2, "我发起的-审批退回"),
//   WAIT_INITIATE(3, "我发起的-待发起");
const { Item } = SearchBar
// 待办
const Index = () => {
  const columns = getTableColumns(PRECESS_COLUMNS, [
    {
      title: '流程ID',
      actions({ processInstanceId, taskId, prepareId, processModelType, businessKey, type }) {
        let name = processInstanceId

        let url = ''
        if (type === 'WAIT_INITIATE') {
          url = `/process/application/detail/${prepareId}?tab=prepare&typeId=approval`
          if (['应收逾期集成/结算单'].includes(processModelType)) {
            url = `/budget/accountsReceivable?type=create&applicationId=${prepareId}`
          }
          name = prepareId
        } else if (type === 'WAIT_APPROVE') {
          url = `/process/receive/detail/${taskId}?typeId=approval&businessKey=${businessKey}&diff=taskId&tab=pending&curTab=pending`
        } else if (type === 'APPROVE_RETURN') {
          url = `/process/application/detail/${taskId}?typeId=approval&businessKey=${businessKey}&diff=taskId&tab=sendback`
        }
        return [
          {
            name,
            to: url,
          },
        ]
      },
    },
    '流程类型',
    '表单名称',
    '流程到达时间',
    '发起人',
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
          return Api.postDashboardToDoList({ ...tableParams })
        },
        onChange: (val) => {
          console.log('val', val)
        },
      }),
    []
  )
  return (
    <Table
      // rowKey={({ taskId, processInstanceId, prepareId }) =>{
      //   return `${processInstanceId}_${taskId}_${prepareId}`
      // }
      // }
      onFilter={(key, val) => saveServer('工作台_统一视图_待办', val)}
      rowKey={({ taskId, processInstanceId, prepareId }) =>
        `${processInstanceId}_${taskId}_${prepareId}`
      }
      scroll={{ x: true }}
      columns={columns}
      store={tableStore}
      // editable={false}
      columnsFilter={'工作台_统一视图_待办'}
      searchbar={{
        labelCol: { span: 6 },
        items: [
          {
            label: '流程ID',
            name: 'processInstanceId',
            placeholder: '请输入',
          },
          <Item label="发起人" name="startUserId" key="startUserId">
            <FounderSelect
              functionCode="selectfounder-3"
              params={{ job: undefined }}
            ></FounderSelect>
          </Item>
        ]
      }}
    ></Table>
  )
}

export default observer(Index)
