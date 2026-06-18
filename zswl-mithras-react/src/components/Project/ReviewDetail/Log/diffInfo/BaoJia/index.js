import { useMemo } from 'react'
import {
  ReviewAssignmentOfClaimsQuotationScheme,
  ReviewFactoringQuotationScheme,
  ReviewLeaseQuotationScheme,
} from '@/components/Project/ReviewDetail/QuotationScheme'

const Index = ({ bizType, detail, showValue, isLog }) => {
  const CurrentDom = useMemo(() => {
    if (!bizType) {
      return <div style={{ height: '200px' }}></div>
    }
    const Dom = {
      ZL: <ReviewLeaseQuotationScheme showValue={showValue} detail={detail} isLog={isLog} />,
      BL: <ReviewFactoringQuotationScheme showValue={showValue} detail={detail} isLog={isLog} />,
      ZZ: <ReviewLeaseQuotationScheme showValue={showValue} detail={detail} isLog={isLog} />,
      ZR: <ReviewAssignmentOfClaimsQuotationScheme showValue={showValue} detail={detail} isLog={isLog} />,
    }
    return Dom[bizType]
  }, [bizType, detail, showValue, isLog])

  return <div>{CurrentDom}</div>
}
export default Index
