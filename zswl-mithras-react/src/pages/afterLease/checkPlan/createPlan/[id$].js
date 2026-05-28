import { useMemo } from 'react'
import { observer, getQuery } from '@zswl/admin'
import { Page, Button } from '@zswl/components'
import { isAssetJonAndAdmin } from '@/utils'
import QuarterPlan from './QuarterPlan'
import OtherPlan from './OtherPlan'
import BaseInfo from './BaseInfo'
import { DetailLayout } from '@/components'
import Store from './store'

const Index = ({ params: { id }, query: { canEditFlags = 'true', businessVersion } }) => {
  // 是否审批流页面
  const isFormApproval = getQuery('typeId') == 'approval'
  const isNew = getQuery('isNew')
  const store = useMemo(() => {
    return new Store({})
  }, [])
  const detail = store.page.getData()
  const { onSubmit } = store
  const canEditFlagsFormAuth = canEditFlags === 'true' && isAssetJonAndAdmin()
  const { planType } = detail
  const commonProps = {
    store,
    detail,
    businessVersion,
    planId: id,
    canEditFlag: canEditFlagsFormAuth,
  }

  const PlanClient = useMemo(() => {
    if (planType === 'QUARTER') {
      return <QuarterPlan {...commonProps}></QuarterPlan>
    }
    return <OtherPlan {...commonProps}></OtherPlan>
  }, [planType, commonProps])

  return (
    <Page store={store} params={{ id, businessVersion }}>
      <DetailLayout
        title={isNew ? '新增检查计划' : '编辑检查计划'}
        extra={
          !isFormApproval && (
            <>
              <Button onClick={() => store.cancelFlow(id)}>取消操作</Button>
              <Button type="primary" onClick={onSubmit}>
                发布计划
              </Button>
            </>
          )
        }
      >
        <BaseInfo
          detail={detail}
          canEditFlag={canEditFlagsFormAuth}
          saveData={store.saveData}
        ></BaseInfo>
        {PlanClient}
      </DetailLayout>
    </Page>
  )
}

export default observer(Index)
