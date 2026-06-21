import { observer } from '@zswl/admin'
import { Descriptions } from 'antd'
import { useMemo } from 'react'
import styles from '../index.less'
import { amountFormat } from '@/utils'
const CashFlowInfo = ({ store }) => {
  const data = store.page.getData()
  const columns = useMemo(() => {
    const { payables, planedPaidDate, planedPaidAmount } = data
    return [
      {
        label: '现金流项目',
        value: payables || '-',
      },
      {
        label: '计划付款日期',
        value: planedPaidDate || '-',
      },
      {
        label: '计划付款金额(元)',
        value: amountFormat(planedPaidAmount / 10000) || '-',
      },
    ]
  }, [data])

  return (
    <>
      <Descriptions
        title="合同金额"
        bordered
        column={3}
        style={{
          marginBottom: 20,
        }}
        labelStyle={{ background: '#F5F6FA' }}
        size={'small'}
        className={styles.summaryDescription}
      >
        {columns.map((item, index) => {
          return (
            <Descriptions.Item key={index} {...item}>
              {item.value}
            </Descriptions.Item>
          )
        })}
      </Descriptions>
    </>
  )
}

export default observer(CashFlowInfo)
