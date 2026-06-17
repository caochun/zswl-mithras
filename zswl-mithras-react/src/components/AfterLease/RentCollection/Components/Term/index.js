import IconFont from '@/components/Icon'
import { amountFormat } from '@/utils'
import classNames from 'classnames'
import styles from './indes.less'

const Term = ({ data, onClick, style }) => {
  const { phase, planCollectionAmount, planCollectionDate, state, tagList, noticeFinancialFlag } =
    data
  const stateEnum = {
    PAID: styles.normal,
    PENDING: styles.warning,
    OVERDUE: styles.error,
    NOT_YET_EXPIRED: styles.no,
  }
  const tagEnum = (t, index) => {
    return {
      DEDUCTION: (
        <div key={index} className={styles.tag}>
          减免
        </div>
      ),
      OVERDUE: (
        <div key={index} className={styles.tag}>
          逾期
        </div>
      ),
      NOTIFIED: (
        <div key={index} className={styles.tag}>
          已通知
        </div>
      ),
    }[t]
  }
  return (
    <div onClick={onClick} className={classNames(styles.termWrap, stateEnum[state])} style={style}>
      <div className={styles.titleWrap}>
        <div className={styles.title}>{`第${phase}期`}</div>
        <div className={styles.tagList}>
          {/* {noticeFinancialFlag && (
            <Tooltip title="已通知财务系统收款">
              <IconFont style={{ marginRight: 12 }} type="icon-icon_notification" />
            </Tooltip>
          )} */}
          {tagList.map((item, index) => {
            return tagEnum(item, index)
          })}
        </div>
      </div>
      <div className={styles.contentItem}>
        <IconFont className={styles.icon} type="icon-icon_pay" />
        <div className={styles.label}>{amountFormat(planCollectionAmount / 10000)}元</div>
      </div>
      <div className={styles.contentItem}>
        <IconFont className={styles.icon} type="icon-shenpiguanli-copy" />
        <div className={styles.label}>{planCollectionDate}</div>
      </div>
    </div>
  )
}

export default Term
