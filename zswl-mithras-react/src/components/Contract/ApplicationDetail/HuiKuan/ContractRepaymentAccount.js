import { RenderColumn } from '@/components/Format'
import { useMemo, useEffect } from 'react'
import { Button, Table, App } from '@zswl/components'
import { observer } from '@zswl/admin'
import IconFont from '@/components/Icon'
import CreateModal from './CreateModal/ContractRepaymentAccountCreateModal'
import Store from './store'
import styles from './index.less'
import { formateCard } from '@/utils'
import { saveServer } from '@/utils'

const ContractRepaymentAccount = ({ canEditFlag, baseStore }) => {
  const { bizType } = baseStore
  const { isFormApproval, contractId, businessVersion } = baseStore.page.getParams()
  const store = useMemo(() => {
    return new Store({ businessVersion, isFormApproval, contractId, bizType })
  }, [businessVersion, isFormApproval, contractId, bizType])

  const { AccountUseEnum } = store

  const columns = useMemo(() => {
    return [
      {
        title: '回款方式',
        dataIndex: 'repayWay',
        render: (v, t) => {
          return (
            <RenderColumn data={v} isCompare={isFormApproval} selectEnum="rePayType"></RenderColumn>
          )
        },
      },
      {
        title: '账户名称',
        dataIndex: 'accountName',
        render: (v, t) => {
          return <RenderColumn data={v} isCompare={isFormApproval}></RenderColumn>
        },
      },
      {
        title: '银行账号',
        dataIndex: 'accountNum',
        width: 240,
        render: (v, t) => {
          return (
            <RenderColumn
              data={v}
              isCompare={isFormApproval}
              formatText={(val) => formateCard(val)}
            ></RenderColumn>
          )
        },
      },
      {
        title: '开户行',
        dataIndex: 'accountAddress',
        render: (v, t) => {
          return <RenderColumn data={v} isCompare={isFormApproval}></RenderColumn>
        },
      },
      {
        title: '操作',
        dataIndex: 'id',
        width: 220,
        actions(record) {
          return [
            {
              name: '编辑',
              onClick: () => store.$createModal.open(record),
              disabled: !canEditFlag,
            },
            { name: '删除', onClick: () => store.remove(record), disabled: !canEditFlag },
          ]
        },
      },
    ]
  }, [canEditFlag])

  useEffect(() => {
    if ((contractId, bizType)) {
      store.$table.search({ contractId, accountUse: AccountUseEnum[bizType] })
    }
  }, [contractId, bizType])

  useEffect(() => {
    return () => {
      App.resetStore(store)
    }
  }, [])

  return (
    <div className={styles.page}>
      <div className={styles.header}>
        <div className={styles.title}>{bizType === 'BL' ? '保理回款账户' : '回款账户'}</div>
        <Button
          type="primary"
          onClick={() => store.$createModal.open()}
          icon={<IconFont type="icon-icon_add" />}
          disabled={!canEditFlag}
        >
          新增
        </Button>
      </div>
      <Table
              columnsFilter={'detail_GaiSuanZuJin_1'}
              onFilter={(key,val) => saveServer('detail_GaiSuanZuJin_1',val)}
      store={store.$table} columns={columns} autoRequest={false} resizable></Table>
      <CreateModal store={store}></CreateModal>
    </div>
  )
}

export default observer(ContractRepaymentAccount)
