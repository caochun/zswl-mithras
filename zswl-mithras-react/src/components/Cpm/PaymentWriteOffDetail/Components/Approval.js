import { observer } from '@zswl/admin'
import { Descriptions } from 'antd'
import { useMemo } from 'react'
import styles from '../index.less'
import { amountFormat } from '@/utils'
import { App } from '@zswl/components'
const Approval = ({ store }) => {
  const data = store.page.getData()
  const columns = useMemo(() => {
    const {
      processStatus,
      applyPaymentDate,
      applyPaymentAmount,
      yunyingReviewStateDisplay,
      yunyingReviewDate,
    } = data
    return [
      {
        label: '审批状态',
        value: processStatus
          ? App.matchOption('projReviewProcessStatus', processStatus).label
          : '-',
      },
      {
        label: '申请付款日期',
        value: applyPaymentDate || '-',
      },
      {
        label: '申请付款金额(元)',
        value: amountFormat(applyPaymentAmount / 10000) || '-',
      },
      { label: '运营提前审核', value: yunyingReviewStateDisplay },
      { label: '运营审核时间', value: yunyingReviewDate },
    ]
  }, [data])

  return (
    <>
      <Descriptions
        title="审批信息"
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

export default observer(Approval)
