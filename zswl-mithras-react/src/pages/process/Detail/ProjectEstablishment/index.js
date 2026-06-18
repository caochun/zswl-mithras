import { ProjectEstablishmentDetail } from '@/components/Project/EstablishmentDetailEntries'
import { observer } from '@zswl/admin'
import { useEffect, useMemo, useState } from 'react'
import Api from '../api'
const detailMap = {
  ZL: 'leasePriceRSP',
  BL: 'factoringPriceRSP',
  ZZ: 'leasePriceRSP',
  ZR: 'aocPriceRSP',
}
const ProjectEstablishment = (props) => {
  const { canEditFlag, businessVersion, id } = props

  const [baseInfoCompareData, setBaseInfoCompareData] = useState()
  const [QSCompareData, setQSCompareData] = useState({})
  const getChangedKeyList = (val) => {
    return val
      ? Object.keys(val).filter((item) => {
          return val[item]?.isChange
        })
      : []
  }
  const getBaseInfoCompareData = async (val) => {
    const res = await Api.establishBaseInfoCompare({ id: val, businessVersion })
    if (res) {
      const changedKeyList = getChangedKeyList(res)
      setBaseInfoCompareData(changedKeyList)
    }
  }
  const getQSCompareData = async (val) => {
    const res = await Api.establishQSCompare({ projEstablishId: val, businessVersion })
    if (res) {
      setQSCompareData({
        ZL: getChangedKeyList(res[detailMap.ZL]),
        BL: getChangedKeyList(res[detailMap.BL]),
        ZZ: getChangedKeyList(res[detailMap.ZZ]),
        ZR: getChangedKeyList(res[detailMap.ZR]),
      })
    }
  }
  useEffect(() => {
    if (id) {
      getBaseInfoCompareData(id)
      getQSCompareData(id)
    }
  }, [id])
  const compareData = useMemo(() => {
    return {
      baseInfo: baseInfoCompareData,
      qs: QSCompareData,
    }
  }, [baseInfoCompareData, QSCompareData])
  return (
    <ProjectEstablishmentDetail
      {...props}
      compareData={compareData}
      params={{ id }}
      query={{
        canEditFlag: canEditFlag ? 'true' : 'false',
        businessVersion,
      }}
    />
  )
}
export default observer(ProjectEstablishment)
