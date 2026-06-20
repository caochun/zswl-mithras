import { RenderColumn } from '@/components/Format'
import { useMemo, useEffect } from 'react'
import { Button, Page, Table } from '@zswl/components'
import { Space } from 'antd'
import { observer } from '@zswl/admin'
import IconFont from '@/components/Icon'
import CreateModal from './CreateModal'
import { BankAccount } from '@/components/Form'
import Store from './store'
import { hasPermission } from '@/utils'
import styles from './index.less'
import { saveServer } from '@/utils'

const Index = ({ id: receiptRepayId, isFormApproval, businessVersion, canEdit = true, detail }) => {
  const store = useMemo(() => {
    return new Store({})
  }, [])

  const columns = useMemo(() => {
    return [
      {
        title: '银行名称',
        width: 150,
        dataIndex: 'accountBank',
        render(val, t) {
          return <RenderColumn data={val} isCompare={isFormApproval}></RenderColumn>
        },
      },

      {
        title: '银行账号',
        dataIndex: 'accountNumber',
        width: 200,
        render: (val) => BankAccount.Format({ value: val }),
      },
      {
        title: '账户性质',
        dataIndex: 'accountType',
        render(val, t) {
          return (
            <RenderColumn
              data={val}
              isCompare={isFormApproval}
              selectEnum="baseDataBankAccountTypeEnum"
            ></RenderColumn>
          )
        },
      },
      {
        title: '开户日期',
        dataIndex: 'accountOpeningDate',
        render(val, t) {
          return <RenderColumn data={val} isCompare={isFormApproval}></RenderColumn>
        },
      },

      {
        title: '操作',
        dataIndex: 'id',
        width: 180,
        fixed: 'right',
        actions(record) {
          return [
            {
              name: '编辑',
              onClick: () => store.$createModal.open(record),
              disabled: !canEdit,
            },
            { name: '删除', onClick: () => store.remove(record), disabled: !canEdit },
          ]
        },
      },
    ]
  }, [canEdit])

  return (
    <Page
      store={store}
      params={{ businessVersion, isFormApproval, receiptRepayId, detail }}
      noStyle
    >
      <div className={styles.page}>
        <div className={styles.header}>
          <div className={styles.title}>还款账户</div>
          <Space>
            {hasPermission('fundRepayAccountAdd') && (
              <Button
                onClick={store.$createModal.open}
                type="primary"
                icon={<IconFont type="icon-icon_add" />}
                disabled={!canEdit}
              >
                新增
              </Button>
            )}
          </Space>
        </div>
        <Table columnsFilter={'detail_RefundAccount_1'}
          onFilter={(key, val) => saveServer('detail_RefundAccount_1', val)} store={store.$table} columns={columns} columnWidth={120} />
        <CreateModal store={store} receiptRepayId={receiptRepayId} />
      </div>
    </Page>
  )
}

export default observer(Index)
