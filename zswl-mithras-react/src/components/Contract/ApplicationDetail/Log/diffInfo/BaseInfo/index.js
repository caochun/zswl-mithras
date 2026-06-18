import { useMemo } from 'react'
import {
  AssignmentOfClaimsBaseInfo,
  FactoringBaseInfo,
  LeaseBaseInfo,
  TurnLeaseBaseInfo,
} from '@/components/Contract/BaseInfo'

const Index = ({ bizType, detail, isLog }) => {
  const CurrentDom = useMemo(() => {
    if (!bizType) {
      return <div style={{ height: '200px' }}></div>
    }
    const commonProps = {
      detail,
      isLog,
      canEdit: false,
    }
    const Dom = {
      ZL: <LeaseBaseInfo {...commonProps} />,
      BL: <FactoringBaseInfo {...commonProps} />,
      ZZ: <TurnLeaseBaseInfo {...commonProps} />,
      ZR: <AssignmentOfClaimsBaseInfo {...commonProps} />,
    }
    return Dom[bizType]
  }, [bizType, detail, isLog])

  return <div>{CurrentDom}</div>
}
export default Index
