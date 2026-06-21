import { Page, Table } from '@zswl/components'
import { observer } from '@zswl/admin'
import { useMemo } from 'react'
import { Button } from 'antd'
import Store from './store'
import ALL_COLUMNS from './Column'
import { getFormColumns, saveServer } from '@/utils'

const formNameColumns = ['融资编号', '合同编号']
const columns = ALL_COLUMNS
const formColumns = getFormColumns(ALL_COLUMNS, formNameColumns)
function FinancialProperty() {
  const store = useMemo(() => {
    return new Store({})
  }, [])
  return (
    <Page store={store}>
      <Table
        columnWidth={180}
        store={store.$table}
        editable={false}
        searchbar={{
          labelCol: { span: 6 },
          items: [...formColumns],
        }}
        extra={[
          <Button key="1" onClick={store.export} type="primary">
            导出
          </Button>,
        ]}
        scroll={{
          x: 1500,
        }}
        columns={[...columns]}
        columnsFilter={'policyManage_list'}
        onFilter={(key, val) => saveServer('policyManage_list', val)}
      />
    </Page>
  )
}

export default observer(FinancialProperty)
