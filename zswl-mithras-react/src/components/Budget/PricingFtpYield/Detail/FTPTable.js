import { Table, TableStore, Tabs } from '@zswl/components'
import { getTableColumns } from '@/utils'
import Api from '@/api/budget/ftpYield/ftpYieldApi'
import { useEffect, useMemo, useState } from 'react'
import ALL_COLUMNS from '../Column'
import { saveServer } from '@/utils'

const FTPTable = ({ dataSource, isAbs, summary = {} }) => {
  const nameColumns = [
    '日期',
    '本日剩余本金(元)',
    !isAbs && 'FTP 收益率（%）',
    !isAbs && 'FTP收益日利率(%)',
    'FTP收益(元)',
    '当年累计FTP收益(元)',
  ]
  const table_columns = getTableColumns(ALL_COLUMNS, nameColumns)

  const $table = useMemo(() => {
    return new TableStore({
      request: async (params) => dataSource,
    })
  }, [dataSource])

  return (
    <Table
      columnsFilter="ftpYield_detail_FTPTable"
      onFilter={(key, val) => saveServer('ftpYield_detail_FTPTable', val)}
      columnWidth={150}
      store={$table}
      editable={false}
      scroll={{ x: 'auto' }}
      // summary={() => <Summary columns ={$table.getOptimizedColumns()} sumData={summary} />}
      columns={table_columns}
    />
  )
}
const Index = ({ id: fundFinancingId, financingType }) => {
  const [tabs, setTabs] = useState([])
  const getList = async () => {
    const data = await Api.postRecordList({ fundFinancingId, financingType })
    setTabs(
      data.map(({ abbreviation, bodys, ftpIncomeId, count }) => ({
        label: abbreviation,
        children: <FTPTable dataSource={bodys} isAbs={data.length > 1} summary={count} />,
        key: ftpIncomeId,
      }))
    )
  }
  useEffect(() => {
    getList()
  }, [fundFinancingId])
  return (
    <>
      {tabs.length > 1 && <Tabs items={tabs} />}
      {tabs.length === 1 && tabs[0].children}
    </>
  )
}

export default Index
