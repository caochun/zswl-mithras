import fundTransferApi from '@/api/financial/fundTransfer'
import moment from 'moment'
import { useEffect, useState } from 'react'
import styles from '../../style.less'
import SupervisePie from '../SupervisePie'
import SuperviseTable from '../SuperviseTable'
import DepositDetailModal from './DepositDetailModal'

const FinancialLiquidityFundTransferDaily = () => {
  const [data, setData] = useState({})

  const getCurrentDaily = async () => {
    const result = await fundTransferApi.postCurrentDaily({
      currentDate: moment().format('YYYY-MM-DD'),
    })
    setData(result)
  }

  useEffect(() => {
    getCurrentDaily()
  }, [])

  return (
    <div className={styles.transferSectionPart}>
      <div className={styles.title}>
        <div className="z-sub-title">监管户待转资金当日</div>
      </div>
      <div className={styles.transferSection}>
        <div className={styles.left}>
          <SupervisePie pieData={data.list || []}></SupervisePie>
        </div>
        <div className={styles.right}>
          <DepositDetailModal />
          <SuperviseTable pieData={data.list || []} />
        </div>
      </div>
    </div>
  )
}

export default FinancialLiquidityFundTransferDaily
