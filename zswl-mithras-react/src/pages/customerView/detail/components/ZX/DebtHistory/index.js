import React from 'react'
import { Typography } from 'antd'
import { Table } from '@zswl/components'
import { observer } from '@zswl/admin'
import styles from './styles.less'
import { saveServer } from '@/utils'

function DebtHistorySummary({ id }) {
  const mockData = [
    {
      key: '1',
      date: '2024-06',
      allAccounts: 8,
      allAmount: 65169.67,
      attentionAccounts: 0,
      attentionAmount: 0,
      nonPerformingAccounts: 0,
      nonPerformingAmount: 0,
      overdueAccounts: 8,
      overdueAmount: 65169.67,
    },
    {
      key: '2',
      date: '2024-03',
      allAccounts: 8,
      allAmount: 65169.67,
      attentionAccounts: 0,
      attentionAmount: 0,
      nonPerformingAccounts: 0,
      nonPerformingAmount: 0,
      overdueAccounts: 8,
      overdueAmount: 65169.67,
    },
    {
      key: '3',
      date: '2023-12',
      allAccounts: 8,
      allAmount: 65169.67,
      attentionAccounts: 0,
      attentionAmount: 0,
      nonPerformingAccounts: 0,
      nonPerformingAmount: 0,
      overdueAccounts: 8,
      overdueAmount: 65169.67,
    },
    {
      key: '4',
      date: '2023-09',
      allAccounts: 8,
      allAmount: 65169.67,
      attentionAccounts: 0,
      attentionAmount: 0,
      nonPerformingAccounts: 0,
      nonPerformingAmount: 0,
      overdueAccounts: 8,
      overdueAmount: 65169.67,
    },
    {
      key: '5',
      date: '2023-06',
      allAccounts: 8,
      allAmount: 65169.67,
      attentionAccounts: 0,
      attentionAmount: 0,
      nonPerformingAccounts: 0,
      nonPerformingAmount: 0,
      overdueAccounts: 8,
      overdueAmount: 65169.67,
    },
  ]

  const columns = [
    {
      title: '负债历史',
      children: [
        {
          title: '日期',
          dataIndex: 'date',
          key: 'date',
          align: 'center',
          fixed: 'left',
          width: 100,
        },
        {
          title: '全部负债',
          children: [
            {
              title: '账户数',
              dataIndex: 'allAccounts',
              key: 'allAccounts',
              align: 'center',
            },
            {
              title: '余额',
              dataIndex: 'allAmount',
              key: 'allAmount',
              align: 'center',
            },
          ],
        },
        {
          title: '关注类负债',
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
          title: '不良类负债',
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
          title: '逾期类负债',
          children: [
            {
              title: '账户数',
              dataIndex: 'overdueAccounts',
              key: 'overdueAccounts',
              align: 'center',
            },
            {
              title: '余额',
              dataIndex: 'overdueAmount',
              key: 'overdueAmount',
              align: 'center',
            },
          ],
        },
      ],
    },
  ]

  return (
    <div id={id} className={styles.creditInfoContainer}>
      <Typography.Title level={5}>负债历史汇总信息</Typography.Title>
      <Table
              columnsFilter={'ZX_DebtHistory_1'}
              onFilter={(key,val) => saveServer('ZX_DebtHistory_1',val)}
        bordered
        columns={columns}
        dataSource={mockData}
        pagination={false}
        rowKey="key"
        scroll={{ x: 'max-content' }} // 横向滚动支持
      />
    </div>
  )
}

export default observer(DebtHistorySummary)
