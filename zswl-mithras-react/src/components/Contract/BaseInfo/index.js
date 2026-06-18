import { useEffect, forwardRef, useState, useImperativeHandle, useMemo } from 'react'
import { observer, getQuery } from '@zswl/admin'
import Lease from './FormConfig/Lease'
import AssignmentOfClaims from './FormConfig/AssignmentOfClaims'
import TurnLease from './FormConfig/TurnLease'
import Factoring from './FormConfig/Factoring'
import Api from '@/api/contract/contractDetail'
import { Skeleton } from 'antd'

export {
  Lease as LeaseBaseInfo,
  AssignmentOfClaims as AssignmentOfClaimsBaseInfo,
  TurnLease as TurnLeaseBaseInfo,
  Factoring as FactoringBaseInfo,
}

const Index = (
  {
    contractId,
    detail,
    canEditFlag = true,
    baseStore = {},
    isDetail = false,
    onEmitData = (v) => v,
  },
  ref
) => {
  const { bizType, saveBaseInfo, page } = baseStore

  const [pageDetail, setPageDetail] = useState({})
  const [currentBizType, setCurrentBizType] = useState()

  useEffect(() => {
    if (detail) {
      setPageDetail(detail)
      onEmitData(detail)
    }
  }, [detail])

  useEffect(() => {
    if (bizType) {
      setCurrentBizType(bizType)
    }
  }, [bizType])

  useEffect(() => {
    // 租金催收
    if (contractId) {
      Api.getBaseInfo({ id: contractId }).then((res) => {
        setCurrentBizType(res.bizType)
        setPageDetail(res)
        onEmitData(res)
      })
    }
  }, [contractId])

  useImperativeHandle(ref, () => ({
    getPageDetail: () => {
      return pageDetail
    },
  }))

  const commonProps = {
    isDetail,
    detail: pageDetail,
    canEdit: canEditFlag,
    saveData: saveBaseInfo,
    store: baseStore,
    isLog: page?.getData()?.isLog,
  }

  const CurrentDom = () => {
    if (!currentBizType) {
      return <Skeleton />
    }
    const Dom = {
      ZL: <Lease {...commonProps} canEdit={false} />,
      ZZ: <TurnLease {...commonProps} canEdit={false} />,
      BL: <Factoring {...commonProps} />,
      ZR: <AssignmentOfClaims {...commonProps} />,
    }
    return Dom[currentBizType]
  }

  return <div>{CurrentDom()}</div>
}

export default observer(forwardRef(Index))
