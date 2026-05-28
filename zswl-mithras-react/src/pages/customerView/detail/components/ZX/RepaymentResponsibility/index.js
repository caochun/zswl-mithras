import React from 'react'
import { Typography } from 'antd'
import { Table } from '@zswl/components'
import { observer } from '@zswl/admin'
import { saveServer } from '@/utils'

function RepaymentSummary({ id }) {
  const mockData = [
    {
      key: '1',
      liabilityType: '保证人/反担保人',
      pursuedAccounts: 8,
      pursuedBalance: 0,
      remainingBalance: 0,
      otherAccounts: 0,
      otherBalance: 0,
      attentionBalance: 65169.67,
      nonPerformingBalance: 0,
    },
    {
      key: '2',
      liabilityType: '合计',
      pursuedAccounts: 11,
      pursuedBalance: 0,
      remainingBalance: 0,
      otherAccounts: 0,
      otherBalance: 0,
      attentionBalance: 94169.69,
      nonPerformingBalance: 0,
    },
  ]

  const columns = [
    {
      title: '责任类型',
      dataIndex: 'liabilityType',
      key: 'liabilityType',
      align: 'center',
    },
    {
      title: '被追偿业务',
      children: [
        {
          title: '还款责任金额',
          dataIndex: 'pursuedAccounts',
          key: 'pursuedAccounts',
          align: 'center',
        },
        {
          title: '账户数',
          dataIndex: 'pursuedBalance',
          key: 'pursuedBalance',
          align: 'center',
        },
        {
          title: '余额',
          dataIndex: 'remainingBalance',
          key: 'remainingBalance',
          align: 'center',
        },
      ],
    },
    {
      title: '其他借贷交易',
      children: [
        {
          title: '账户数',
          dataIndex: 'otherAccounts',
          key: 'otherAccounts',
          align: 'center',
        },
        {
          title: '余额',
          dataIndex: 'otherBalance',
          key: 'otherBalance',
          align: 'center',
        },
        {
          title: '关注类余额',
          dataIndex: 'attentionBalance',
          key: 'attentionBalance',
          align: 'center',
        },
        {
          title: '不良类余额',
          dataIndex: 'nonPerformingBalance',
          key: 'nonPerformingBalance',
          align: 'center',
        },
      ],
    },
  ]
  return (
    <div id={id}>
      <Typography.Title level={5}>相关还款责任信息概要</Typography.Title>
      <Table columnsFilter={'ZX_RepaymentResponsibility_1'}
              onFilter={(key,val) => saveServer('ZX_RepaymentResponsibility_1',val)} bordered columns={columns} dataSource={mockData} pagination={false} rowKey="key" />
    </div>
  )
}

export default observer(RepaymentSummary)
