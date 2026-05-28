import { observer } from '@zswl/admin'
import { useMemo } from 'react'
import { Table, Page } from '@zswl/components'
import { getSearchColumns } from '@/utils'
import { ALL_COLUMNS } from './Column'
import { COMMON_COLUMNS } from '@/pages/report/Operation/Column'
import Store from './Store'
import ListDrawer from './ListDrawer'
import { saveServer } from '@/utils'

export const reportTitle = '运营待办管理表'

const Index = () => {
  const store = useMemo(() => new Store(), [])
  const { table } = store

  const searchItem = getSearchColumns(COMMON_COLUMNS, ['岗位'])
  const columns = [...ALL_COLUMNS(store)]

  return (
    <>
      <ListDrawer store={store}></ListDrawer>
      <Table
        scroll={{ x: true }}
        bordered
        extra={[
          {
            name: '导出',
            type: 'primary',
            onClick: () => store.export({ columns }),
          },
        ]}
        editable={false}
        columnsFilter={`管报_${reportTitle}`}
        onFilter={(key,val) => saveServer(`管报_${reportTitle}`,val)}
        store={table}
        searchbar={{
          items: [],
        }}
        columns={columns}
      ></Table>
    </>
  )
}

export default observer(Index)
