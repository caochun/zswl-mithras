import { Page, Table, App, SearchBar } from '@zswl/components'
import { observer } from '@zswl/admin'
import PageListDown from '@/components/PageListDown'
import { useMemo } from 'react'
import { Button } from 'antd'
import Store from './store'
import ALL_COLUMNS from './Column'
import { getFormColumns } from '@/utils'
import { saveServer } from '@/utils'

const formNameColumns = [
    '融资编号',
    '合同编号',
]
const { Item } = SearchBar
const columns = ALL_COLUMNS
const formColumns = getFormColumns(ALL_COLUMNS, formNameColumns)
function Index() {
  const { optionsType } = App.getData()
  const store = useMemo(() => {
    return new Store({})
  }, [])
  const deptIdList = useMemo(() => store.deptIdList, [store.deptIdList])
  return (
    <Page store={store}>
      <Table
        columnWidth={180}
        store={store.$table}
        editable={false}
        searchbar={{
            labelCol: { span: 6 },
            items: [...formColumns]
        }}
        extra={[<Button key="1" onClick={store.export} type='primary'>导出</Button>]}
        scroll={{
          x: 1500,
        }}
        columns={[...columns]}
        columnsFilter={'policyManage_list'}
        onFilter={(key,val) => saveServer('policyManage_list',val)}
      />
    </Page>
  )
}

export default observer(Index)
