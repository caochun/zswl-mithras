import { observer } from '@zswl/admin'
import { Table } from '@zswl/components'
import { getTableColumns, getSearchColumns } from '@/utils'
import { ALL_COLUMNS } from './Column'
import Api from '@/api/dashboard/customerOverview'
import { columnsFilterKey } from '../../Config'
import styles from './index.less'
import ExportBtn from '../../../../../Export'
import TableSummary from '../../../../../TableSummary'
import { saveServer } from '@/utils'

// 已结清客户
const Index = ({ group }) => {
  const searchItem = getSearchColumns(ALL_COLUMNS, [
    '客户名称',
    '所属部门',
    '所属主办',
    // '项目名称',
    // '合同编号',
  ])

  const table = Table.useStore({
    request: async (params) => {
      const { records, sumData } = await Api.postDashboardClientOverviewSettledList(params)
      return records
    },
  })

  return (
    <Table
    columnsFilter={'CustomListDrawer_SettledList_1'}
    onFilter={(key,val) => saveServer('CustomListDrawer_SettledList_1',val)}
      extra={<ExportBtn tableStore={table} businessType={'DASHBOARD_CLIENT_OVERVIEW_SETTLED'} />}
      rowKey={'clientId'}
      className={styles.myTable}
      editable={false}
      scroll={{ x: true }}
      store={table}
      searchbar={{
        items: searchItem,
      }}
      columns={getTableColumns(ALL_COLUMNS, ['客户名称', '所属部门', '所属主办'])}
      expandable={{
        expandedRowRender: (record) => {
          return (
            <Table
            columnsFilter={'CustomListDrawer_SettledList_2'}
            onFilter={(key,val) => saveServer('CustomListDrawer_SettledList_2',val)}
              pagination={false}
              className={styles.myTable}
              dataSource={record.projReviewList}
              columns={getTableColumns(ALL_COLUMNS, ['项目名称'])}
              expandable={{
                expandedRowRender: (record) => {
                  return (
                    <Table
                    columnsFilter={'CustomListDrawer_SettledList_3'}
                    onFilter={(key,val) => saveServer('CustomListDrawer_SettledList_3',val)}
                      pagination={false}
                      className={styles.myTable}
                      dataSource={record.contractList}
                      columns={getTableColumns(ALL_COLUMNS, [
                        '合同编号',
                        '业务类型',
                        '所属部门',
                        '所属主办',
                        '项目协办',
                      ])}
                    ></Table>
                  )
                },
              }}
            ></Table>
          )
        },
      }}
    ></Table>
  )
}

export default observer(Index)
