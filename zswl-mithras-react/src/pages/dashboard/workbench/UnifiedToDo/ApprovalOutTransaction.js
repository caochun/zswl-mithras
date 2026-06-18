import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import { Table, TableStore, SearchBar } from '@zswl/components'
import { FounderSelect } from '@/components/Select'
import { getTableColumns } from '@/utils'
import Api from './api'
import { PRECESS_COLUMNS } from './Column'
import { saveServer } from '@/utils'

// 已办理
const { Item } = SearchBar
const Index = () => {
  const columns = getTableColumns(PRECESS_COLUMNS, [
    {
      title: '流程ID',
      actions({ processInstanceId, taskId, businessKey }) {
        return [
          {
            name: processInstanceId,
            to: `/process/receive/detail/${taskId}?typeId=approval&businessKey=${businessKey}&diff=taskId`,
          },
        ]
      },
    },
    '流程类型',
    '表单名称',
    '当前节点',
    '当前审批人',
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
          return Api.postDashboardToDoMyProcessFinish({ ...tableParams })
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
      columnsFilter={'工作台_统一视图_已办理'}
            onFilter={(key, val) => saveServer('工作台_统一视图_已办理', val)}
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
