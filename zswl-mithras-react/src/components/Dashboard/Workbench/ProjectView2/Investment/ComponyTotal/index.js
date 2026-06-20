import { useEffect } from 'react'
import { observer } from '@zswl/admin'
import { AmountFormat } from '@/components/Format'
import styles from './index.less'

const sourceMap = {
  payContractQuantity: '投放合计数量',
  payAmount: '投放金额(万元)',
  averageIrr: '加权IRR',
  averageCommissionRate: '加权手续费率',
  averageContractInterestRate: '加权合同利率',
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
      <div className={styles.componyWrap}>
        {Object.keys(sourceMap).map((key) => {
          return (
            <div className={styles.item} key={key} onClick={() => investmentDrawer.open(key)}>
              <span className={styles.num}>
                {AmountFormat({
                  value: companyData[key]?.value ?? companyData[key],
                  unit: companyData[key]?.unit?.indexOf('%') > -1 ? companyData[key]?.unit : '',
                  initFormat: 1,
                })}
              </span>
              <span className={styles.label}>{sourceMap[key]}</span>
            </div>
          )
        })}
      </div>
    </div>
  )
}

export default observer(Index)
