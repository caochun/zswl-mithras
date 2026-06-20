import { useMemo } from 'react'
import { Table, TableStore } from '@zswl/components'
import { saveServer } from '@/utils'

const BeautyTable = (props) => {
  const { store, rowKey, columns, borderTop, dataSource, ...rest } = props
  const table = useMemo(() => {
    if (store) return store
    return new TableStore({
      request: () => {
        return dataSource
      },
      pagination: false,
    })
  }, [store, dataSource])
  return (
    <Table
      columnsFilter={'Component_Table_1'}
      onFilter={(key, val) => saveServer('Component_Table_1', val)}
      resizable={false}
      store={table}
      rowKey={rowKey ?? 'id'}
      columns={columns}
      bordered
      style={{ borderTop: borderTop && '1px solid #2558e6' }}
      {...rest}
    />
  )
}

export default BeautyTable
