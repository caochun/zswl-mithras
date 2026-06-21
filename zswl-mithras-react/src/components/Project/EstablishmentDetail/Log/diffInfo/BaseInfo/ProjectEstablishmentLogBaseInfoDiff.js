import { useMemo } from 'react'

import {
  EstablishmentAssignmentOfClaimsBaseInfo,
  EstablishmentFactoringBaseInfo,
  EstablishmentLeaseBaseInfo,
  EstablishmentTurnLeaseBaseInfo,
} from '../../../BaseInfo/ProjectEstablishmentBaseInfo'

const ProjectEstablishmentLogBaseInfoDiff = ({ bizType, detail, showValue, isLog }) => {
  const CurrentDom = useMemo(() => {
    if (!bizType) {
      return <div style={{ height: '200px' }}></div>
    }
    const Dom = {
      ZL: <EstablishmentLeaseBaseInfo showValue={showValue} detail={detail} isLog={isLog} />,
      BL: <EstablishmentFactoringBaseInfo showValue={showValue} detail={detail} isLog={isLog} />,
      ZZ: <EstablishmentTurnLeaseBaseInfo showValue={showValue} detail={detail} isLog={isLog} />,
      ZR: <EstablishmentAssignmentOfClaimsBaseInfo showValue={showValue} detail={detail} isLog={isLog} />,
    }
    return Dom[bizType]
  }, [bizType, detail, showValue, isLog])

  return <div>{CurrentDom}</div>
}
export default ProjectEstablishmentLogBaseInfoDiff
