import { Collapse } from '@/components'
import { getQuery } from '@zswl/admin'
import Report from '@/pages/project/review/detail/Report'
import Data from '@/pages/project/review/detail/Data'
import Api from '@/pages/project/review/detail/api'
import BlankBlock from '@/pages/process/components/BlankBlock'
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
