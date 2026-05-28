import { useMemo } from 'react'
import Lease from '@/pages/contract/list/detail/BaseInfo/FormConfig/Lease'
import AssignmentOfClaims from '@/pages/contract/list/detail/BaseInfo/FormConfig/AssignmentOfClaims'
import Factoring from '@/pages/contract/list/detail/BaseInfo/FormConfig/Factoring'
import TurnLease from '@/pages/contract/list/detail/BaseInfo/FormConfig/TurnLease'

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
      ZL: <Lease {...commonProps} />,
      BL: <Factoring {...commonProps} />,
      ZZ: <TurnLease {...commonProps} />,
      ZR: <AssignmentOfClaims {...commonProps} />,
    }
    return Dom[bizType]
  }, [bizType, detail, isLog])

  return <div>{CurrentDom}</div>
}
export default Index
