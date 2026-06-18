import { observer } from '@zswl/admin'
import { Table, Drawer } from '@zswl/components'
import { getTableColumns, getSearchColumns } from '@/utils'
import { ALL_COLUMNS } from './Column'
import { MatchOptionColumn, InputColumn, AmountColumn, DateColumn } from '@/components/Format'
import { operationViewColumnsFilterKey as columnsFilterKey } from '@/dashboard/DashboardUtilsFilterKeys'
import Api from '@/api/dashboard/contractAging'
import { ExportBtn } from '@/components/Dashboard'
import { useEffect } from 'react'
import { saveServer } from '@/utils'

const group = '运营审批时效'

const Index = ({ store }) => {
  const { approvalDrawer, queryDate, approvalDrawerTable: table } = store
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
    '项目主办',
    '业务分类',
    '业务模式',
  ])

  return (
    <Drawer
      store={approvalDrawer}
      width={'90%'}
      destroyOnClose
      extra={null}
      title={group}
      onClose={approvalDrawer.close}
    >
      <Table
        bordered
        extra={<ExportBtn tableStore={table} businessType={'DASHBOARD_OPERATION_APPROVAL'} />}
        editable={false}
        columnsFilter={`${columnsFilterKey}_${group}`}
        onFilter={(key,val) => saveServer(`${columnsFilterKey}_${group}`,val)}
        scroll={{ x: true }}
        store={table}
        searchbar={{
          items: searchItem,
        }}
        columns={[
          ...columns,
          {
            title: '运营经办审批',
            children: [
              InputColumn({
                title: '运营经办到达时间',
                dataIndex: 'operationHandlingArrivalTime',
              }),
              InputColumn({
                title: '运营经办提交时间',
                dataIndex: 'operationHandlingSubmitTime',
              }),
              InputColumn({
                title: '运营经办耗时(工作日)',
                dataIndex: 'operationHandlingTotalTime',
              }),
            ],
          },
          {
            title: '运营复核审批',
            children: [
              InputColumn({
                title: '运营复核到达时间',
                dataIndex: 'operationReviewArrivalTime',
              }),
              InputColumn({
                title: '运营复核提交时间',
                dataIndex: 'operationReviewSubmitTime',
              }),
              InputColumn({
                title: '运营复核耗时(工作日)',
                dataIndex: 'operationReviewTotalTime',
              }),
            ],
          },
          InputColumn({
            title: '运营部总耗时(工作日)',
            dataIndex: 'operationTotalTime',
          }),
        ]}
      ></Table>
    </Drawer>
  )
}

export default observer(Index)
