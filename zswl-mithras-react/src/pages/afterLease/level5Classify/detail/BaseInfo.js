import EditDescription from '@/components/Table/EditDescription'
import { ALL_COLUMNS } from '@/components/AfterLease/Level5ClassifyColumns'
import { getDescColumns, isAssetJon } from '@/utils'
import { observer } from '@zswl/admin'
import { Button } from '@zswl/components'
import styles from './index.less'

const columns = getDescColumns(ALL_COLUMNS, ALL_COLUMNS)

function Index({ store, detail, saveData, isLog, canEdit = true }) {
  const { isFormApproval } = store.page.getParams()
  const { nodeStatue } = store.page.getData()

  const hideBtn = isFormApproval || nodeStatue !== 'WAIT'

  return (
    <div className={styles.baseInfo}>
      <EditDescription
        title={
          <div className={styles.header}>
            <div>基本信息</div>
            {!hideBtn && isAssetJon() && (
              <Button type="primary" onClick={store.submitApproval}>
                复核确认
              </Button>
            )}
          </div>
        }
        detail={detail}
        saveData={saveData}
        canEdit={canEdit}
        isLog={isLog}
        columns={columns}
      />
    </div>
  )
}

export default observer(Index)
