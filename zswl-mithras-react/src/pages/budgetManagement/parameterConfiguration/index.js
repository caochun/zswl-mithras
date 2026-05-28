import { Button, Page, Table,Access } from '@zswl/components'
import { observer } from '@zswl/admin'
import ALL_COLUMNS from './Column'
import { useMemo, useState } from 'react'
import { getTableColumns, getFormColumns, getSearchColumns } from '@/utils'
import AmountRange from '@/components/AmountRange'
import { TableSummary } from '@/pages/dashboard/workbench/components'
import Store from './store'
import ModalDetail from './ModalDetail'

function Index() {
  const store = useMemo(() => {
    return new Store()
  }, [])

  return (
    <Page>
      <Table
        store={store.$table}
        resizable
        scroll={false}
        // editable={false}
        // selectable
        // extra={[<PageListDown key="1" module="payment" table={store.table} />]}
        // scroll={{ x: 1200 }}
        // columnWidth={180}
        columns={[
          ...ALL_COLUMNS,
          {
            title: '操作',
            fixed: 'right',
            width: 200,
            actions(record, rowIndex) {
              return [
                {
                  name: '查看',
                  key: 'view',
                  onClick: () =>
                    store.editItem({
                      ...record,
                      isEdit: false,
                    }),
                },
                {
                  name: '编辑',
                  key: 'edit',
                  disabled: !Access.validate('newFtpParameterSettingModify'),
                  onClick: () =>
                    store.editItem({
                      ...record,
                      isEdit: true,
                    }),
                },
              ].filter(Boolean)
            },
          },
        ]}
        // summary={() => {
        //   return (
        //     <TableSummary
        //       columns={store.table.getOptimizedColumns()}
        //       sumData={sumData}
        //       initFormat={10000}
        //       startIndex={0}
        //     ></TableSummary>
        //   )
        // }}
      />
        <ModalDetail store={store}></ModalDetail>
    </Page>
  )
}

export default observer(Index)
