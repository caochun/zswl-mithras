import { observer, getQuery, history } from '@zswl/admin'
import Store from './store'
import { Page, Table, Button } from '@zswl/components'
import { Space } from 'antd'
import { useMemo } from 'react'
import { BudgetProvisioningParamsConfigColumnsByType as getColumnsByType } from '@/components/Budget/BudgetEntries'

const Index = ({ params }) => {
  const store = useMemo(() => new Store(), [])
  const { id } = params
  const detail = store.page.getData()
  const { view } = getQuery()
  const readOnly = ['readOnly', 'version'].includes(view)
  const needAddConfig = ['RATING_MAPPING', 'BREACH_MAPPING'].includes(detail?.configCode)

  const enums = detail?.enums || {}

  const baseColumns = getColumnsByType({ enums, detail })
  const columns = [
    ...baseColumns,
    !readOnly &&
      needAddConfig && {
        title: '操作',
        dataIndex: 'actions',
        width: 150,
        actions: (_, record, index) => {
          return [
            {
              name: '删除',
              onClick: () => {
                store.$table.deleteRow(_.id)
              },
            },
          ]
        },
        fixed: 'right',
        width: 100,
      },
  ].filter(Boolean)
  return (
    <Page
      current={detail?.configName}
      store={store.page}
      params={{ id }}
      extra={
        <Space>
          {!readOnly && needAddConfig && (
            <Button
              type="primary"
              onClick={() => {
                store.$table.addRow()
              }}
            >
              增加
            </Button>
          )}
          {!readOnly && <Button onClick={() => history.goBack()}>取消</Button>}
          {!readOnly && (
            <Button type="primary" onClick={() => store.save()}>
              保存
            </Button>
          )}
        </Space>
      }
    >
      <Table
        columns={columns}
        store={store.$table}
        serial
        resizable
        columnWidth={120}
        scroll={{ x: 'auto' }}
        editable={!readOnly}
      />
    </Page>
  )
}

export default observer(Index)
