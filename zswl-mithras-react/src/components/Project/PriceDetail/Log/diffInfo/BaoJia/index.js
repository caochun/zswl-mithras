import { useMemo } from 'react'
import {
  PriceAssignmentOfClaimsQuotationScheme,
  PriceFactoringQuotationScheme,
  PriceLeaseQuotationScheme,
} from '../../../QuotationScheme'

const Index = ({ bizType, detail, showValue, isLog }) => {
  const CurrentDom = useMemo(() => {
    if (!bizType) {
      return <div style={{ height: '200px' }}></div>
    }
    const Dom = {
      ZL: <PriceLeaseQuotationScheme showValue={showValue} detail={detail} isLog={isLog} />,
      BL: <PriceFactoringQuotationScheme showValue={showValue} detail={detail} isLog={isLog} />,
      ZZ: <PriceLeaseQuotationScheme showValue={showValue} detail={detail} isLog={isLog} />,
      ZR: <PriceAssignmentOfClaimsQuotationScheme showValue={showValue} detail={detail} isLog={isLog} />,
    }
    return Dom[bizType]
  }, [bizType, detail, showValue, isLog])

  return <div>{CurrentDom}</div>
}
export default Index
