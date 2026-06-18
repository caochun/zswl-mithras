import ListRender from '@/components/AfterLease/RentCollection/ListRender'
import { observer } from '@zswl/admin'
import styles from '../../index.less'
import store from '../../store'
const RentCollection = () => {
  const { rentCollection } = store.page.getData()
  return (
    <div className={styles.moduleWrap} style={{ marginBottom: 16 }}>
      <div className={styles.title}>租后催收</div>
      <div className={styles.rentCollectionWrap}>
        <ListRender dataSource={rentCollection} />
      </div>
    </div>
  )
}

export default observer(RentCollection)
