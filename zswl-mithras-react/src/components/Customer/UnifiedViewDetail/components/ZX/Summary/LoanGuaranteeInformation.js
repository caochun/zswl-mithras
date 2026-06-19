import React from 'react'
import { observer } from '@zswl/admin'
import { Descriptions } from '@zswl/components'
import { Typography } from 'antd'
import numeral from 'numeral'

const LoanGuaranteeInformation = () => {
  const tableColumns = [
    { title: '余额', dataIndex: 'loan_overdue', key: 'loan_overdue' },
    { title: '余额2', dataIndex: 'loan_overdue', key: 'loan_overdue' },
    { title: '其中: 被追偿余额', dataIndex: 'loan_attention', key: 'loan_attention' },
    {
      title: '其中: 关注类余额',
      dataIndex: 'guarantee_nonperforming',
      key: 'guarantee_nonperforming',
    },
    { title: '关注类余额', dataIndex: 'loan_nonperforming', key: 'loan_nonperforming' },
    { title: '不良类余额', dataIndex: 'guarantee_balance', key: 'guarantee_balance' },
    { title: '不良类余额', dataIndex: 'guarantee_attention', key: 'guarantee_attention' },
  ]

  const tableData = {
    key: '1',
    loan_balance: numeral('19944241.27').format('0,0.00'),
    loan_overdue: numeral('0.00').format('0,0.00'),
    loan_attention: numeral('0.00').format('0,0.00'),
    loan_nonperforming: numeral('0.00').format('0,0.00'),
    guarantee_balance: numeral('3600.00').format('0,0.00'),
    guarantee_attention: numeral('0.00').format('0,0.00'),
    guarantee_nonperforming: numeral('0.00').format('0,0.00'),
  }

  return (
    <div>
      <div style={{ border: '1px solid #f0f0f0', marginTop: 20 }}>
        {/* 自定义头部 */}
        <div
          style={{
            display: 'flex',
            textAlign: 'center',
            fontWeight: 'bold',
            background: '#fafafa',
            borderBottom: '1px solid #f0f0f0',
          }}
        >
          <div style={{ flex: 1, padding: '10px' }}>借贷交易</div>
          <div style={{ flex: 1, padding: '10px' }}>担保交易</div>
        </div>

        {/* Descriptions 内容 */}
        <Descriptions
          dataSource={tableData}
          items={tableColumns}
          bordered
          contentStyle={{ textAlign: 'center' }}
        />
      </div>
    </div>
  )
}

export default observer(LoanGuaranteeInformation)
