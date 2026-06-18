import { ProjectReviewDetail } from '@/components/Project/ReviewDetailEntries'
import { observer } from '@zswl/admin'
import { useEffect, useMemo, useState } from 'react'
import Api from '../api'
const detailMap = {
  ZL: 'leasePriceDetailRSP',
  BL: 'factoringPriceDetailRSP',
  ZZ: 'leasePriceDetailRSP',
  ZR: 'aocPriceDetailRSP',
}
const ProjectReview = (props) => {
  const { canEditFlag, processInstanceId, businessVersion, id, isNewLayout, modelKey } = props
  const [baseInfoCompareData, setBaseInfoCompareData] = useState()
  const [QSCompareData, setQSCompareData] = useState({})
  const [bizType, setBizType] = useState()

  const getChangedKeyList = (val) => {
    return val
      ? Object.keys(val).filter((item) => {
          return val[item]?.isChange
        })
      : []
  }
  const getBaseInfoCompareData = async (val) => {
    const api =
      modelKey === 'ProjReviewCreateFlow'
        ? Api.reviewPricingBaseInfoCompare
        : Api.reviewBaseInfoCompare
    const res = await api({ id: val, businessVersion, processInstanceId })
    if (res) {
      setBizType(res.bizType?.value)
      const changedKeyList = getChangedKeyList(res)
      setBaseInfoCompareData(changedKeyList)
    }
  }

  const getQSCompareData = async (val) => {
    const api =
      modelKey === 'ProjReviewCreateFlow' ? Api.reviewPricingQSCompare : Api.reviewQSCompare
    const params = {
      businessVersion,
      processInstanceId,
      id: val,
    }

    const res = await api(params)
    if (res) {
      setQSCompareData({
        ZL: getChangedKeyList(res[detailMap.ZL]),
        BL: getChangedKeyList(res[detailMap.BL]),
        ZZ: getChangedKeyList(res[detailMap.ZZ]),
        ZR: getChangedKeyList(res[detailMap.ZR]),
      })
    }
  }
  // useEffect(() => {
  //   if (id) {
  //     getBaseInfoCompareData(id)
  //     getQSCompareData(id)
  //   }
  // }, [id])
  // const compareData = useMemo(() => {
  //   return {
  //     baseInfo: baseInfoCompareData,
  //     qs: QSCompareData,
  //   }
  // }, [baseInfoCompareData, QSCompareData])

  return (
    <ProjectReviewDetail
      processInstanceId={processInstanceId}
      modelKey={modelKey}
      {...props}
      // compareData={compareData}
      isNewLayout={isNewLayout}
      params={{ id }}
      query={{
        canEditFlag: canEditFlag ? 'true' : 'false',
        businessVersion,
        bizType,
      }}
    />
  )
}
export default observer(ProjectReview)
