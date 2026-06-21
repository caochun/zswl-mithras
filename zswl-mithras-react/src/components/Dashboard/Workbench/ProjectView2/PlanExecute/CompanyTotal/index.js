import { useEffect } from 'react'
import { observer } from '@zswl/admin'
import { AmountFormat } from '@/components/Format'
import styles from './index.less'

const sourceMap = {
  payContractQuantity: '投放合计数量',
  planPayAmount: '计划投放金额(万元)',
  payAmount: '实际投放金额(万元)',
  finishRate: '达成率',
}

const Index = ({ store }) => {
  const { companyData, activityKey, investmentDrawer } = store
  useEffect(() => {
    store.getCompanyData()
  }, [])

  useEffect(() => {
    store.getCompanyData()
  }, [activityKey])
  return (
    <div>
      <div className={styles.title}>公司合计</div>
      <div className={styles.companyWrap} onClick={investmentDrawer.open}>
        {Object.keys(sourceMap).map((key) => {
          return (
            <div className={styles.item} key={key}>
              <div className={styles.num}>
                {AmountFormat({
                  value: companyData[key]?.value ?? companyData[key],
                  unit: !companyData[key]?.unit?.includes('元') ? companyData[key]?.unit : '',
                  initFormat: 1,
                })}
              </div>
              <div className={styles.label}>{sourceMap[key]}</div>
            </div>
          )
        })}
      </div>
    </div>
  )
}

export default observer(Index)
