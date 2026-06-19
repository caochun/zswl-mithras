import { Page, Table } from '@zswl/components'
import { getTableColumns, getFormColumns } from '@/utils'
import { observer } from '@zswl/admin'
import { useMemo } from 'react'
import Store from './Store'
import moment from 'moment'
import ALL_COLUMNS from './Column'
import EditModal from './EditModal'
import { saveServer } from '@/utils'

const Index = ({ params: { id }, query: { receiptCode } }) => {
  const store = useMemo(() => new Store(), [])
  const table_columns = getTableColumns(ALL_COLUMNS())
  const formColumns = getFormColumns(ALL_COLUMNS(), ['日期', '成本是否已确认'])

  return (
    <Page params={{ id, receiptCode }} store={store.page}>
      <Table
        columnsFilter="ftpInterest_priceDetail_idjs"
        onFilter={(key, val) => saveServer('ftpInterest_priceDetail_idjs', val)}
        // extra={
        //   <Button type="primary" onClick={store.$editModal.open}>
        //     编辑
        //   </Button>
        // }
        store={store.$table}
        editable={false}
        searchbar={{
          items: formColumns,
        }}
        scroll={{
          x: 1500,
        }}
        columns={[
          ...table_columns,
          {
            title: '操作',
            width: 100,
            fixed: 'right',
            actions: (records) => {
              return [
                {
                  name: '编辑',
                  onClick: () => {
                    store.$editModal.open({
                      ...records,
                      recordDate: records.recordDate ? moment(records.recordDate) : undefined,
                    })
                  },
                },
              ]
            },
          },
        ]}
      />
      <EditModal store={store}> </EditModal>
    </Page>
  )
}

export default observer(Index)
