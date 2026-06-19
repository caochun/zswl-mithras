import { observer } from '@zswl/admin'
import { Table, Page } from '@zswl/components'
import { getFormColumns, getTableColumns, saveServer } from '@/utils'
import ALL_COLUMNS from '../../ProvisioningImpairmentColumns'
import Create from './Create'
import store from './store'

const nameColumns = ['月份', '状态', '创建日期', '创建人']
const formNameColumns = ['月份']

const Index = ({ pathname }) => {
  const columns = getTableColumns(ALL_COLUMNS({ pathname }), nameColumns)
  const formColumns = getFormColumns(ALL_COLUMNS(), formNameColumns)

  return (
    <Page store={store} params={{ pathname }}>
      <Table
        columnsFilter="budget_provisioning_1"
        onFilter={(key, val) => saveServer('budget_provisioning_1', val)}
        store={store.$table}
        columnWidth={180}
        editable={false}
        searchbar={{
          items: formColumns,
        }}
        actions={[
          {
            name: '创建',
            type: 'primary',
            onClick: () => store.createModal.open(),
          },
        ]}
        scroll={{
          x: 1000,
        }}
        columns={columns}
      />
      <Create />
    </Page>
  )
}
export default observer(Index)
