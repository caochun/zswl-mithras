import { observer } from '@zswl/admin'
import { AmountFormat } from '@/components/Format'
import { useEffect } from 'react'
import { Spin } from 'antd'
import IconFont from '@/components/Icon'
import ApprovalDrawer from '../ApprovalDrawer'
import styles from './index.less'

const sourceMap = {
  contractApprovedNumber: '合同审批通过数量',
  operationHandAverage: '运营经办平均时效(工作日)',
  operationReviewAverage: '运营复核平均时效(工作日)',
  operationAverage: '运营部平均时效(工作日)',
  processAverage: '全流程平均时效(工作日)',
}

const Index = ({ store }) => {
  const { approvalStatistics, approvalStatisticsLoading } = store

  useEffect(() => {
    store.getApprovalStatistics()
  }, [])

  return (
    <div>
      <div className={styles.title}>
        <IconFont type="icon-yuqixiangmu" className={styles.icon}></IconFont>
        运营审批时效
      </div>
      <Spin spinning={approvalStatisticsLoading}>
        <div className={styles.companyWrap} onClick={store.approvalDrawer.open}>
          {Object.keys(sourceMap).map((key) => {
            return (
              <div className={styles.item} key={key}>
                <div className={styles.num}>
                  {AmountFormat({
                    value: approvalStatistics[key],
                    initFormat: 1,
                  })}
                </div>
                <div className={styles.label}>{sourceMap[key]}</div>
              </div>
            )
          })}
        </div>
      </Spin>
      <ApprovalDrawer store={store}></ApprovalDrawer>
    </div>
  )
}

export default observer(Index)
