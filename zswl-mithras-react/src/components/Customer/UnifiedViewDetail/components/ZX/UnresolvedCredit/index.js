import React from 'react'
import { Typography } from 'antd'
import { Table } from '@zswl/components'
import { observer } from '@zswl/admin'
import styles from './styles.less'
import { saveServer } from '@/utils'

function UnresolvedCredit({ store, id }) {
  const columns = [
    {
      title: '',
      dataIndex: 'type',
      key: 'type',
      align: 'center',
    },
    {
      title: '正常类',
      children: [
        {
          title: '账户数',
          dataIndex: 'normalAccounts',
          key: 'normalAccounts',
          align: 'center',
        },
        {
          title: '余额',
          dataIndex: 'normalAmount',
          key: 'normalAmount',
          align: 'center',
        },
      ],
    },
    {
      title: '关注类',
      children: [
        {
          title: '账户数',
          dataIndex: 'attentionAccounts',
          key: 'attentionAccounts',
          align: 'center',
        },
        {
          title: '余额',
          dataIndex: 'attentionAmount',
          key: 'attentionAmount',
          align: 'center',
        },
      ],
    },
    {
      title: '不良类',
      children: [
        {
          title: '账户数',
          dataIndex: 'nonPerformingAccounts',
          key: 'nonPerformingAccounts',
          align: 'center',
        },
        {
          title: '余额',
          dataIndex: 'nonPerformingAmount',
          key: 'nonPerformingAmount',
          align: 'center',
        },
      ],
    },
    {
      title: '合计',
      children: [
        {
          title: '账户数',
          dataIndex: 'totalAccounts',
          key: 'totalAccounts',
          align: 'center',
        },
        {
          title: '余额',
          dataIndex: 'totalAmount',
          key: 'totalAmount',
          align: 'center',
        },
      ],
    },
  ]

  return (
    <div id={id} className={styles.creditInfoContainer}>
      <Typography.Title level={5}>未结清信贷及授信信息概要</Typography.Title>
      <Table         columnsFilter={'ZX_UnresolvedCredit_1'}
        onFilter={(key,val) => saveServer('ZX_UnresolvedCredit_1',val)} store={store} bordered columns={columns} pagination={false} rowKey="key" />
    </div>
  )
}

export default observer(UnresolvedCredit)
