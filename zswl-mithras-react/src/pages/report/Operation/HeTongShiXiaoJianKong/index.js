import { observer } from '@zswl/admin'
import { useMemo } from 'react'
import { Table, Page } from '@zswl/components'
import { getSearchColumns } from '@/utils'
import { ALL_COLUMNS } from './Column'
import { COMMON_COLUMNS } from '@/pages/report/Operation/Column'
import { initQueryDate, initYearQueryDate } from '@/pages/report/Operation/utils'
import ListDrawer from './ListDrawer'
import BarCharts from './BarCharts'
import Store from './Store'
import { saveServer } from '@/utils'

export const reportTitle = '合同时效监控报表'

const Index = () => {
  const store = useMemo(() => new Store(), [])
  const { table } = store

  const searchItem = getSearchColumns(COMMON_COLUMNS, ['时间', '业务部门', '业务类型', '租赁类型'])
  const columns = [
    {
      title: '业务部门',
      dataIndex: 'bizDeptName',
      actions: ({ bizDeptName, ...rest }) => {
        return [
          {
            name: bizDeptName,
            onClick: () => store.listDrawer.open(rest),
          },
        ]
      },
    },
    ...ALL_COLUMNS,
  ]

  return (
    <>
      <ListDrawer store={store}></ListDrawer>

      <Table
        scroll={{ x: true }}
        bordered
        onFilter={(key,val) => saveServer(`管报_${reportTitle}`,val)}
        extra={[
          {
            name: '导出',
            type: 'primary',
            onClick: () => store.export({ columns }),
          },
        ]}
        editable={false}
        columnsFilter={`管报_${reportTitle}`}
        store={table}
        searchbar={{
          items: searchItem,
          initialValues: {
            processStartDate: initYearQueryDate,
          },
        }}
        columns={columns}
      ></Table>
      <h4 style={{ marginTop: 20 }}> 合同时效监控图表</h4>
      <BarCharts store={store}></BarCharts>
    </>
  )
}

export default observer(Index)
