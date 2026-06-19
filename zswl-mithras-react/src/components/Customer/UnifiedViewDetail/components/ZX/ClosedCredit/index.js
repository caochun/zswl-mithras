import React from 'react'
import { Typography } from 'antd'
import { Table } from '@zswl/components'
import { observer } from '@zswl/admin'
import styles from './styles.less'
import { saveServer } from '@/utils'

function SettledCreditSummary({ id }) {
  const mockData = [
    {
      key: '1',
      loanType: '中长期借款',
      normalAccounts: 1,
      attentionAccounts: 0,
      nonPerformingAccounts: 0,
      totalAccounts: 1,
    },
    {
      key: '2',
      loanType: '短期借款',
      normalAccounts: 7,
      attentionAccounts: 0,
      nonPerformingAccounts: 0,
      totalAccounts: 7,
    },
    {
      key: '3',
      loanType: '合计',
      normalAccounts: 8,
      attentionAccounts: 0,
      nonPerformingAccounts: 0,
      totalAccounts: 8,
    },
  ]

  const columns = [
    {
      title: '贷款类型',
      dataIndex: 'loanType',
      key: 'loanType',
      align: 'center',
      width: 150,
    },
    {
      title: '正常类账户数',
      dataIndex: 'normalAccounts',
      key: 'normalAccounts',
      align: 'center',
    },
    {
      title: '关注类账户数',
      dataIndex: 'attentionAccounts',
      key: 'attentionAccounts',
      align: 'center',
    },
    {
      title: '不良类账户数',
      dataIndex: 'nonPerformingAccounts',
      key: 'nonPerformingAccounts',
      align: 'center',
    },
    {
      title: '合计',
      dataIndex: 'totalAccounts',
      key: 'totalAccounts',
      align: 'center',
    },
  ]

  return (
    <div id={id} className={styles.creditInfoContainer}>
      <Typography.Title level={5}>已结清信贷信息概要</Typography.Title>
      <Table         columnsFilter={'ZX_ClosedCredit_1'}
        onFilter={(key,val) => saveServer('ZX_ClosedCredit_1',val)} bordered columns={columns} dataSource={mockData} pagination={false} rowKey="key" />
    </div>
  )
}

export default observer(SettledCreditSummary)
