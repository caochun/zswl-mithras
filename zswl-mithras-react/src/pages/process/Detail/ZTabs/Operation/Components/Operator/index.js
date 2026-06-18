import { Form } from '@zswl/components'
import { getQuery } from '@zswl/admin'
import { Radio, Input } from 'antd'
import { useEffect, useMemo, useRef, useState } from 'react'
import styles from './index.less'
import SendDuplicateModal from '../SendDuplicateModal'
import moment from 'moment'
import BackReason from './Components/BackReason'
import CheckCorpSubject from './Components/CheckCorpSubject'
import CheckPaymentTimeOut from './Components/CheckPaymentTimeOut'
import { useFlowData } from '@/utils/domains/process/ProcessFlowContext'
import DynamicBtn from './Components/DynamicBtn'
import DynamicForm from './Components/DynamicForm'
import { shouldShowCreditWithdrawalForm } from './utils'
import Transfer from './Components/Transfer'
import { FounderSelect } from '@/components'
import store from './store'
import MessageModal from './Components/MessageModal'

const { Item } = Form
const { TextArea } = Input

const formLayout = {
  labelCol: { span: 4 },
  wrapperCol: { span: 16 },
}
const tailLayout = {
  wrapperCol: { offset: 4, span: 16 },
}

export default function Index() {
  const { detailData: detail, isNewLayout, pathname, isEditing } = useFlowData()
  const {
    ccTabShowFlag,
    processInstanceId,
    taskId,
    dynamicFormData,
    dynamicFormKeyList,
    businessKey,
    ccTabReadOnlyFlag,
    ccUerList,
    taskActivityId
  } = detail

  const [show, setShow] = useState(false)
  const [assetClassifyDisabled, setAssetClassifyDisabled] = useState(true)
  const backRef = useRef()
  const isReceivePage = pathname.indexOf('receive') > -1

  useEffect(() => {
    store.detailData = detail
  })

  useEffect(() => {
    if (pathname) {
      store.pathname = pathname
    }
  }, [pathname])

  useEffect(() => {
    // 董事会成员
    if (dynamicFormKeyList.includes('projReview_showDirectors')) {
      store.operatorForm.setFieldsValue({
        userId: Object.keys(dynamicFormData).length > 0 && dynamicFormData.projReview_showDirectors,
      })
    }
    // 定价委员会
    if (dynamicFormKeyList.includes('ftp_chooseJudges')) {
      store.operatorForm.setFieldsValue({
        userId: Object.keys(dynamicFormData).length > 0 && dynamicFormData.ftp_chooseJudges,
      })
    }
    // 项目评审委员
    if (dynamicFormKeyList.includes('projReview_chooseJudges')) {
      store.operatorForm.setFieldsValue({
        userId: dynamicFormData.projReview_chooseJudges.map((item) => +item.value),
      })
    }
    // IRR
    if (dynamicFormKeyList.includes('contract_changeIrrForm')) {
      store.operatorForm.setFieldsValue({
        contractIrr: dynamicFormData.contract_changeIrrForm,
      })
    }

    // 资产管理复核
    if (dynamicFormKeyList.includes('afterLeaseCheckReport_assetManager')) {
      store.operatorForm.setFieldsValue({
        // userId: dynamicFormData.afterLeaseCheckReport_assetManager.length > 0 ? dynamicFormData.afterLeaseCheckReport_assetManager[0] : [],
      })
    }
    //法务经理复核
    if (dynamicFormKeyList.includes('projReview_lawManagerReview')) {
      store.operatorForm.setFieldsValue({
        // userId: dynamicFormData.projReview_lawManagerReview.length > 0 ? dynamicFormData.projReview_lawManagerReview[0] : [],
      })
    }
    // 五级分类,定性调整
    if (dynamicFormKeyList.includes('asset_classify_qualitative_adjust')) {
      const val = dynamicFormData.asset_classify_qualitative_adjust || 0
      store.operatorForm.setFieldsValue({
        asset_classify_qualitative_adjust: val,
        asset_classify_result: dynamicFormData.asset_classify_result,
      })
      setAssetClassifyDisabled(!val)
    }

    // 舆情,确认预警信号
    if (dynamicFormKeyList.includes('risk_opinion_asset_management')) {
      const val = dynamicFormData.warnLevel
      store.operatorForm.setFieldsValue({
        warnLevel: val,
      })
    }
    // 评审会预计召开时间
    if (dynamicFormKeyList.includes('projReview_setReviewMeetingPlanDate')) {
      const val = dynamicFormData.projReview_setReviewMeetingPlanDate?.reviewMeetingPlanDate
      store.operatorForm.setFieldsValue({
        reviewMeetingPlanDate: val && moment(val),
      })
    }
    // 董事会预计召开时间
    if (dynamicFormKeyList.includes('projReview_setDirectorMeetingPlanDate')) {
      const val = dynamicFormData.projReview_setDirectorMeetingPlanDate?.reviewMeetingPlanDate
      store.operatorForm.setFieldsValue({
        directorMeetingPlanDate: val && moment(val),
      })
    }
    // 定价委员会
    if (dynamicFormKeyList.includes('projReview_pricingChooseApproveAuth')) {
      const val = dynamicFormData.projReview_pricingChooseApproveAuth ?? {}
      store.operatorForm.setFieldsValue({
        approvalUserIdList: val.approvalUserIdList,
        approveAuth: val.approveAuth,
      })
    }
    // 合同起租金FTP
    if (dynamicFormKeyList.includes('contract_setPaymentFtp')) {
      store.operatorForm.setFieldsValue({
        paymentList: [...dynamicFormData.contract_setPaymentFtp?.paymentList],
      })
    }
    // 合同印花税
    if (dynamicFormKeyList.includes('contract_updateStampDuty')) {
      store.operatorForm.setFieldsValue({
        dutyFormRSPList: [...dynamicFormData.contract_updateStampDuty?.dutyFormRSPList],
      })
    }

    // 付款申请最低irr
    if (dynamicFormKeyList.includes('payment_updateIRR')) {
      store.operatorForm.setFieldsValue({
        lowestIrr: dynamicFormData.payment_updateIRR?.lowestIrr,
      })
    }
    if (dynamicFormKeyList.includes('projReview_setApprovedAmount')) {
      store.operatorForm.setFieldsValue({
        approvedAmount: dynamicFormData.projReview_setApprovedAmount?.approvedAmount,
      })
    }
    // ollow_up_rental_inspection_form
    if (dynamicFormKeyList.includes('follow_up_rental_inspection_form')) {
      const { nextCheckWay, nextDeadline } = dynamicFormData?.follow_up_rental_inspection_form ?? {}
      store.operatorForm.setFieldsValue({
        nextCheckWay,
        nextDeadline: nextDeadline ? moment(nextDeadline) : undefined,
      })
    }
    // 是否符合集团授信提款条件（风控/法务经理首次审批节点不回显，其余节点回显；仅集团授信项目展示）
    if (
      dynamicFormKeyList.includes('projReview_setCreditWithdrawal') &&
      shouldShowCreditWithdrawalForm(dynamicFormData) &&
      !['userTask_riskManager', 'userTask_lawManager'].includes(taskActivityId)
    ) {
      const { groupCreditWithdrawalRisk, groupCreditWithdrawalLaw } = dynamicFormData
      store.operatorForm.setFieldsValue({ groupCreditWithdrawalRisk, groupCreditWithdrawalLaw })
    }
    if (dynamicFormData?.opinion) {
      store.operatorForm.setFieldsValue({
        message: dynamicFormData?.opinion,
      })
    }
  }, [dynamicFormData, dynamicFormKeyList, taskActivityId])

  useEffect(() => {
    // 抄送人
    if (ccUerList && ccUerList.length > 0) {
      store.operatorForm.setFieldsValue({
        ccUserIdList: ccUerList,
      })
    }
  }, [ccUerList])

  useEffect(() => {
    store.operatorForm.setFieldsValue({
      attentionFlag: detail.attentionFlag ?? 1,
    })
  }, [detail])

  const onAssetClassifyQualitativeAdjust = (e) => {
    const { value } = e.target
    setAssetClassifyDisabled(!value)
    if (value === 0) {
      store.operatorForm.setFieldsValue({
        asset_classify_result: dynamicFormData.asset_classify_init_result,
      })
    }
  }

  return (
    <div className={styles.Operator}>
      <Form
        store={store.operatorForm}
        cache="false"
        preserve={false}
        layout={isNewLayout ? 'vertical' : 'horizontal'}
        {...(isNewLayout ? null : formLayout)}
      >
        <DynamicForm
          store={store}
          onAssetClassifyQualitativeAdjust={onAssetClassifyQualitativeAdjust}
          assetClassifyDisabled={assetClassifyDisabled}
          formStore={store.operatorForm}
        ></DynamicForm>
        <Item label={'审批意见'} name={'message'}>
          <TextArea showCount maxLength={2000} rows={4} />
        </Item>
        {ccTabShowFlag && (
          <Item label={'抄送'} name={'ccUserIdList'}>
            <FounderSelect
              disabled={!!ccTabReadOnlyFlag}
              mode="multiple"
              functionCode="selectfounder-3"
              params={{ job: undefined }}
            ></FounderSelect>
          </Item>
        )}
        {isReceivePage && (
          <Item label={'流程结束后通知我'} name={'attentionFlag'}>
            <Radio.Group>
              <Radio value={1}>是</Radio>
              <Radio value={0}>否</Radio>
            </Radio.Group>
          </Item>
        )}
        <Item {...(isNewLayout ? null : tailLayout)}>
          <DynamicBtn store={store} backRef={backRef} setShow={setShow} isEditing={isEditing}></DynamicBtn>
        </Item>
      </Form>
      {/* 协调 */}
      <SendDuplicateModal
        flag="collaborate"
        taskId={taskId}
        externalForm={store.operatorForm}
        detailData={{ ccTabReadOnlyFlag, ccUerList }}
        visible={show}
        callBack={() => {
          setShow(false)
        }}
      />
      <BackReason
        ref={backRef}
        detail={detail}
        submit={(data) => store.returnBack({ ...data, taskId, processInstanceId })}
      ></BackReason>
      {/* 风控经理:评审流程提交时检验客户管理模块财务报表录入是否完整 */}
      <CheckCorpSubject store={store} id={businessKey}></CheckCorpSubject>
      <CheckPaymentTimeOut store={store} id={businessKey}></CheckPaymentTimeOut>
      <MessageModal store={store} id={businessKey} />
      {/* 转办 */}
      <Transfer store={store} id={businessKey}></Transfer>
    </div>
  )
}
