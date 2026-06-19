// 财务报表
import React, { useMemo } from 'react'
import { Tabs } from 'antd'
import styles from '../style.less'
import Cash from '../../FinancialReport/Cash'
import Debt from '../../FinancialReport/Debt'
import Profit from '../../FinancialReport/Profit'
import Store from '../../FinancialReport/store'
import { observer } from '@zswl/admin'

const FinancialReport = ({ id }) => {
  const canEdit = true
  const store = useMemo(() => new Store({ id, canEditFlag: false }), [id])
  const items = [
    {
      label: '1. 资产负债表',
      key: '1',
      children: <Debt store={store} />,
    },
    {
      label: '2. 利润表',
      key: '2',
      children: <Profit store={store} />,
    },
    {
      label: '3. 现金流量表',
      key: '3',
      children: <Cash store={store} />,
    },
  ]

  return (
    <div className={styles['financial-report-container']}>
      <Tabs tabPosition={'left'} items={items} />
    </div>
  )
}

export default observer(FinancialReport)
