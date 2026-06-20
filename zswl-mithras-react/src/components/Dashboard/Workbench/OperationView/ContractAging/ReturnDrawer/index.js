import { observer } from '@zswl/admin'
import { Table, Drawer } from '@zswl/components'
import { getTableColumns, getSearchColumns } from '@/utils'
import { ALL_COLUMNS } from './Column'
import { operationViewColumnsFilterKey as columnsFilterKey } from '@/utils/domains/dashboard/DashboardUtilsFilterKeys'
import Api from '@/api/dashboard/contractAging'
import ExportBtn from '../../../../Export'
import TableSummary from '../../../../TableSummary'
import { useEffect } from 'react'
import { saveServer } from '@/utils'

const group = '合同退回统计'

const Index = ({ store }) => {
  const { returnDrawer, queryDate, returnDrawerTable: table } = store
  const columns = getTableColumns(ALL_COLUMNS)
  const searchItem = getSearchColumns(ALL_COLUMNS, [
    {
      title: '结束时间',
      rename: '审批通过时间',
      search: true,
    },
    '业务部门',
    '客户名称',
    '项目名称',
    '合同编号',
    '运营经办',
    '运营复核',
    '项目主办',
    '业务分类',
    '业务模式',
  ])

  return (
    <Drawer
      store={returnDrawer}
      width={'90%'}
      destroyOnClose
      extra={null}
      title={group}
      onClose={returnDrawer.close}
    >
      <Table
        extra={
          <ExportBtn tableStore={table} businessType={'DASHBOARD_OPERATION_CONTRACT_RETURN'} />
        }
        editable={false}
        columnsFilter={`${columnsFilterKey}_${group}`}
        onFilter={(key,val) => saveServer(`${columnsFilterKey}_${group}`,val)}
        scroll={{ x: true }}
        store={table}
        searchbar={{
          items: searchItem,
        }}
        columns={[...columns]}
      ></Table>
    </Drawer>
  )
}

export default observer(Index)
