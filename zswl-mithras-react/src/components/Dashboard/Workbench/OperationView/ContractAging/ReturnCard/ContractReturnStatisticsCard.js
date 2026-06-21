import { observer } from '@zswl/admin'
import { AmountFormat } from '@/components/Format'
import { useEffect } from 'react'
import IconFont from '@/components/Icon'
import { Spin } from 'antd'
import { hasValue } from '@/utils'
import ContractReturnStatisticsDrawer from '../ReturnDrawer/ContractReturnStatisticsDrawer'
import styles from './index.less'

const sourceMap = {
  contractApprovedNumber: '合同审批通过数量',
  contractReturnNumber: '存在流程退回数量',
  returnRate: '退回率',
  returnCount: '总退回次数',
  returnAverage: '平均退回次数',
}

const ContractReturnStatisticsCard = ({ store }) => {
  const { returnStatistics, returnStatisticsLoading } = store

  useEffect(() => {
    store.getReturnStatistics()
  }, [])

  return (
    <div>
      <div className={styles.title}>
        <IconFont type="icon-jieqing" className={styles.icon}></IconFont>
        合同退回统计
      </div>
      <Spin spinning={returnStatisticsLoading}>
        <div className={styles.companyWrap} onClick={store.returnDrawer.open}>
          {Object.keys(sourceMap).map((key) => {
            return (
              <div className={styles.item} key={key}>
                <div className={styles.num}>
                  {AmountFormat({
                    value: returnStatistics[key],
                    initFormat: 1,
                    unit: hasValue(returnStatistics[key]) ? (key === 'returnRate' ? '%' : '') : '',
                  })}
                </div>
                <div className={styles.label}>{sourceMap[key]}</div>
              </div>
            )
          })}
        </div>
      </Spin>
      <ContractReturnStatisticsDrawer store={store}></ContractReturnStatisticsDrawer>
    </div>
  )
}

export default observer(ContractReturnStatisticsCard)
