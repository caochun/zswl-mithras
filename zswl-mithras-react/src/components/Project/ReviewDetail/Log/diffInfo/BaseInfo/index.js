// import { useMemo } from 'react'

// import Lease from '@/components/Project/ReviewDetail/BaseInfo/FormConfig/Lease'
// import Factoring from '@/components/Project/ReviewDetail/BaseInfo/FormConfig/Factoring'
// import AssignmentOfClaims from '@/components/Project/ReviewDetail/BaseInfo/FormConfig/AssignmentOfClaims'
// import TurnLease from '@/components/Project/ReviewDetail/BaseInfo/FormConfig/TurnLease'

// const Index = ({ bizType, detail, showValue, isLog }) => {
//   const CurrentDom = useMemo(() => {
//     if (!bizType) {
//       return <div style={{ height: '200px' }}></div>
//     }
//     const Dom = {
//       ZL: <Lease showValue={showValue} detail={detail} isLog={isLog} />,
//       BL: <Factoring showValue={showValue} detail={detail} isLog={isLog} />,
//       ZZ: <TurnLease showValue={showValue} detail={detail} isLog={isLog} />,
//       ZR: <AssignmentOfClaims showValue={showValue} detail={detail} isLog={isLog} />,
//     }
//     return Dom[bizType]
//   }, [bizType, detail, showValue, isLog])

//   return <div>{CurrentDom}</div>
// }
// export default Index

import { useMemo } from 'react'
import Lease from '@/components/Project/ReviewDetail/BaseInfo/FormConfig/Lease'
import AssignmentOfClaims from '@/components/Project/ReviewDetail/BaseInfo/FormConfig/AssignmentOfClaims'
import Factoring from '@/components/Project/ReviewDetail/BaseInfo/FormConfig/Factoring'
import TurnLease from '@/components/Project/ReviewDetail/BaseInfo/FormConfig/TurnLease'

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
