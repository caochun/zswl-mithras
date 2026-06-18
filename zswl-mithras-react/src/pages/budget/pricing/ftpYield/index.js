import { Page, Table, TableStore, ModalStore, PageStore, DatePicker } from '@zswl/components'
import { useEffect, useMemo, useRef, useState } from 'react'
import ALL_COLUMNS from './Column'
import { getTableColumns, saveServer } from '@/utils'
import { TableExportAction as TableExport } from '@/components/Actions'
import ftpYieldApi from '@/api/budget/ftpYield/ftpYieldApi'
import { Summary } from '@/components/Table'

const Index = ({ pathname }) => {
  const columns = getTableColumns(
    ALL_COLUMNS,
    [
      {
        title: '融资编号',
        actions: ({ fundFinancingId: id, financingCode: name, financingType }) => [
          {
            name,
            to: `${pathname}/detail/${id}?financingType=${financingType}`,
          },
        ],
        search: true,
      },
      '融资机构',
      '融资金额（元）',
      'FTP 收益率（%）',
      '当月FTP收益（元）',
      '当年累计FTP收益（元）',
      '资金主办',
      '更新日期',
    ],
    true
  )
  const [sumData, setSumData] = useState({})
  const getSum = async (data) => {
    const res = await ftpYieldApi.postInfoCount(data)
    setSumData(res)
  }

  const $table = useMemo(() => {
    return new TableStore({
      request: async (params) => {
        const data = await ftpYieldApi.postInfoList(params)
        await getSum(params)
        return data
      },
    })
  }, [])

  return (
    <Page>
      <Table
        serial={{ width: 100 }}
        columnsFilter="pricing_ftpYield_1"
        onFilter={(key, val) => saveServer('pricing_ftpYield_1', val)}
        store={$table}
        editable={false}
        scroll={{ x: 'auto' }}
        extra={[
          <TableExport
            table={[{ tableStore: $table, otherData: [{ ...sumData, financingCode: '合计' }] }]}
            otherExcelProps={{ fileName: 'FTP收益率表' }}
          />,
        ]}
        columns={columns}
        // searchbar={{
        //   items: [
        //     {
        //       label: '日期',
        //       dataIndex: 'date',
        //       element: <DatePicker />,
        //       itemProps: {
        //         transform: (val) => {
        //           return val.format('YYYY-MM-DD')
        //         },
        //       },
        //     },
        //   ],
        // }}
        summary={() => {
          return <Summary columns={$table.getOptimizedColumns()} sumData={sumData}></Summary>
        }}
      />
    </Page>
  )
}
export default Index
