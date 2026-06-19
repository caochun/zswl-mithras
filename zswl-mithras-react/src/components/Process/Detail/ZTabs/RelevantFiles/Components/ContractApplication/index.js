import Collapse from '@/components/Collapse'
import { Empty } from 'antd'
import {
  ContractChangeMaterials as CheckMaterial,
  ContractChangeProtocol as ChangeProtocol,
  ContractLeaseMaterials as LeaseZiLiao,
  ContractMaterials as ZiLiao,
  ContractSettlementProtocol as SettlementProtocol,
  ContractStartRentMaterials as StartRentZiLiao,
  ContractText as HeTong,
} from '@/components/Contract/MaterialsEntries'
import Api from '@/api/process/detail/contractDetailApi'
import BlankBlock from '../../../../../BlankBlock'
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
