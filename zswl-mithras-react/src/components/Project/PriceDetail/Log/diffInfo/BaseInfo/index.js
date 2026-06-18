import { useMemo } from 'react'
import {
  PriceAssignmentOfClaimsBaseInfo,
  PriceFactoringBaseInfo,
  PriceLeaseBaseInfo,
  PriceTurnLeaseBaseInfo,
} from '@/components/Project/PriceDetail/BaseInfo'

const Index = ({ bizType, detail, isLog }) => {
  const CurrentDom = useMemo(() => {
    if (!bizType) {
      return <div style={{ height: '200px' }}></div>
    }
    const commonProps = {
      detail,
      isLog,
      isLogPage: true,
      canEdit: false,
    }
    const Dom = {
      ZL: <PriceLeaseBaseInfo {...commonProps} />,
      BL: <PriceFactoringBaseInfo {...commonProps} />,
      ZZ: <PriceTurnLeaseBaseInfo {...commonProps} />,
      ZR: <PriceAssignmentOfClaimsBaseInfo {...commonProps} />,
    }
    return Dom[bizType]
  }, [bizType, detail, isLog])

  return <div>{CurrentDom}</div>
}
export default Index
