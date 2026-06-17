import { observer, getQuery, setSessionStorage } from '@zswl/admin'
import { Page } from '@zswl/components'
import Tab from '@/components/CreditManage/CreditTable/Tab'
import { CREATETABLE_PARAMS } from '@/components/CreditManage/CreditTableConfig'
import { isRiskManager } from '@/utils'
import { useEffect } from 'react'

const Index = ({
  query: { businessVersion, businessKey, canEditFlags = 'true', processInstanceId },
}) => {
  const isFormApproval = getQuery('typeId') == 'approval'
  const canEdit = isRiskManager() && canEditFlags === 'true'

  useEffect(() => {
    setSessionStorage(CREATETABLE_PARAMS, null)
    return () => {
      setSessionStorage(CREATETABLE_PARAMS, null)
    }
  }, [])
  return (
    <Page>
      <Tab
        channel={isFormApproval ? 'PROC' : 'EDIT'}
        showActionColumn={true}
        businessVersion={businessVersion}
        businessKey={businessKey}
        isFormApproval={isFormApproval}
        processInstanceId={processInstanceId}
        canEdit={canEdit}
        showSearch
      ></Tab>
    </Page>
  )
}

export default observer(Index)
