import React from 'react'
import { Typography } from 'antd'
import { Table } from '@zswl/components'
import { observer } from '@zswl/admin'
import styles from './styles.less'
import { saveServer } from '@/utils'

function CreditSummary({ id }) {
  const mockData = [
    {
      key: '1',
      nonRevolvingTotal: 1300900,
      nonRevolvingUsed: 848200,
      nonRevolvingRemaining: 452700,
      revolvingTotal: 600000,
      revolvingUsed: 600000,
      revolvingRemaining: 0,
    },
  ]

  const columns = [
    {
      title: '',
      children: [
        {
          title: '非循环信用额度',
          children: [
            {
              title: '总额',
              dataIndex: 'nonRevolvingTotal',
              key: 'nonRevolvingTotal',
              align: 'center',
            },
            {
              title: '已用额度',
              dataIndex: 'nonRevolvingUsed',
              key: 'nonRevolvingUsed',
              align: 'center',
            },
            {
              title: '剩余可用额度',
              dataIndex: 'nonRevolvingRemaining',
              key: 'nonRevolvingRemaining',
              align: 'center',
            },
          ],
        },
        {
          title: '循环信用额度',
          children: [
            {
              title: '总额',
              dataIndex: 'revolvingTotal',
              key: 'revolvingTotal',
              align: 'center',
            },
            {
              title: '已用额度',
              dataIndex: 'revolvingUsed',
              key: 'revolvingUsed',
              align: 'center',
            },
            {
              title: '剩余可用额度',
              dataIndex: 'revolvingRemaining',
              key: 'revolvingRemaining',
              align: 'center',
            },
          ],
        },
      ],
    },
  ]

  return (
    <div id={id} className={styles.creditInfoContainer}>
      <Typography.Title level={5}>授信协议汇总信息</Typography.Title>
      <Table         columnsFilter={'ZX_CreditAgreement_1'}
              onFilter={(key,val) => saveServer('ZX_CreditAgreement_1',val)} bordered columns={columns} dataSource={mockData} pagination={false} rowKey="key" />
      <Typography.Text style={{ marginTop: 8, display: 'block', color: '#999' }}>
        说明：由于存在在线授信额度的控制，剩余可用额度无法准确计算，需整合各授信明细信息进行估算。
      </Typography.Text>
    </div>
  )
}

export default observer(CreditSummary)
