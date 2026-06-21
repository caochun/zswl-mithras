import { useMemo, useEffect } from 'react'
import { getQuery, observer } from '@zswl/admin'
import { Page } from '@zswl/components'
import QuarterPanel from './Component/QuarterPanel'
import List from './Component/List'
import Process from './Component/Process'
import moment from 'moment'
import Store from './store'
import styles from './index.less'
import FileList from './FileList'
import { isAssetJon, hasPermission } from '@/utils'

const lastQuarterMoment = moment().add(-1, 'Q')
const quarter = lastQuarterMoment.quarter()
const year = lastQuarterMoment.format('yyyy')

const AfterLeaseLevel5Classify = ({ params = {}, query = {} }) => {
  const { id } = params
  const { businessVersion, modelKey, curTaskActivityIds, taskStatus, canEditFlags = 'true' } = query
  const isFormApproval = getQuery('typeId') == 'approval'
  const canEdit = canEditFlags === 'true'

  const store = useMemo(() => {
    return new Store({ quarter, year, id, businessVersion, modelKey })
  }, [quarter, year, id, businessVersion, modelKey])

  useEffect(() => {
    if (isFormApproval) {
      store.table.search()
    }
  }, [id, isFormApproval])

  return (
    <div className={styles.page}>
      <Page store={store} params={{ isFormApproval, quarter, year }}>
        {!isFormApproval && (
          <>
            <QuarterPanel store={store}></QuarterPanel>
            <Process store={store}></Process>
          </>
        )}
        <List
          store={store}
          modelKey={modelKey}
          curTaskActivityIds={curTaskActivityIds}
          query={query}
        ></List>
        {hasPermission('assetClassifyReviewFileListGroup') && (
          <FileList
            taskStatus={taskStatus}
            businessVersion={businessVersion}
            canEdit={canEdit && isFormApproval}
            isFormApproval={isFormApproval}
            store={store}
            modelKey={modelKey}
          ></FileList>
        )}
      </Page>
    </div>
  )
}

export default observer(AfterLeaseLevel5Classify)
