import { Button, Page, Table } from '@zswl/components'
import { observer } from '@zswl/admin'
import store from './store'
import { useMemo } from 'react'
import { CustomColumn } from '@/components/Format'
import { getTableColumns } from '@/utils'
import ALL_COLUMNS from './Column'
import PageListDown from '@/components/PageListDown'
import AddModal from './AddModal'
import { saveServer } from '@/utils'

function Index({ path }) {
  const columns = useMemo(() => {
    return getTableColumns(
      ALL_COLUMNS,
      [
        {
          title: '诉讼登记编号',
          search: true,
          actions: ({ code: name, id }) => [{ name, to: `${path}/detail/${id}` }],
        },
        CustomColumn({
          title: '客户名称',
          render: (val) => val?.clientName ?? '-',
          search: true,
        }),
        { title: '合同编号', search: true },
        { title: '诉讼状态', search: true },
        '创建人',
        { title: '记录时间', rename: '创建时间' },
      ],
      true
    )
  }, [])
  return (
    <Page>
      <Table
        store={store.table}
        editable={false}
        actions={[
          <Button.Add type="primary" onClick={() => store.createModal.open()}>
            新增诉讼登记
          </Button.Add>,
        ]}
        scroll={{ x: 1200 }}
        extra={[<PageListDown module={'litigationRegistration'} table={store.table} />]}
        resizable
        columnsFilter={'overdue_litigationRegistration_1'}
        onFilter={(key,val) => saveServer('overdue_litigationRegistration_1',val)}
        columns={columns}
      />
      <AddModal store={store} />
    </Page>
  )
}

export default observer(Index)
