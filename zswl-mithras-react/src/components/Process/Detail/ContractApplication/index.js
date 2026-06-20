import { useEffect, useMemo, useState, cloneElement } from 'react'
import { observer } from '@zswl/admin'
import {
  ContractApplicationDetail,
} from '@/components/Contract/ApplicationDetailPageEntries'
import {
  ContractChangeDetail as LPRchange,
} from '@/components/Contract/ChangeDetailEntries'
import {
  ContractCreateReceiptDetail as KSJJcreateReceipt,
} from '@/components/Contract/CreateReceiptDetailEntries'
import {
  ContractSettlementDetail as JQsettlement,
} from '@/components/Contract/SettlementDetailEntries'
import {
  ContractStartRentDetail as KSQZstartRent,
} from '@/components/Contract/StartRentDetailEntries'
import Api from '@/api/process/detail/flowDetailApi'

const ContractProcess = (props) => {
  const {
    canEditFlag,
    subModule,
    id,
    businessVersion,
    taskActivityId,
    modelKey,
    processInstanceId,
    taskStatus,
    startUserId,
    isNewLayout,
  } = props
  const [bizType, setBizType] = useState()

  const getContractBaseInfo = async (contractId, version) => {
    const res = await Api.contractBaseInfo({ id: contractId, businessVersion: version })
    setBizType(res.bizType?.value)
  }
  useEffect(() => {
    if (id && !['CREATE_ALL', 'MODIFY_ALL'].includes(subModule)) {
      getContractBaseInfo(id, businessVersion)
    }
  }, [id, subModule, businessVersion])

  const renderContractType = useMemo(() => {
    const queryProps = {
      bizType,
      businessVersion,
      taskActivityId,
      modelKey,
      processInstanceId,
      taskStatus,
      canEditFlags: canEditFlag ? 'true' : 'false',
    }

    // 合同-创建
    if (subModule == 'CREATE_ALL') {
      return <ContractApplicationDetail params={{ id }} query={queryProps} />
    }
    //合同-起租
    if (subModule == 'START_RENT' || modelKey === 'ContractStartRentAutoFlow') {
      return (
        <KSQZstartRent
          params={{ id }}
          query={{
            ...queryProps,
          }}
        />
      )
    }
    // 合同-新增借据
    if (subModule == 'ADD_NEW_RECEIPT' || modelKey === 'ContractAddNewReceiptAutoFlow') {
      return <KSJJcreateReceipt params={{ id }} query={queryProps} />
    }
    // 合同变更-LPR调整
    if (subModule == 'LPR_CHANGE') {
      return (
        <LPRchange
          params={{ id }}
          query={{
            ...queryProps,
            changeType: 'LPR_CHANGE',
          }}
        />
      )
    }
    // 合同变更-提前还款
    if (subModule == 'EARLY_REPAYMENT') {
      return (
        <LPRchange
          params={{ id }}
          query={{
            ...queryProps,
            changeType: 'EARLY_REPAYMENT',
          }}
        />
      )
    }
    // 合同变更- 展期
    if (subModule == 'EXTENSION') {
      return (
        <LPRchange
          params={{ id }}
          query={{
            ...queryProps,
            changeType: 'EXTENSION',
          }}
        />
      )
    }
    // 合同变更-调整还款计划
    if (subModule == 'CHANGE_REPAY_PLAN') {
      return (
        <LPRchange
          params={{ id }}
          query={{
            ...queryProps,
            changeType: 'CHANGE_REPAY_PLAN',
          }}
        />
      )
    }
    // 合同变更- 其它
    if (subModule == 'MODIFY_ALL') {
      return (
        <ContractApplicationDetail
          params={{ id }}
          query={{
            ...queryProps,
            formChangeOther: 'true',
          }}
        />
      )
    }
    // 合同-提前结清
    if (subModule == 'EARLY_SETTLE' || modelKey === 'ContractEarlySettleConfirmFlow') {
      return (
        <JQsettlement
          params={{ id }}
          query={{
            ...queryProps,
            planType: 'SETTLE_IN_ADVANCE',
          }}
        />
      )
    }
    // 合同-正常结清
    if (subModule == 'NORMAL_SETTLE') {
      return (
        <JQsettlement
          params={{ id }}
          query={{
            ...queryProps,
            planType: 'SETTLE_NORMAL',
            startUserId: startUserId,
          }}
        />
      )
    }
    return null
  }, [subModule, bizType, processInstanceId, taskStatus])

  return <div>{cloneElement(renderContractType, { isNewLayout })}</div>
}
export default observer(ContractProcess)
