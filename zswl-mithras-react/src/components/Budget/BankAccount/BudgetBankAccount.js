import { observer } from '@zswl/admin'
import { Button, Page, Table } from '@zswl/components'
import { useMemo } from 'react'
import { getTableColumns, saveServer } from '@/utils'
import Store from './store'
import ALL_COLUMNS from './Column'
import EditModal from './EditModal'

const nameColumns = [
  '开户银行',
  '银行账号',
  '户名',
  '币种',
  '账户余额',
  '账户性质',
  '账户状态',
  '开户时间',
  '是否贷款账户',
  '创建人',
  '创建时间',
  '更新时间',
]
const columns = getTableColumns(ALL_COLUMNS, nameColumns, true)
const BankAccount = () => {
  const store = useMemo(() => new Store(), [])

  return (
    <Page>
      <Table
        columnsFilter="budget_bankAccount_1"
        onFilter={(key, val) => saveServer('budget_bankAccount_1', val)}
        columns={[
          ...columns,
          {
            title: '操作',
            dataIndex: 'action',
            width: 200,
            fixed: 'right',
            actions: [
              { name: '编辑', onClick: (record) => store.editModal.open(record) },
              { name: '删除', onClick: (record) => store.itemDelete(record) },
            ],
          },
        ]}
        store={store.list}
        editable={false}
        columnWidth={120}
        actions={[
          <Button.Add onClick={() => store.editModal.open()} key="add">
            新增
          </Button.Add>,
        ]}
      />
      <EditModal store={store.editModal} />
    </Page>
  )
}

export default observer(BankAccount)
