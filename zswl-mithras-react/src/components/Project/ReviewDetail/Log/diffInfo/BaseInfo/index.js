import { useMemo } from 'react'
import {
  ReviewAssignmentOfClaimsBaseInfo,
  ReviewFactoringBaseInfo,
  ReviewLeaseBaseInfo,
  ReviewTurnLeaseBaseInfo,
} from '../../../BaseInfo'

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
      ZL: <ReviewLeaseBaseInfo {...commonProps} />,
      BL: <ReviewFactoringBaseInfo {...commonProps} />,
      ZZ: <ReviewTurnLeaseBaseInfo {...commonProps} />,
      ZR: <ReviewAssignmentOfClaimsBaseInfo {...commonProps} />,
    }
    return Dom[bizType]
  }, [bizType, detail, isLog])

  return <div>{CurrentDom}</div>
}
export default Index
