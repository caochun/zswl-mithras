import { observer, getQuery } from '@zswl/admin'
import { Page, Button } from '@zswl/components'
import {
  isAssetJonAndAdmin,
  getKeyOptionsLabelMapPlus,
  isBaseinessDept,
  isAdminAccount,
} from '@/utils'
import { useMemo } from 'react'
import BaseInfo from './BaseInfo'
import Report from './Report'
import Project from './Project'
import Store from './store'
import styles from './index.less'
import { QuarterMap } from '@/utils/domains/afterLease/AfterLeaseUtils'
import { DetailLayout } from '@/components/Layout'

const AfterLeaseCheckPlanDetail = ({ params = {}, query: { canEditFlags = 'true', businessVersion } }) => {
  // 是否审批流页面
  const isFormApproval = getQuery('typeId') == 'approval'
  const { id } = params
  const store = useMemo(() => {
    return new Store({})
  }, [])
  const { page, onSubmit } = store
  const { year, month, quarter, planType, planStatus } = page.getData()
  const planTypeText = `${year}年${quarter ? QuarterMap[quarter] : month + '月'}`

  // 权限 + 提交完成的
  const canEditFlagsFormAuth =
    canEditFlags === 'true' && isAssetJonAndAdmin() && planStatus !== 'FINISH'

  return (
    <div className={styles.page}>
      <Page store={store} params={{ id, businessVersion }}>
        <DetailLayout
          extra={
            !isFormApproval &&
            canEditFlagsFormAuth && (
              <Button type="primary" onClick={onSubmit}>
                提交完结审批
              </Button>
            )
          }
          title={
            <div>
              {year ? planTypeText + '常规租后检查计划' : ''}&nbsp;
              <span className={styles.tag}>
                {getKeyOptionsLabelMapPlus('afterLeaseCheckPlanTypeEnum')[planType]}
              </span>
            </div>
          }
        >
          <BaseInfo detail={page.getData()} planTypeText={planTypeText}></BaseInfo>
          {(!isBaseinessDept() || isAdminAccount()) && (
            <Report
              planId={id}
              canEditFlag={canEditFlagsFormAuth}
              businessVersion={businessVersion}
            ></Report>
          )}

          <Project
            planId={id}
            canEditFlag={canEditFlagsFormAuth}
            canEditFlags={canEditFlags}
            detail={page.getData()}
            businessVersion={businessVersion}
          ></Project>
        </DetailLayout>
      </Page>
    </div>
  )
}

export default observer(AfterLeaseCheckPlanDetail)
