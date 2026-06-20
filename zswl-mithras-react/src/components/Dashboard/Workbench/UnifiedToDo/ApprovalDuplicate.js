import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import { Table, TableStore, SearchBar } from '@zswl/components'
import { FounderSelect } from '@/components/Select'
import { getTableColumns } from '@/utils'
import Api from '@/api/dashboard/unifiedTodo'
import { PRECESS_COLUMNS } from './Column'
import { saveServer } from '@/utils'

// 抄送
const { Item } = SearchBar
const Index = () => {
  const columns = getTableColumns(PRECESS_COLUMNS, [
    {
      title: '流程ID',
      actions({ processInstanceId, businessKey }) {
        return [
          {
            name: processInstanceId,
            to: `/process/receive/detail/${processInstanceId}?typeId=approval&businessKey=${businessKey}&diff=processInstanceId`,
          },
        ]
      },
    },
    {
      title: '流程类型',
      dataIndex: 'modelName',
    },
    '表单名称',
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
          return Api.postDashboardTaskMyReceiveCCList({ ...tableParams })
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
      columnsFilter={'工作台_统一视图_抄送我的'}
      onFilter={(key, val) => saveServer('工作台_统一视图_抄送我的', val)}
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
