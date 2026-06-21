import { ApprovalAction as Approval } from '@/components/Actions'
import { LeaseApprovalConfirmAction as ApprovalConfirm } from '@/components/Lease/ApprovalConfirmEntries'
import { BusinessInfoCheck } from '@/components/BusinessInfoCheck/BusinessInfoCheckEntries'
import {
  checkCreditDate,
  checkReviewMaterialComments,
  postPaymentCheckApplyAmount,
} from '@/utils/domains/cpm/PaymentApplicationUtils'
import { useFlowData } from '@/utils/domains/process/ProcessFlowContext'
import mathjs from '@/utils/math'
import { observer } from '@zswl/admin'
import { Button, Form } from '@zswl/components'
import { Dropdown, Menu, Modal, Space, message } from 'antd'
import { useMemo, useRef } from 'react'
import OverturnButton from './OverturnButton'
import { CpmPaymentApplicationPublicCheckModal as PublicCheckModal } from '@/components/Cpm/PaymentApplicationPublicCheckEntries'

const { Item } = Form

const btnList = ['ZL_PR_RE_VOTE', 'VOTE_BACK']
const agreeList = ['AGREE', 'SUBMIT', 'VOTE_AGREE', 'VOTE_CONDITION_AGREE', 'VOTE_DISAGREE', 'VOTE_ABSTAIN', 'ZL_PR_RECONSIDER']

const ProcessDynamicButtonGroup = ({ store, backRef, setShow, isEditing }) => {
  const { detailData, isRiskManagerProj, triggerCallback } = useFlowData()
  const businessInfoCheckRef = useRef(null)
  const {
    businessKey,
    mainModule,
    multiBackOptionFlag,
    modelKey,
    dynamicButtonList,
    processInstanceId,
    taskId,
    canBackNodeList,
    taskActivityId,
    curTaskActivityIds,
    ccTabReadOnlyFlag,
    dynamicFormKeyList,
    dynamicFormData,
    ccUerList,
  } = detailData
  const isLeaseFlow = ['LeaseCreateFlow', 'LeaseModifyFlow'].includes(modelKey)

  const btnlists = useMemo(() => {
    let arr = []
    canBackNodeList?.forEach((v) => {
      arr.push({ key: v.activityId, label: v.name })
    })
    return arr ?? []
  }, [canBackNodeList])

  const menu = (
    <Menu
      onClick={(e) => {
        store.backToStartUser(e, 1)
      }}
      items={[
        { key: '1', label: '逐级审批' },
        { key: '2', label: '直达本节点' },
      ]}
    />
  )

  const BackMenu = (
    <Menu
      onClick={(e) => {
        store.backToStep({ activityId: e.key, buttonKey: 'BACK_TO_STEP' })
      }}
      items={btnlists}
    />
  )

  const renderBtn = (buttonList) => {
    const btns = buttonList?.map((v, i) => {
      if (!v) return
      const defaultBtnProps = {
        disabled: !v.clickFlag,
        key: i,
        type: 'primary',
      }

      // 转办
      if (v.buttonKey == 'TRANSFER') {
        return <Button onClick={store.transferModal.open}>转办</Button>
      }
      if (v.buttonKey === 'FOLLOWING') {
        return <OverturnButton store={store} disabled={!v.clickFlag} buttonName={v.buttonName} />
      }
      if (v.buttonKey == 'RANDOM_RETURN') {
        // 租赁物退回任意节点
        return (
          <ApprovalConfirm
            {...defaultBtnProps}
            isEffect={isLeaseFlow}
            onClick={() => backRef.current?.modal.open({ processInstanceId, taskId })}
            params={{ id: businessKey }}
            text={v.buttonName}
          />
        )
      }
      // 关闭流程
      if (v.buttonKey == 'CANCEL') {
        return (
          <Button disabled={!v.clickFlag} key={i} onClick={store.cancelProcess}>
            {v.buttonName}
          </Button>
        )
      }
      // 撤回
      if (v.buttonKey == 'WITHDRAW_TASK') {
        return (
          <Button {...defaultBtnProps} onClick={store.withdrawTask}>
            {v.buttonName}
          </Button>
        )
      }
      // 不同意
      if (v.buttonKey == 'DISAGREE') {
        return (
          <Button {...defaultBtnProps} onClick={store.reject}>
            {v.buttonName}
          </Button>
        )
      }
      // 撤回(发起人)
      if (v.buttonKey == 'WITHDRAW_START_USER') {
        return (
          <Button {...defaultBtnProps} onClick={store.withdrawToStartUser}>
            {v.buttonName}
          </Button>
        )
      }
      // 有条件同意(特殊)、 不同意(特殊) 、不同意(退回发起人)
      if (['DISAGREE_BACK_TO_START_USER', 'ZL_PR_MEETING_SECRETARY_DISAGREE', 'ZL_PR_CONDITION_AGREE', 'ZL_PR_AGREE'].includes(v.buttonKey)) {
        return (
          <Button
            {...defaultBtnProps}
            onClick={() => {
              store.backToStartUser('', 2, v.buttonKey)
            }}
          >
            {v.buttonName}
          </Button>
        )
      }

      // 协同
      if (v.buttonKey == 'COLLABORATE') {
        return (
          <ApprovalConfirm
            {...defaultBtnProps}
            isEffect={isLeaseFlow}
            params={{ id: businessKey }}
            isClear={ccTabReadOnlyFlag && ccUerList}
            onClear={() => {
              store.operatorForm.setFieldsValue({
                ccUserIdList: [],
              })
            }}
            text={v.buttonName}
            onClick={() => setShow(true)}
          />
        )
      }
      // 退回发起人
      if (v.buttonKey == 'BACK_TO_START_USER' && multiBackOptionFlag) {
        return (
          <Dropdown key={i} disabled={!v.clickFlag} overlay={menu} placement="bottomLeft" arrow>
            <Button>{v.buttonName}</Button>
          </Dropdown>
        )
      }
      // 退回发起人
      if (v.buttonKey == 'BACK_TO_START_USER' && !multiBackOptionFlag) {
        return (
          <Button
            disabled={!v.clickFlag}
            key={i}
            onClick={() => {
              store.backToStartUser('', 2, v.buttonKey)
            }}
          >
            {v.buttonName}
          </Button>
        )
      }

      // 退回(退回指定节点)
      if (v.buttonKey == 'BACK_TO_STEP') {
        return (
          <Dropdown key={i} overlay={BackMenu} disabled={!v.clickFlag} placement="bottomLeft" arrow>
            <Button>{v.buttonName}</Button>
          </Dropdown>
        )
      }
      //  重新提交审批(特殊)、退回(投票)
      if (btnList.includes(v.buttonKey)) {
        return (
          <Button
            {...defaultBtnProps}
            onClick={() => {
              store.backToStep({ buttonKey: v.buttonKey })
            }}
          >
            {v.buttonName}
          </Button>
        )
      }
      // 同意、提交、同意(投票)、有条件同意(投票)、不同意(投票)、弃权(投票)、复议(特殊)
      if (agreeList.includes(v.buttonKey)) {
        const PaymentCreateFlow = ['PaymentCreateFlow'].includes(modelKey)
        const checkCreditDateButton = ['SUBMIT', 'AGREE'].includes(v.buttonKey)
        const activeList = ['userTask_startUser', 'Activity_1h93upw', 'userTask_loanReviewPost']
        const checkCreditDateActive = activeList.includes(taskActivityId) || activeList.includes(curTaskActivityIds)

        const isIrrValidate =
          (modelKey === 'ContractChangeRepayPlanFlow' && taskActivityId === 'Activity_0ymd3mm') ||
          (modelKey === 'ContractModifyFlow' && taskActivityId === 'Activity_1drt6mw') ||
          (modelKey === 'ContractLPRChangeFlow' && taskActivityId === 'userTask_financeManager') ||
          (modelKey === 'ContractExtensionFlow' && taskActivityId === 'Activity_0w63b6g')
        const handleSubmit = async (extParams) => {
          if (isRiskManagerProj) {
            const ret = await checkReviewMaterialComments({ id: businessKey })
            if (ret.checkResult === 0) {
              Modal.error({
                title: '提示',
                content: ret.checkResultMsg,
                okText: '确定',
              })
              return
            }
          }
          const approvalMessage = store.operatorForm.getFieldValue('message')
          if (isIrrValidate && !approvalMessage && dynamicFormKeyList.findIndex((item) => item === 'contract_changeIrrForm') !== -1) {
            const { lowestIrr, averageIrr } = dynamicFormData['contract_changeIrrForm']
            if (mathjs.greaterThan(lowestIrr, averageIrr)) {
              message.warning('合同加权平均IRR低于最低IRR要求，需填写审批意见！')
              return
            }
          }
          // 详情页提交校验
          if (triggerCallback && triggerCallback()) {
            return
          }
          if (isEditing) {
            message.warning('请先保存保证金退抵方案！')
            return
          }
          if (checkCreditDateButton && checkCreditDateActive && PaymentCreateFlow) {
            await checkCreditDate({ paymentId: businessKey })
          }
          if (modelKey === 'PaymentCreateFlow' && (taskActivityId === 'userTask_riskDeptMaster' || taskActivityId === 'userTask_startUser')) {
            await postPaymentCheckApplyAmount({ paymentId: businessKey, activityId: taskActivityId })
          }
          return store.beforePassProcess({
            buttonKey: v.buttonKey,
            onlyCheck: false,
            extParams,
          })
        }
        if (['SUBMIT', 'AGREE'].includes(v.buttonKey) && isLeaseFlow) {
          return (
            <ApprovalConfirm
              {...defaultBtnProps}
              isEffect={isLeaseFlow}
              params={{ id: businessKey, taskActivityId, processInstanceId }}
              text={v.buttonName}
              onClick={handleSubmit}
            />
          )
        }
        const checkActive =
          ['userTask_startUser', 'userTask_loanReviewPost'].includes(taskActivityId) ||
          ['userTask_startUser', 'userTask_loanReviewPost'].includes(curTaskActivityIds)

        if (PaymentCreateFlow && checkActive) {
          return (
            <>
              <Button {...defaultBtnProps} onClick={async (e) => store.checkPublic(handleSubmit)}>
                {v.buttonName}
              </Button>
              <PublicCheckModal modal={store.publicModal} submit={handleSubmit} id={businessKey} />
            </>
          )
        }
        const isCreditReportSelectFlow = ['CreditReportSelectFlow'].includes(modelKey)
        if (isCreditReportSelectFlow) {
          return (
            <>
              <Button
                {...defaultBtnProps}
                onClick={async (e) => {
                  businessInfoCheckRef.current.setSubmitFn(() => handleSubmit())
                  await businessInfoCheckRef.current.store.checkCompare()
                }}
              >
                {v.buttonName}
              </Button>
              <BusinessInfoCheck creditSearchId={businessKey} needOption={false} needButton={false} ref={businessInfoCheckRef} />
            </>
          )
        }
        return (
          <Approval
            {...defaultBtnProps}
            // 复议(特殊)
            isEffect={v.buttonKey === 'ZL_PR_RECONSIDER'}
            text={v.buttonName}
            params={{
              moduleType: mainModule,
              mainId: businessKey,
              remarkType: 'RECONSIDER',
            }}
            onClick={handleSubmit}
          />
        )
      } else {
        return <Button {...defaultBtnProps}>{v.buttonName}</Button>
      }
    })
    return [...btns]
  }
  return (
    <Item noStyle>
      <Space size={[8, 16]} wrap>
        {renderBtn(dynamicButtonList)}
      </Space>
    </Item>
  )
}

export default observer(ProcessDynamicButtonGroup)
