import Collapse from '@/components/Collapse'
import { getQuery } from '@zswl/admin'
import {
  ReviewDetailDataList as Data,
  ReviewDetailReport as Report,
} from '@/components/Project/ReviewDetailMaterialEntries'
import Api from '@/api/process/detail/projectReviewDetailApi'
import BlankBlock from '../../../../../BlankBlock'
import { useEffect, useState } from 'react'

const Index = ({ detailData, canEdit }) => {
  const { businessKey, businessVersion, processInstanceId } = detailData
  const [baseDetailData, setBaseDetailData] = useState({})
  const { isProjSponsor, processModel } = baseDetailData

  const getDetail = async () => {
    const res = await Api.postProjectBaseInfoDetail({ id: businessKey })
    setBaseDetailData(res)
  }

  useEffect(() => {
    businessKey && getDetail()
  }, [businessKey])

  const commonProps = {
    id: businessKey,
    canEdit,
    isProjSponsor,
    businessVersion,
    processInstanceId,
    processModel,
    isFormApproval: getQuery('typeId') == 'approval',
  }

  return (
    <>
      <Collapse header={'项目评审资料'}>
        <Report {...commonProps}></Report>
      </Collapse>
      <BlankBlock></BlankBlock>
      <Collapse header={'资料清单'}>
        <Data {...commonProps}></Data>
      </Collapse>
    </>
  )
}

export default Index
