import { useMemo } from 'react'
import Lease from '@/components/Project/ReviewDetail/QuotationScheme/FormConfig/Lease'
import Factoring from '@/components/Project/ReviewDetail/QuotationScheme/FormConfig/Factoring'
import AssignmentOfClaims from '@/components/Project/ReviewDetail/QuotationScheme/FormConfig/AssignmentOfClaims'

const Index = ({ bizType, detail, showValue, isLog }) => {
  const CurrentDom = useMemo(() => {
    if (!bizType) {
      return <div style={{ height: '200px' }}></div>
    }
    const Dom = {
      ZL: <Lease showValue={showValue} detail={detail} isLog={isLog} />,
      BL: <Factoring showValue={showValue} detail={detail} isLog={isLog} />,
      ZZ: <Lease showValue={showValue} detail={detail} isLog={isLog} />,
      ZR: <AssignmentOfClaims showValue={showValue} detail={detail} isLog={isLog} />,
    }
    return Dom[bizType]
  }, [bizType, detail, showValue, isLog])

  return <div>{CurrentDom}</div>
}
export default Index
