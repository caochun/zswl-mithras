import { Button, Page, Table } from '@zswl/components'
import { observer } from '@zswl/admin'
import store from './store'
import { getTableColumns } from '@/utils'
import ALL_COLUMNS from './Column'
import { useMemo } from 'react'
import AddModal from './AddModal'
import { saveServer } from '@/utils'

function Index({ path }) {
  const { rows } = store.table.getSelected()
  const hasSelected = rows.length === 1
  const canClose = hasSelected && rows.every((item) => ['NEW'].includes(item.status))

  const columns = useMemo(() => {
    const nameColumns = [
      {
        title: '账龄截止日',
        actions: ({ deadline: name, id }) => [{ name, to: `${path}/detail/${id}` }],
      },
      '核算组织名称',
      '状态',
      '创建用户',
      '创建时间',
    ]

    return getTableColumns(ALL_COLUMNS, nameColumns, true)
  }, [])
  return (
    <Page>
      <Table
        store={store.table}
        editable={false}
        selectable
        actions={[
          <Button.Add onClick={store.add} key="add">
            新增
          </Button.Add>,
          <Button onClick={store.delete} key="close" disabled={!canClose}>
            关闭
          </Button>,
          <Button onClick={store.complete} key="complete" disabled={!canClose}>
            完成
          </Button>,
        ]}
        scroll={{ x: 'auto' }}
        resizable
        columnsFilter={'budget_businessAgingTable_1'}
        onFilter={(key,val) => saveServer('budget_businessAgingTable_1',val)}
        columns={columns}
      />
      <AddModal store={store} />
    </Page>
  )
}

export default observer(Index)
