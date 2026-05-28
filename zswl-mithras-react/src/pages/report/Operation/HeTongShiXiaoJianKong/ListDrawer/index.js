import { observer } from '@zswl/admin'
import { Drawer, Table } from '@zswl/components'
import { getSearchColumns } from '@/utils'
import { ALL_COLUMNS } from './Column'
import { COMMON_COLUMNS } from '@/pages/report/Operation/Column'
import { initQueryDate, initYearQueryDate } from '@/pages/report/Operation/utils'
import { reportTitle } from '../index'
import { saveServer } from '@/utils'

const Index = ({ store }) => {
  const { listDrawerTable, listDrawer } = store

  const columns = ALL_COLUMNS
  const searchItem = getSearchColumns(COMMON_COLUMNS, ['时间', '业务部门', '业务类型', '租赁类型'])

  return (
    <Drawer
      store={listDrawer}
      width={'90%'}
      destroyOnClose
      extra={null}
      title={`${reportTitle}-明细`}
      onClose={listDrawer.close}
    >
      <Table
        scroll={{ x: true }}
        bordered
        extra={[
          {
            name: '导出',
            type: 'primary',
            onClick: () => store.detailExport({ columns }),
          },
        ]}
        editable={false}
        onFilter={(key,val) => saveServer(`管报_${reportTitle}_详情`,val)}
        columnsFilter={`管报_${reportTitle}_详情`}
        store={listDrawerTable}
        searchbar={{
          initialValues: {
            processStartDate: initYearQueryDate,
          },
          items: searchItem,
        }}
        columns={[...columns]}
      ></Table>
    </Drawer>
  )
}

export default observer(Index)
