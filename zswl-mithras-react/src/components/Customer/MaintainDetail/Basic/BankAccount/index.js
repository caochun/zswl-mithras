import { Table, App } from '@zswl/components'
import { observer, getQuery } from '@zswl/admin'
import Store from './store'
import IconFont from '@/components/Icon'
import { Tooltip } from 'antd'
import BankAccountModal from './BankAccountModal'
import { useEffect, useMemo } from 'react'
import { formateCard } from '@/utils'

import { saveServer } from '@/utils'

const diffNode = (obj, type) => {
  if (type == 'mainAccount') {
    return (
      <Tooltip title={obj?.value}>
        <span style={{ color: obj?.isChange ? 'red' : '#333' }}>{obj?.value ? '是' : '否'}</span>
      </Tooltip>
    )
  }
  return (
    <Tooltip title={obj?.value}>
      <span style={{ color: obj?.isChange ? 'red' : '#333' }}>{obj?.value}</span>
    </Tooltip>
  )
}
function CustomerBankAccount({ clientType, canEditFlag, id, businessVersion, startUserId }) {
  const store = useMemo(
    () => new Store(id, businessVersion, startUserId),
    [id, businessVersion, startUserId]
  )
  return (
    <>
      <Table
              columnsFilter={'Basic_BankAccount_1'}
              onFilter={(key,val) => saveServer('Basic_BankAccount_1',val)}
        columnWidth={180}
        scroll={{ x: 1000 }}
        resizable
        extra={[
          {
            name: (
              <span>
                <IconFont type="icon-icon_add" />
                新增
              </span>
            ),
            type: 'primary',
            onClick: () => {
              store.bankAccountModal.open({ clientType })
            },
            disabled: !canEditFlag,
          },
        ]}
        store={store.bankAccount}
        columns={[
          {
            title: '是否主账号',
            dataIndex: 'mainAccount',
            render: (v, t) => {
              if (getQuery('typeId') == 'approval') {
                return diffNode(t.mainAccount, 'mainAccount')
              }
              return t.mainAccount ? '是' : '否'
            },
          },
          {
            title: '银行账户',
            dataIndex: 'accountNumber',
            render: (val) => BankAccount.Format({ value: val }),
            // render: (v, t) => {
            //   if (getQuery('typeId') == 'approval') {
            //     return diffNode(t.accountNumber, 'card')
            //   }
            //   return formateCard(t.accountNumber)
            // },
          },
          {
            title: '账户名称',
            dataIndex: 'accountName',
            render: (v, t) => {
              if (getQuery('typeId') == 'approval') {
                return diffNode(t.accountName)
              }
              return t.accountName
            },
          },
          {
            title: '开户行',
            dataIndex: 'accountBank',
            render: (v, t) => {
              if (getQuery('typeId') == 'approval') {
                return diffNode(t.accountBank)
              }
              return t.accountBank
            },
          },
          {
            title: '操作',
            fixed: 'right',
            actions(values) {
              return [
                {
                  name: '编辑',
                  onClick: store.bankAccountModal.open,
                  disabled: !canEditFlag,
                },
                {
                  name: '删除',
                  onClick: () => {
                    store.removeBankAccount(values)
                  },
                  disabled: !canEditFlag,
                },
              ]
            },
          },
        ]}
      />
      <BankAccountModal store={store} />
    </>
  )
}

export default observer(CustomerBankAccount)
