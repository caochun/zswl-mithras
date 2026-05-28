import { observer } from '@zswl/admin'
import { Button } from '@zswl/components'
import { Space } from 'antd'
import styles from './styles.less'

const Index = ({ store, auth }) => {
  const { executeData } = store

  return (
    <div style={{ marginTop: 20 }}>
      <Space>
        <Button type="primary" onClick={store.calc} disabled={!auth}>
          试算
        </Button>
        <Space>
          试算次数
          <span className={styles.gray} style={{ width: 120 }}>
            {executeData?.executeCount}/{executeData?.executeCountLimit}
          </span>
        </Space>
        <Space>
          建议项目限额
          <span className={styles.gray} style={{ width: 120 }}>
            {executeData?.projQuota ? executeData.projQuota + '万元' : ''}
          </span>
        </Space>
      </Space>
    </div>
  )
}

export default observer(Index)
