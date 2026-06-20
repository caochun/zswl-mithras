import React, { useEffect, useMemo, useRef, useState } from 'react'
import { observer } from '@zswl/admin'
import styles from './index.less'
import Store from './store'
import Container from './Container'
import Lease from './FormConfig/Lease'
import Factoring from './FormConfig/Factoring'
import AssignmentOfClaims from './FormConfig/AssignmentOfClaims'

export {
  Lease as ReviewLeaseQuotationScheme,
  Factoring as ReviewFactoringQuotationScheme,
  AssignmentOfClaims as ReviewAssignmentOfClaimsQuotationScheme,
}

const QuotationScheme = ({
  id,
  compareData,
  canEdit = true,
  isProjSponsor,
  rootStore,
  businessVersion,
  processInstanceId,
  isFormAdjust,
}) => {
  const { bizType, QSShowValue, setQSShowValue, QSZLShowValue, setQSZLShowValue, baseForm } =
    rootStore || {}
  const store = useMemo(() => new Store({ baseForm, rootStore }), [rootStore])
  const { getProjectQSDetail, newLeaseCredit, setProjectQSDetail } = store
  store.businessVersion = businessVersion
  store.bizType = bizType
  store.processInstanceId = processInstanceId
  // 项目调整页面引用，不需要调用compare
  store.isFormAdjust = isFormAdjust
  useEffect(() => {
    if (id) {
      getProjectQSDetail(id)
    }
    return () => {
      setProjectQSDetail(undefined)
    }
  }, [id, bizType, processInstanceId, businessVersion])

  useEffect(() => {
    return () => {
      setQSShowValue?.(true)
      setQSZLShowValue?.(true)
    }
  }, [])
  return bizType === 'BL' ? (
    <>
      <Container
        projectId={id}
        isProjSponsor={isProjSponsor}
        type={bizType}
        canEdit={canEdit}
        compareData={compareData}
        showValue={QSShowValue}
        setShowValue={setQSShowValue}
        store={store}
        rootStore={rootStore}
      />
      {newLeaseCredit && (
        <Container
          isProjSponsor={isProjSponsor}
          projectId={id}
          type="ZL"
          noTitle={true}
          canEdit={canEdit}
          compareData={compareData}
          showValue={QSZLShowValue}
          setShowValue={setQSZLShowValue}
          store={store}
          rootStore={rootStore}
          title={
            <div className={styles.subTitle} style={{ marginBottom: 20 }}>
              租赁方案
            </div>
          }
        />
      )}
    </>
  ) : (
    <Container
      isProjSponsor={isProjSponsor}
      canEdit={canEdit}
      compareData={compareData}
      showValue={QSShowValue}
      setShowValue={setQSShowValue}
      type={bizType}
      store={store}
      rootStore={rootStore}
    />
  )
}

export default observer(QuotationScheme)
