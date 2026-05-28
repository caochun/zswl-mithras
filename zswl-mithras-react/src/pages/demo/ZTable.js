import { Button, Page, Table } from '@zswl/components'
import { observer } from '@zswl/admin'
import store from './store'
import ALL_COLUMNS from './Column'
import { useMemo } from 'react'
import { getTableColumns, getFormColumns } from '@/utils'
import { getSearchColumns } from './utils'
import { saveServer } from '@/utils'

const formNameColumns = [
  '融资机构',
  '融资金额（元）',
  '本息收付款状态',
  '选择时间',
  '资金经理',
  '创建时间',
  '更新时间',
]
const formColumns = getSearchColumns(ALL_COLUMNS, formNameColumns)
function Index({ path }) {
  const columns = useMemo(() => {
    const nameColumns = [
      //收付款编号,融资机构,融资金额（元）,已还本金（元）,已还利息（元）,一年内到期本金（元）,本月未还金额（元）,收付款状态,审批状态,创建人,创建日期,更新日期
      {
        title: '收付款编号',
        fixed: 'left',
        actions: ({ receiptRepayCode: name, id }) => [{ name, to: `${path}/detail/${id}` }],
      },
      '融资机构',
      '融资金额（元）',
      '已还本金（元）',
      '已还利息（元）',
      '一年内到期本金（元）',
      '本月未还金额（元）',
      '本息收付款状态',
      '审批状态',
      '创建时间',
      '更新时间',
      '创建人',
    ]

    return getTableColumns(ALL_COLUMNS, nameColumns, false)
  }, [path])
  const canBatch = store.table.selectedRowKeys.length > 0
  return (
    <Table
    columnsFilter={'demo_ZTable_1'}
    onFilter={(key,val) => saveServer('demo_ZTable_1',val)}
      store={store.table}
      editable={false}
      selectable
      searchbar={formColumns}
      scroll={{
        x: 1200,
      }}
      columnWidth={180}
      columns={columns}
    />
  )
}

export default observer(Index)
