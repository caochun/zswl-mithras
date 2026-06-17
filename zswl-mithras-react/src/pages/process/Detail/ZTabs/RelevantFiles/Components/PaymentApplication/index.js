import { Collapse } from '@/components'
import LendingMaterials from '@/components/Cpm/PaymentApplication/LendingMaterials'
import InformationList from '@/components/Cpm/PaymentApplication/InformationList'
import Api from '@/api/cpm/payment/paymentApplicationDetail'
import BlankBlock from '@/components/Process/BlankBlock'
import { useEffect, useState } from 'react'

const Index = ({ detailData, canEdit }) => {
  const { businessKey, businessVersion } = detailData
  const [baseDetailData, setBaseDetailData] = useState()

  const getDetail = async () => {
    const res = await Api.getPaymentDetail({ id: businessKey })
    setBaseDetailData(res)
  }

  useEffect(() => {
    businessKey && getDetail()
  }, [businessKey])

  return (
    <>
      <Collapse header={'放款材料'}>
        <LendingMaterials
          mainId={businessKey}
          canEditFlag={canEdit}
          businessVersion={businessVersion}
        ></LendingMaterials>
      </Collapse>
      <BlankBlock></BlankBlock>
      <Collapse header={'资料清单'}>
        <InformationList
          mainId={businessKey}
          canEditFlag={canEdit}
          baseDetailData={baseDetailData}
        ></InformationList>
      </Collapse>
    </>
  )
}

export default Index
