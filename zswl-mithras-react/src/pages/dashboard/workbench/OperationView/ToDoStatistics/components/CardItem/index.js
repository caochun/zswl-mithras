import styles from './index.less'
import { observer } from '@zswl/admin'
import { hasValue } from '@/utils'

const Index = ({ data, style, store }) => {
  return (
    <div className={styles.wrap} style={{ ...style }}>
      <div className={styles.title}>{data.modelName}</div>
      <div className={styles.item}>
        {data.content?.map((item, index) => {
          return (
            <div key={index} className={styles.group}>
              <div className={styles.group_value}>
                {hasValue(item.count) ? (
                  <span className={styles.cursor} onClick={() => store.goProcess(item)}>
                    {item.count}
                  </span>
                ) : (
                  '-'
                )}
              </div>
              <span className={styles.group_title}>{item.activityDisplay}</span>
            </div>
          )
        })}
      </div>
    </div>
  )
}

export default observer(Index)
