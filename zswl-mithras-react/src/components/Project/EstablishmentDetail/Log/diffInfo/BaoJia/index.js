import { useMemo } from 'react'
import {
  EstablishmentAssignmentOfClaimsQuotationScheme,
  EstablishmentFactoringQuotationScheme,
  EstablishmentLeaseQuotationScheme,
} from '../../../QuotationScheme/ProjectEstablishmentQuotationScheme'

const Index = ({ bizType, detail, showValue, isLog }) => {
  const CurrentDom = useMemo(() => {
    if (!bizType) {
      return <div style={{ height: '200px' }}></div>
    }
    const Dom = {
      ZL: <EstablishmentLeaseQuotationScheme showValue={showValue} detail={detail} isLog={isLog} />,
      BL: <EstablishmentFactoringQuotationScheme showValue={showValue} detail={detail} isLog={isLog} />,
      ZZ: <EstablishmentLeaseQuotationScheme showValue={showValue} detail={detail} isLog={isLog} />,
      ZR: <EstablishmentAssignmentOfClaimsQuotationScheme showValue={showValue} detail={detail} isLog={isLog} />,
    }
    return Dom[bizType]
  }, [bizType, detail, showValue, isLog])

  return <div>{CurrentDom}</div>
}
export default Index
