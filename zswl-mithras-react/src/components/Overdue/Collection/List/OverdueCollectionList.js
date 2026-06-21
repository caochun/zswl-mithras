import { Button, Page, Table } from '@zswl/components'
import { observer } from '@zswl/admin'
import store from './store'
import { useMemo } from 'react'
import { getTableColumns, saveServer } from '@/utils'
import ALL_COLUMNS from '../Column'
import { PageListDownloadAction as PageListDown } from '@/components/Actions'

function OverdueCollectionList({ path }) {
  const columns = useMemo(() => {
    return getTableColumns(
      ALL_COLUMNS,
      [
        {
          title: '客户名称',
          search: true,
          actions: ({ clientName: name, id }) => [
            {
              name,
              to: `${path}/detail/${id}`,
            },
          ],
        },
        '风险敞口',
        '逾期租金',
        '逾期罚息',
        '当前最大逾期天数',
        { title: '逾期状态', search: true },
        { title: '项目主办', search: true },
        { title: '业务部门', search: true },
        '最新进展',
        '最近跟进人',
        '最近跟进时间',
      ],
      true
    )
  }, [])

  return (
    <Page>
      <Table
        columnsFilter={'overdue_collection_1'}
        onFilter={(key, val) => saveServer('overdue_collection_1', val)}
        store={store.table}
        editable={false}
        selectable={false}
        extra={[<PageListDown module="overdueCollection" table={store.table} />]}
        scroll={{ x: 2000 }}
        resizable
        searchbar={{
          initialValues: {
            overdue: true,
          },
        }}
        columns={columns}
      />
    </Page>
  )
}

export default observer(OverdueCollectionList)
