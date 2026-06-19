import { Table, TableStore } from '@zswl/components'
import { getTableColumns, getFormColumns } from '@/utils'
import Api from '@/api/budget/pricing/ftpInterest'
import { useMemo } from 'react'
import ALL_COLUMNS from '../Column'
import { saveServer } from '@/utils'

const formNameColumns = ['选择日期']
const nameColumns = [
  {
    title: '日期',
    fixed: 'left',
  },
  '本日现金支出(元)',
  '本日现金收入(元)',
  '资金占用(元)',
  '现金FTP价格(%)',
  '现金FTP日利率(%)',
  '资金计息(元)',
  '本日票据支出(元)',
  '本日票据收入(元)',
  '票据占用(元)',
  '票据FTP价格(%)',
  '票据FTP日利率(%)',
  '票据计息(元)',
  '是否逾期',
  '当年累计计息(元)',
]
const Index = ({ ftpInterestId }) => {
  const table_columns = getTableColumns(ALL_COLUMNS(), nameColumns)
  const table_formColumns = getFormColumns(ALL_COLUMNS(), formNameColumns)

  const $table = useMemo(() => {
    return new TableStore({
      request: async (params) => {
        const data = await Api.postDetailPagelist({ ...params, ftpInterestId })
        return data
      },
    })
  }, [ftpInterestId])

  return (
    <Table
      columnsFilter="ftpInterest_detail_FTPTable"
      onFilter={(key, val) => saveServer('ftpInterest_detail_FTPTable', val)}
      columnWidth={150}
      store={$table}
      editable={false}
      searchbar={{
        searchButton: false,
        resetButton: false,
        items: table_formColumns,
      }}
      scroll={{
        x: 1600,
      }}
      columns={table_columns}
    />
  )
}

export default Index
