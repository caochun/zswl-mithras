import { Button, Page, Table } from '@zswl/components'
import { getQuery, observer } from '@zswl/admin'
import Store from './store'
import { useMemo } from 'react'
import { getTableColumns } from '@/utils'
import ALL_COLUMNS from './Column'
import AddModal from './AddModal'
import { saveServer } from '@/utils'
import { TableExportAction as TableExport } from '@/components/Actions'

/**
 * 汇率设置页面主组件
 * 展示汇率列表，支持新增、编辑、删除操作
 * @param {Object} path - 路由路径
 */
function ExchangeRateIndex({ query }) {
  const store = useMemo(() => new Store(), [])
  const { year, month } = query
  const isFormApproval = getQuery('typeId') === 'approval'
  /**
   * 构建表格列配置
   * 包含搜索、操作按钮等功能
   */
  const columns = useMemo(() => {
    return getTableColumns(
      ALL_COLUMNS,
      ['年份', '月份', '币种', '汇率', '汇率日期', '更新时间'],
      true
    )
  }, [])

  return (
    <Page params={{ year, month }} store={store}>
      <Table
        store={store.table}
        editable={false}
        actions={[
          <Button.Add type="primary" onClick={() => store.createModal.open()}>
            新增
          </Button.Add>,
        ]}
        scroll={{ x: 'auto' }}
        // extra={[<TableExport table={store.table} />]}
        resizable
        columnsFilter={'budget_exchangeRate_1'}
        onFilter={(key, val) => saveServer('budget_exchangeRate_1', val)}
        columns={[
          ...columns,
          {
            title: '操作',
            dataIndex: 'action',
            width: 150,
            fixed: 'right',
            actions: (record, index) => [
              {
                name: '编辑',
                onClick: () => {
                  store.createModal.open(record)
                },
              },
              {
                name: '删除',
                confirm: !(isFormApproval && record.currency === 'USD'),
                disabled: isFormApproval && record.currency === 'USD',
                onClick: () => {
                  store.deleteRecord(record.id)
                },
              },
            ],
          },
        ]}
      />
      <AddModal store={store} />
    </Page>
  )
}

export default observer(ExchangeRateIndex)
