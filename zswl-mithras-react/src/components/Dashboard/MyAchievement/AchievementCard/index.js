import { AmountFormat } from '@/components/Format'
import { observer } from '@zswl/admin'
import { Skeleton } from 'antd'
import styles from './index.less'
import { cardConfig } from '../Config'

const Card = (props) => {
  return (
    <div className={styles.wrap} style={{ borderColor: props.borderColor }}>
      <div className={styles.mark} style={{ borderTopColor: props.borderColor }}>
        <div className={styles.text}>{props.type}</div>
      </div>
      <div className={styles.content}>
        <div className={styles.row}>
          <div className={styles.row_value}>
            <AmountFormat value={props.accumulatedAmount} initFormat={1}></AmountFormat>
          </div>
          <div className={styles.row_title}>累积{props.type}(万元)</div>
        </div>
        <div className={styles.row}>
          <div className={styles.row_value}>
            <AmountFormat value={props.target} initFormat={1}></AmountFormat>
          </div>
          <div className={styles.row_title}>目标(万元)</div>
        </div>
        <div className={styles.row}>
          <div className={styles.row_value}>
            <AmountFormat value={props.completionRate} initFormat={1} unit={'%'}></AmountFormat>
          </div>
          <div className={styles.row_title}>完成率</div>
        </div>
      </div>
    </div>
  )
}

const Index = ({ cardData }) => {
  return (
    <div className={styles.container}>
      {cardData.map((item) => {
        const current = cardConfig.find((itemConfig) => itemConfig.title === item.type)
        return (
          <div className={styles.cardRow}>
            <Card {...item} {...current}></Card>
          </div>
        )
      })}
    </div>
  )
}

export default observer(Index)
