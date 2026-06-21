import { useMemo, useEffect } from 'react'
import { Button, Table, App } from '@zswl/components'
import { observer, getQuery } from '@zswl/admin'
import IconFont from '@/components/Icon'
import CreateModal from './CreateModal/ContractReceivableAccountModal'
import Store from './store'
import styles from './index.less'
import { getKeyOptionsLabelMapPlus } from '@/utils'


import { saveServer } from '@/utils'

const ContractReceiptAccount = ({ canEditFlag, baseStore }) => {
  const { bizType, leaseTypes } = baseStore
  const { isFormApproval, contractId, businessVersion } = baseStore.page.getParams()
  const baseData = baseStore.page.getData()
  const detail = isFormApproval ? baseData.newDetail : baseData.detail

  const store = useMemo(() => {
    return new Store({ isFormApproval, contractId, bizType, businessVersion })
  }, [isFormApproval, contractId, bizType, businessVersion])

  const { AccountUseEnum } = store

  const columns = useMemo(() => {
    return [
      {
        title: '收款方',
        dataIndex: 'payeeType',
        render: (value) => {
          return (
            <FiledFormat
              isChange={value?.isChange}
              value={
                getKeyOptionsLabelMapPlus('contractAccountPayeeTypeEnum')[value?.value ?? value]
              }
            />
          )
        },
      },
      {
        title: '账户名称',
        dataIndex: 'accountName',
        render: (value) => <FiledFormat value={value} />,
      },
      {
        title: '银行账号',
        dataIndex: 'accountNum',
        render: (value) => {
          return (
            <FiledFormat
              isChange={value?.isChange}
              title={BankAccount.Format({ value: value?.value ?? value })}
            />
          )
        },
      },
      {
        title: '开户行',
        dataIndex: 'accountAddress',
        render: (value) => <FiledFormat value={value} />,
      },
      {
        title: '操作',
        dataIndex: 'id',
        fixed: 'right',
        width: 150,
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
    if (contractId && bizType) {
      store.contractId = contractId
      store.$table.search({ contractId, accountUse: AccountUseEnum[bizType] })
    }
  }, [])

  return (
    <div className={styles.page}>
      <div className={styles.header}>
        <div className={styles.title}>
          {bizType === 'BL' || bizType === 'ZR' ? '卖方收款账户' : '收款账户'}
        </div>
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
        columnsFilter={'detail_ShouKuan_1'}
        onFilter={(key, val) => saveServer('detail_ShouKuan_1', val)}
        store={store.$table}
        columns={columns}
        autoRequest={false}
        resizable
        scroll={{ x: 1000 }}
      ></Table>
      <CreateModal store={store} leaseTypes={leaseTypes} projCode={detail?.projCode}></CreateModal>
    </div>
  )
}

export default observer(ContractReceiptAccount)
