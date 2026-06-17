import { observer } from '@zswl/admin'
import { Table, Page } from '@zswl/components'
import { getTableColumns, getFormColumns } from '@/utils'
import ALL_COLUMNS from '@/components/Budget/ProvisioningImpairmentColumns'
import Create from './Create'
import store from './store'
import { saveServer } from '@/utils'

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
      <Create></Create>
    </Page>
  )
}
export default observer(Index)
