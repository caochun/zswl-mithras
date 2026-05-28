import { history, observer } from '@zswl/admin'
import { Button, Page, Table } from '@zswl/components'
import Store from './store'
import ALL_COLUMNS from './Column'
import { getTableColumns, saveServer } from '@/utils'
import { useEffect, useMemo } from 'react'
import EditModal from './EditModal'

/**
 * 应收账款列表页面组件
 */
const nameColumns = ['计划月份', '报送计划状态', '创建时间']
const columns = getTableColumns(ALL_COLUMNS, nameColumns, true)

const AccountsReceivable = ({ query }) => {
  const store = useMemo(() => new Store(), [])
  const { rows } = store.list.getSelected()
  const disabled = !(rows.length === 1 && rows.every((item) => ['NEW'].includes(item.reportStatus)))
  const { type, applicationId } = query
  useEffect(() => {
    if (type === 'create') {
      store.createModal.open()
    }
  }, [type, applicationId])
  return (
    <Page>
      <Table
        columnsFilter={'budget_accountsReceivable_1'}
        onFilter={(key, val) => saveServer('budget_accountsReceivable_1', val)}
        columns={[...columns]}
        store={store.list}
        editable={false}
        selectable
        columnWidth={120}
        actions={[
          <Button.Add onClick={() => store.createModal.open()} key="add">
            创建报送计划
          </Button.Add>,
          <Button onClick={store.handleClose} disabled={disabled}>
            关闭
          </Button>,
        ]}
      />
      <EditModal store={store.createModal} />
    </Page>
  )
}

export default observer(AccountsReceivable)
