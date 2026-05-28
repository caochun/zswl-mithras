import { useState, useEffect, useMemo, useRef } from 'react'
import { observer, getQuery } from '@zswl/admin'
import Lease from './FormConfig/Lease'
import AssignmentOfClaims from './FormConfig/AssignmentOfClaims'
import Factoring from './FormConfig/Factoring'
import Store from './store'
import { Skeleton } from 'antd'

const Index = ({
  contractId,
  detail,
  canEditFlag = true,
  baseStore = {},
  businessVersion,
  isDetail = false,
  isLog,
}) => {
  const { baseForm, setIrrIsChange, page } = baseStore
  const isFormApproval = getQuery('typeId') == 'approval'
  const { remainAvailableQuota } = isFormApproval
    ? page?.getData()?.newDetail ?? {}
    : page?.getData()?.detail ?? {}

  const store = useMemo(() => {
    return new Store({
      businessVersion,
      contractId,
      isFormApproval,
      baseForm,
      setIrrIsChange,
      baseStore,
    })
  }, [businessVersion, contractId, isFormApproval, baseForm, setIrrIsChange])

  const {
    getContractQSDetail,
    postContractQSModify,
    contractQSDetail,
    getBaseInfo,
    bizType,
    leaseTypes,
  } = store

  const baoJiaDetail = detail || store.getDetail()

  useEffect(() => {
    if (contractId) {
      getBaseInfo(contractId).then(() => {
        getContractQSDetail(contractId)
      })
    }
  }, [contractId])

  const commonProps = {
    initEdit: !!getQuery().isCreate,
    leaseTypes,
    bizType,
    store,
    isDetail,
    detail: baoJiaDetail,
    canEdit: canEditFlag,
    saveData: postContractQSModify,
    isLog: isLog || contractQSDetail?.isLog,
    remainAvailableQuota,
  }

  if (!bizType) {
    return <Skeleton />
  }
  const Dom = {
    ZL: <Lease {...commonProps} />,
    ZZ: <Lease {...commonProps} />,
    BL: <Factoring {...commonProps} />,
    ZR: <AssignmentOfClaims {...commonProps} />,
  }
  return Dom[bizType]
}

export default observer(Index)
