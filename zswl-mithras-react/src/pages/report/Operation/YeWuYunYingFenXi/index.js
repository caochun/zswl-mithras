import { observer } from '@zswl/admin'
import { Children, useEffect, useMemo } from 'react'
import { Table, Page } from '@zswl/components'
import { getSearchColumns, getTableColumns } from '@/utils'
import { ALL_COLUMNS } from './Column'
import { COMMON_COLUMNS } from '@/pages/report/Operation/Column'
import Store from './Store'
import ListDrawer from './ListDrawer'
import BarCharts from './BarCharts'
import { initQueryDate, initYearQueryDate } from '@/utils/report'
import { saveServer } from '@/utils'

export const reportTitle = '业务运行分析表'

const Index = () => {
  const store = useMemo(() => new Store(), [])
  const { table } = store

  useEffect(() => {
    store.getChartsData()
  }, [])

  const searchItem = getSearchColumns(COMMON_COLUMNS, [
    '流程类型',
    '时间',
    '流程状态',
    '业务部门',
    '业务类型',
    '租赁类型',
  ])

  const columns = [
    {
      title: '业务部门',
      dataIndex: 'bizDeptName',
      actions: ({ bizDeptName, ...rest }) => {
        return [
          {
            name: bizDeptName,
            onClick: () => store.listDrawer.open(rest),
            disabled: bizDeptName === '合计',
          },
        ]
      },
    },
    ...ALL_COLUMNS,
  ]

  return (
    <div>
      <ListDrawer store={store}></ListDrawer>
      <Table
        scroll={{ x: true }}
        bordered
        extra={[
          {
            name: '导出',
            type: 'primary',
            onClick: () => store.export({ columns }),
          },
        ]}
        editable={false}
        columnsFilter={`管报_${reportTitle}`}
        onFilter={(key,val) => saveServer(`管报_${reportTitle}`,val)}
        store={table}
        searchbar={{
          items: searchItem,
          initialValues: {
            processStartDate: initYearQueryDate,
          },
        }}
        columns={columns}
      ></Table>
      <h4 style={{ marginTop: 20 }}> 业务运行分析图表</h4>
      <BarCharts store={store}></BarCharts>
    </div>
  )
}

export default observer(Index)
