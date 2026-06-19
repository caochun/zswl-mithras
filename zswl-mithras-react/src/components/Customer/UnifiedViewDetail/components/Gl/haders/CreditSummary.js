import React from 'react'
import { Progress } from 'antd'
import styles from './styles.less'
import { observer } from '@zswl/admin'

const formatAmount = (amount) => {
  const value = Number(amount / 10000)
  return !isNaN(value) ? value.toLocaleString() : '0'
}

function CreditSummary({ store }) {
  return (
    <div className={styles['credit-summary-container']}>
      <div className={styles['credit-summary-header']}>
        <div className={styles['credit-summary-icon']} />
        <div className={styles['credit-summary-info']}>
          <div className={styles['credit-summary-row']}>
            <div className={styles['credit-summary-label']}>授信总额度</div>
            <div className={styles['credit-summary-amount']}>
              {formatAmount(store?.credit?.applyCreditAmount)}
            </div>
          </div>
          <div className={styles['credit-summary-row']}>
            <div className={styles['credit-summary-label']}>剩余本金</div>
            <div className={styles['credit-summary-amount']} style={{ marginLeft: 24 }}>
              {formatAmount(store?.credit?.remainingPrincipal)}
            </div>
          </div>
        </div>
      </div>

      <div className={styles['credit-summary-divider']} />
      <div className={styles['credit-summary-content']}>
        <div className={styles['progress-container']}>
          <Progress
            type="circle"
            percent={(store?.credit?.usageRate || 0) * 100}
            width={80}
            strokeColor="#1890ff"
            format={(percent) => `${percent || 0}%`}
          />
        </div>

        <div className={styles['data-container']}>
          <div className={styles['data-item']}>
            <div className={styles['data-label']}>
              <div className={styles['dot-used']} /> 已用
            </div>
            <div className={styles['data-value']}>
              {formatAmount(store?.credit?.usedCreditAmount)}
            </div>
          </div>
          <div className={styles['data-item']}>
            <div className={styles['data-label']}>
              <div className={styles['dot-unused']} /> 未用
            </div>
            <div className={styles['data-value']}>
              {formatAmount(store?.credit?.unusedCreditAmount)}
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default observer(CreditSummary)
