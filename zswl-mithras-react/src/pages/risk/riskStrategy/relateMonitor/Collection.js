import { useMemo } from 'react'
import { Table, TableStore, Page } from '@zswl/components'
import { observer } from '@zswl/admin'
import { getTableColumns, getFormColumns } from '@/utils'
import ALL_COLUMNS from '@/components/Risk/RelateMonitorColumns'
import Api from '@/api/risk/relatedTransaction'
import { saveServer } from '@/utils'

function Index({ pullSelect }) {
  const $table = useMemo(() => {
    return new TableStore({
      request: async (params) => {
        const data = await Api.postCollectionList(params)
        return data
      },
    })
  }, [])

  const columns = useMemo(() => {
    const nameColumns = [
      {
        title: '关联方名称',
        rename: '关联方名称/客户名称',
      },
      '合同编号',
      '关联方关系类型',
      '关联关系说明',
      '现金流编号',
      '交易金额',
      '交易日期',
    ]
    return getTableColumns(ALL_COLUMNS(pullSelect), nameColumns)
  }, [pullSelect])

  const formColumns = useMemo(() => {
    const formNameColumns = ['关联方名称', '合同编号', '交易金额', '交易日期']
    return getFormColumns(ALL_COLUMNS(pullSelect), formNameColumns)
  }, [pullSelect])

  return (
    <Page>
      <Table
        columnsFilter={'riskStrategy_relateMonitor_Collection'}
        onFilter={(key, val) => saveServer('riskStrategy_relateMonitor_Collection', val)}
        scroll={{ x: 1500 }}
        store={$table}
        editable={false}
        searchbar={{
          labelCol: { span: 6 },
          items: formColumns,
        }}
        actions={[]}
        columns={[...columns]}
      />
    </Page>
  )
}

export default observer(Index)
