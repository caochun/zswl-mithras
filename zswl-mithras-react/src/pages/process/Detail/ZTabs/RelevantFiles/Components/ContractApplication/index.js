import { Collapse } from '@/components'
import { Empty } from 'antd'
import HeTong from '@/pages/contract/list/detail/HeTong'
import ZiLiao from '@/components/Contract/ContractMaterials'
import CheckMaterial from '@/components/Contract/ChangeMaterials'
import LeaseZiLiao from '@/components/Contract/LeaseMaterials'
import Api from '@/api/contract/contractDetail'
import StartRentZiLiao from '@/components/Contract/StartRentMaterials'
import SettlementProtocol from '@/components/Contract/SettlementProtocol'
import ChangeProtocol from '@/components/Contract/ChangeProtocol'
import BlankBlock from '@/pages/process/components/BlankBlock'
import { useEffect, useMemo, useState } from 'react'

const Index = ({ detailData, canEdit }) => {
  const { businessKey, businessVersion, taskActivityId, subModule, modelKey } = detailData
  const [baseDetailData, setBaseDetailData] = useState({})

  const isFormChangeType = modelKey === 'ContractModifyFlow'
  const { leaseType, bizType, isProjSponsor } = baseDetailData
  const auth = canEdit && isProjSponsor

  const commonProps = {
    id: businessKey,
    canEditFlag: auth,
    businessVersion,
  }

  const getDetail = async () => {
    const res = await Api.getBaseInfo({ id: businessKey })
    setBaseDetailData(res)
  }

  useEffect(() => {
    businessKey && getDetail()
  }, [businessKey])

  const Comp = useMemo(() => {
    if (subModule === 'CREATE_ALL' || subModule === 'MODIFY_ALL') {
      return (
        <>
          <Collapse header={'合同相关材料'}>
            <HeTong
              {...commonProps}
              taskActivityId={taskActivityId}
              modelKey={modelKey}
              isFormChangeType={isFormChangeType}
            ></HeTong>
            <CheckMaterial {...commonProps} canEdit={canEdit}></CheckMaterial>
          </Collapse>
          <BlankBlock></BlankBlock>
          <Collapse header={'资料清单'}>
            <ZiLiao {...commonProps}></ZiLiao>
            {leaseType === 'hui_zu' && bizType === 'ZL' && (
              <LeaseZiLiao {...commonProps}></LeaseZiLiao>
            )}
          </Collapse>
        </>
      )
    } else if (subModule === 'START_RENT' || modelKey === 'ContractStartRentAutoFlow') {
      return (
        <Collapse header={'起租材料'}>
          <StartRentZiLiao {...commonProps} contractId={businessKey}></StartRentZiLiao>
        </Collapse>
      )
    } else if (subModule === 'EARLY_SETTLE' || subModule === 'NORMAL_SETTLE') {
      return (
        <Collapse header={'补充协议'}>
          <SettlementProtocol {...commonProps} canEdit={canEdit}></SettlementProtocol>
        </Collapse>
      )
    } else if (
      ['EXTENSION', 'CHANGE_REPAY_PLAN', 'EARLY_REPAYMENT', 'LPR_CHANGE'].includes(subModule)
    ) {
      return (
        <Collapse header={'补充协议'}>
          <ChangeProtocol
            {...commonProps}
            canEdit={canEdit}
            changeType={subModule}
          ></ChangeProtocol>
        </Collapse>
      )
    }
    return <Empty></Empty>
  }, [subModule, modelKey, JSON.stringify(baseDetailData)])

  return <div>{Comp}</div>
}

export default Index
