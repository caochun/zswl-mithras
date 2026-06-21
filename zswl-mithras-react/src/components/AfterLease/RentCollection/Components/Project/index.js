import IconFont from '@/components/Icon'
import { App } from '@zswl/components'
import { Tooltip } from 'antd'
import classNames from 'classnames'
import { useMemo } from 'react'
import styles from './index.less'
const Project = ({ data, onClick }) => {
  const {
    receiptCode,
    contractCode,
    clientName,
    contractStatus,
    projSponsorUserName,
    bizDeptName,
    paymentState,
  } = data
  const tagColor = useMemo(() => {
    return {
      OVERDUE: styles.error,
      START_RENT: styles.normal,
      TAKE_EFFECT: styles.normal,
    }[paymentState]
  }, [paymentState])
  return (
    <div onClick={onClick} className={styles.projectWrap}>
      <Tooltip title={clientName}>
        <div className={styles.title}>{clientName}</div>
      </Tooltip>

      <div className={styles.code}>{receiptCode}</div>
      <div className={classNames(styles.tag, tagColor)}>
        {App.matchOption('rentCollectionIndexPaymentState', paymentState).label}
      </div>
      <Tooltip title={`${bizDeptName} - ${projSponsorUserName}`}>
        <div className={styles.contentItem}>
          <IconFont className={styles.icon} type="icon-kehuguanli" />
          <div className={styles.label}>
            {bizDeptName} - {projSponsorUserName}
          </div>
        </div>
      </Tooltip>
    </div>
  )
}

export default Project
