import { useMemo, useEffect } from 'react'
import { Button, Table } from '@zswl/components'
import { Space } from 'antd'
import { observer } from '@zswl/admin'
import IconFont from '@/components/Icon'
import CreateModal from './CreateModal'
import RenderColumn from '@/components/RenderColumn'
import BankAccount from '@/components/Form/BankAccount'
import Store from './store'
import styles from './index.less'
import { MatchOptionColumn } from '@/components/Format'
import { saveServer } from '@/utils'

const Index = ({ financingId, isFormApproval, businessVersion, canEdit = true, detail }) => {
  const store = useMemo(() => {
    return new Store({ businessVersion, isFormApproval, financingId, detail })
  }, [businessVersion, isFormApproval, financingId, detail])

  const columns = useMemo(() => {
    return [
      {
        title: '银行名称',
        width: 300,
        dataIndex: 'accountBank',
        render(val, t) {
          return <RenderColumn data={val} isCompare={isFormApproval}></RenderColumn>
        },
      },

      {
        title: '银行账号',
        dataIndex: 'accountNumber',
        width: 250,
        render: (val) => {
          return (
            <div style={val?.isChange ? { color: 'red' } : {}}>
              {BankAccount.Format({ value: val })}
            </div>
          )
        },
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
      MatchOptionColumn({
        title: '账户类别',
        dataIndex: 'accountCategory',
        matchOption: 'fundFinancingAccountTypeEnum',
      }),
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
    <div className={styles.page}>
      <div className={styles.header}>
        <div className={styles.title}>我司还款账户</div>
        <Space>
          <Button
            onClick={store.$createModal.open}
            type="primary"
            icon={<IconFont type="icon-icon_add" />}
            disabled={!canEdit}
          >
            新增
          </Button>
        </Space>
      </div>
      <Table         columnsFilter={'detail_RefundAccount_1'}
        onFilter={(key,val) => saveServer('detail_RefundAccount_1',val)} store={store.$table} columns={columns} columnWidth={120}></Table>
      <CreateModal store={store} financingId={financingId}></CreateModal>
    </div>
  )
}

export default observer(Index)
