import { getTableColumns } from '@/utils'
import { Page, Table, TableStore, Tabs } from '@zswl/components'
import ALL_COLUMNS from '../../Columns'
import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import recordTableApi from '@/api/blackGray/recordTableApi'
import warehouseTaskApi from '@/api/blackGray/warehouseTaskApi'
import store from './store'
import { saveServer } from '@/utils'

const ReportList = () => {
  const columns = useMemo(() => {
    const nameColumns = [
      '企业名称',
      { title: '统一社会信用代码', search: false },
      { title: '所属机构', dataIndex: 'applyOrganization' },
      '名单来源',
      '业务类型',
      '黑灰标识',
      { title: '入库时间', search: false },
      '观察期',
      { title: '申请原因', rename: '入库原因', search: false },
      '业务规模（万元）',
      '所属集团',
      {
        title: '所属集团黑灰标识',
        search: false,
        width: 160,
      },
      { title: '更新时间', dataIndex: 'updateTime' },
    ]
    return getTableColumns(ALL_COLUMNS, nameColumns, true)
  }, [])
  const table = useMemo(
    () =>
      new TableStore({
        request: async (params) => {
          const res = await recordTableApi.postRecordList({ ...params, isHistory: 0 })
          return res
        },
      }),
    []
  )
  return (
    <Table
      onFilter={(key, val) => saveServer('warehouse_search_1', val)}
      columnsFilter={'warehouse_search_1'}
      columns={columns}
      store={table}
      serial
      columnWidth={120}
    ></Table>
  )
}

const TaskList = ({ path }) => {
  const columns = useMemo(() => {
    const nameColumns = [
      {
        title: '任务编号',
        search: true,
        actions: ({ taskNum: name, id }) => [{ name, to: `${path}/detail/${id}?view=1` }],
      },
      '数据时点',
      { title: '所属机构', search: true },
      '任务类型',
      { title: '是否超时', search: true },
      '更新时间',
    ]
    return getTableColumns(ALL_COLUMNS, nameColumns, true)
  }, [])
  const table = useMemo(
    () =>
      new TableStore({
        request: async (params) => {
          return await warehouseTaskApi.postTaskList({
            ...params,
            auditStatus: [4],
            businessSource: 'INTERNAL_APPROVAL',
          })
        },
      }),
    []
  )
  return (
    <Table
      onFilter={(key, val) => saveServer('warehouse_search_2', val)}
      columnsFilter={'warehouse_search_2'}
      columns={columns}
      store={table}
      serial
      scroll={{ x: 'auto' }}
    ></Table>
  )
}
const BlackGrayWarehouseSearch = ({ path }) => {
  const items = [
    { key: 'inside', label: '报送查询', children: <ReportList path={path} /> },
    { key: 'outside', label: '任务查询', children: <TaskList path={path} /> },
  ]

  return (
    <Page>
      <Tabs
        items={items}
        type="card"
        activeKey={store.activeKey}
        onChange={store.activeKeyChange}
      ></Tabs>
    </Page>
  )
}

export default observer(BlackGrayWarehouseSearch)
