import { useMemo } from 'react'
import Lease from '@/components/Contract/BaseInfo/FormConfig/Lease'
import AssignmentOfClaims from '@/components/Contract/BaseInfo/FormConfig/AssignmentOfClaims'
import Factoring from '@/components/Contract/BaseInfo/FormConfig/Factoring'
import TurnLease from '@/components/Contract/BaseInfo/FormConfig/TurnLease'

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
