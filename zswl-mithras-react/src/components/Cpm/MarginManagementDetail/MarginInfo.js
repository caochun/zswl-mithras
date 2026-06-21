import React, { useState } from 'react'
import { Descriptions } from 'antd'
import { amountFormat } from '@/utils'
import styles from './index.less'

function CpmMarginInfo({
  planMarginDate,
  planMarginAmount,
  marginAmount,
  deductAmount,
  backAmount,
  canBackAmount,
  totalReceivableAmount,
  notReceivableAmount,
}) {
  return (
    <>
      <div style={{ paddingTop: '24px', paddingBottom: '24px' }} className={styles.contractDetil}>
        <Descriptions
          bordered
          title="保证金"
          labelStyle={{ background: '#F5F6FA', height: '48px' }}
          size={'small'}
          // size={size}
          // extra={<Button type="primary">Edit</Button>}
        >
          <Descriptions.Item
            label="计划收款日期"
            labelStyle={{ width: '230px' }}
            contentStyle={{ width: '300px' }}
          >
            {planMarginDate}
          </Descriptions.Item>
          <Descriptions.Item
            label="计划收取保证金(元)"
            labelStyle={{ width: '230px' }}
            contentStyle={{ width: '300px' }}
          >
            {amountFormat(planMarginAmount / 10000)}
          </Descriptions.Item>
          <Descriptions.Item
            label="累计应收保证金(元)"
            labelStyle={{ width: '230px' }}
            contentStyle={{ width: '300px' }}
          >
            {amountFormat(totalReceivableAmount / 10000)}
          </Descriptions.Item>
          <Descriptions.Item label="已抵扣金额(元)">
            {amountFormat(deductAmount / 10000)}
          </Descriptions.Item>
          <Descriptions.Item label="已退金额(元)">
            {amountFormat(backAmount / 10000)}
          </Descriptions.Item>
          <Descriptions.Item label="未收金额(元)">
            {amountFormat(notReceivableAmount / 10000)}
          </Descriptions.Item>
          <Descriptions.Item label="可退金额(元)">
            {amountFormat(canBackAmount / 10000)}
          </Descriptions.Item>
          <Descriptions.Item
            label="保证金余额(元)"
            labelStyle={{ width: '230px' }}
            contentStyle={{ width: '300px' }}
          >
            {amountFormat(marginAmount / 10000)}
          </Descriptions.Item>
        </Descriptions>
      </div>
    </>
  )
}

export default CpmMarginInfo
