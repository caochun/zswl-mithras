import { Button, Page, Table } from '@zswl/components'
import { observer } from '@zswl/admin'
import ALL_COLUMNS from './Column'
import { useMemo } from 'react'
import { getTableColumns, getFormColumns } from '@/utils'
import ChooseMonthModal from './ChooseMonthModal'
import Store from './Store'
import { saveServer } from '@/utils'

const formNameColumns = ['融资编号', '融资渠道', '借款性质']
const formColumns = getFormColumns(ALL_COLUMNS, formNameColumns)

function FinancialPayableInterest({ path }) {
  const store = useMemo(() => {
    return new Store()
  }, [])
  const columns = useMemo(() => {
    const nameColumns = [
      {
        title: '融资编号',
        fixed: 'left',
        width: 240,
        actions: ({ financingCode, financingId, type }) => [
          { name: financingCode, to: `${path}/detail/${financingId}?type=${type}` },
        ],
      },
      '融资渠道',
      '起息日',
      '业务类型',
      '借款性质',
      '利率',
      '当年累积应付利息(元)',
      // '当年累积应付利息(税后)(元)',
      '当期应付利息(元)',
      // '当期应付利息(税后)(元)',
      '会计期间',
      '更新日期',
    ]

    return getTableColumns(ALL_COLUMNS, nameColumns, false)
  }, [path])

  return (
    <Page>
      <Table
        onFilter={(key, val) => saveServer('财务管理_还本利息列表', val)}
        actions={[
          {
            name: '计提利息',
            type: 'primary',
            onClick: store.chooseMonthModal.open,
          },
        ]}
        columnsFilter={'财务管理_还本利息列表'}
        store={store.table}
        editable={false}
        searchbar={formColumns}
        scroll={{
          x: true,
        }}
        columns={columns}
      />
      <ChooseMonthModal store={store}></ChooseMonthModal>
    </Page>
  )
}

export default observer(FinancialPayableInterest)
