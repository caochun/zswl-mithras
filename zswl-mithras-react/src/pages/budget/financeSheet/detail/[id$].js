import { observer } from '@zswl/admin'
import { Page, Table, TableStore } from '@zswl/components'
import { amountFormat, formatPercent, hasValue } from '@/utils'
import Api from '../api'
import { saveServer } from '@/utils'

const Index = ({ query: { factorDate, factorTable } }) => {
  const $table = new TableStore({
    request: async (params) => {
      const data = await Api.factorList({
        ...params,
        factorDate,
        factorTable,
      })
      return data
    },
  })
  return (
    <Page>
      <Table
        columnsFilter={'financeSheet_detail_idjs'}
                onFilter={(key,val) => saveServer('financeSheet_detail_idjs',val)}
        
        resizable
        store={$table}
        columnWidth={180}
        searchbar={{
          items: [
            {
              name: 'factorName',
              label: '名称',
            },
          ],
        }}
        columns={[
          {
            title: '名称',
            dataIndex: 'factorName',
          },
          {
            title: '表名',
            dataIndex: 'factorTable',
          },
          {
            title: '金额(元)',
            dataIndex: 'factorValue',
            align: 'right',
            render: (val) => {
              return hasValue(val) ? amountFormat(formatPercent(val)) : '-'
            },
          },
        ]}
      />
    </Page>
  )
}

export default observer(Index)
