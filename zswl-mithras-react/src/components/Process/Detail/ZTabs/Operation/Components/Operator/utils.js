import { message } from 'antd'

/** 是否集团授信项目：为真时才展示/提交「是否符合集团授信提款条件」动态表单（isGroupCredit 在接口返回的 dynamicFormData 内） */
export const shouldShowCreditWithdrawalForm = (dynamicFormData) => {
  const v = dynamicFormData?.isGroupCredit
  return v === true || v === 1 || v === '1'
}

export const getBackEndData = ({ dynamicFormKeyList, formData, backEndData, bcxxFormData, dynamicFormData }) => {
  if (dynamicFormKeyList.includes('projReview_chooseJudges')) {
    let arr = []
    formData?.userId?.forEach((v) => {
      arr.push({ userId: v?.value || v })
    })
    backEndData.dynamicFormData.projReview_chooseJudges = arr
    if (arr.length != 5) {
      message.info('项目评审委员必须是5个！')
      throw new Error('项目评审委员必须是5个！')
    }
  }

  // 法务经理复核
  if (dynamicFormKeyList.includes('projReview_lawManagerReview')) {
    let arr = {}
    if (formData?.userId) {
      arr = { userId: formData?.userId.value }
    }
    backEndData.dynamicFormData.projReview_lawManagerReview = arr
  }
  //资产管理复核
  if (dynamicFormKeyList.includes('afterLeaseCheckReport_assetManager')) {
    let arr = {}
    if (formData?.userId) {
      arr = { userId: formData?.userId.value }
    }
    backEndData.dynamicFormData.afterLeaseCheckReport_assetManager = arr
  }
  if (dynamicFormKeyList.includes('risk_control_payment_assetManager')) {
    let arr = {}
    if (formData?.userId) {
      arr = { userId: formData?.userId.value }
    }
    backEndData.dynamicFormData.risk_control_payment_assetManager = arr
  }

  if (dynamicFormKeyList.includes('early_warning_monitor_after_loan_assetManager')) {
    let arr = {}
    if (formData?.userId) {
      arr = { userId: formData?.userId.value }
    }
    backEndData.dynamicFormData.early_warning_monitor_after_loan_assetManager = arr
  }

  if (dynamicFormKeyList.includes('risk_opinion_dispose_after_loan_assetManager')) {
    let arr = {}
    if (formData?.userId) {
      arr = { userId: formData?.userId.value }
    }
    backEndData.dynamicFormData.risk_opinion_dispose_after_loan_assetManager = arr
  }

  if (dynamicFormKeyList.includes('projReview_showDirectors')) {
    let arr = []
    formData?.userId?.forEach((v) => {
      arr.push({ userId: v.userId })
    })
    backEndData.dynamicFormData.projReview_showDirectors = arr
  }
  // 是否需要董事会
  if (dynamicFormKeyList.includes('projReview_setNeedBorad')) {
    backEndData.dynamicFormData.projReview_setNeedBorad = { needBorad: formData.needBorad }
  }
  // 五级分类
  if (dynamicFormKeyList.includes('asset_classify_qualitative_adjust')) {
    backEndData.dynamicFormData.asset_classify_qualitative_adjust = formData.asset_classify_qualitative_adjust
    backEndData.dynamicFormData.asset_classify_result = formData.asset_classify_result
  }
  // 舆情
  if (dynamicFormKeyList.includes('risk_opinion_asset_management')) {
    backEndData.dynamicFormData.warnLevel = formData.warnLevel
  }
  // 项目立项，风控经理多选
  if (dynamicFormKeyList.includes('projEstablish_setRiskManager')) {
    let restData = bcxxFormData?.userId?.map((item) => {
      return { userId: +item.value }
    })
    backEndData.dynamicFormData.projEstablish_setRiskManager = restData
  }
  // 项目评审评审会秘书
  if (dynamicFormKeyList.includes('projReview_setReviewMeetingPlanDate')) {
    const val = formData.reviewMeetingPlanDate ? formData.reviewMeetingPlanDate.format('YYYY-MM-DD') : undefined
    backEndData.dynamicFormData.projReview_setReviewMeetingPlanDate = {
      reviewMeetingPlanDate: val,
    }
  }
  // 舆情
  if (dynamicFormKeyList.includes('risk_opinion_handle_type')) {
    backEndData.dynamicFormData.handleResult = formData.handleResult
  }
  if (dynamicFormKeyList.includes('follow_up_rental_inspection_form')) {
    backEndData.dynamicFormData.follow_up_rental_inspection_form = {
      nextCheckWay: bcxxFormData.nextCheckWay,
      nextDeadline: bcxxFormData.nextDeadline ? moment(bcxxFormData.nextDeadline).format('YYYY-MM-DD') : undefined,
    }
  }
  // 项目评审董事会秘书
  if (dynamicFormKeyList.includes('projReview_setDirectorMeetingPlanDate')) {
    const val = formData.directorMeetingPlanDate ? formData.directorMeetingPlanDate.format('YYYY-MM-DD') : undefined
    backEndData.dynamicFormData.projReview_setDirectorMeetingPlanDate = {
      directorMeetingPlanDate: val,
    }
  }
  // 合同变更-提前还款流程财务确认节点增加动态表单
  if (dynamicFormKeyList.includes('contract_early_repay_financial_confirm')) {
    backEndData.dynamicFormData.contract_early_repay_financial_confirm = {
      isPass: formData.isPass,
    }
  }
  // 合同变更-提前还款流程项目经理修改节点增加动态表单
  if (dynamicFormKeyList.includes('contract_early_repay_start_user_modify')) {
    backEndData.dynamicFormData.contract_early_repay_start_user_modify = {}
  }

  // 定价委员会议
  if (dynamicFormKeyList.includes('projReview_pricingChooseApproveAuth')) {
    backEndData.dynamicFormData.projReview_pricingChooseApproveAuth = {
      approvalUserIdList: formData.approvalUserIdList?.map((item) => item?.value || item),
      approveAuth: formData.approveAuth,
    }
  }
  // 项目分类  项目评审流程，秘书会票节点  这个节点增加项目分类
  if (dynamicFormKeyList.includes('projReview_setProjectClassify')) {
    if (!bcxxFormData?.projectClassify) {
      message.info('请选择项目分类！')
      throw new Error('请选择项目分类！')
    }
    backEndData.dynamicFormData.projReview_setProjectClassify = {
      projectClassify: bcxxFormData?.projectClassify,
    }
  }
  // 合同起租金FTP
  if (dynamicFormKeyList.includes('contract_setPaymentFtp')) {
    backEndData.dynamicFormData.contract_setPaymentFtp = {
      paymentList: formData.paymentList,
    }
  }
  // 合同印花税财务经理节点
  if (dynamicFormKeyList.includes('contract_updateStampDuty')) {
    backEndData.dynamicFormData.contract_updateStampDuty = {
      dutyFormRSPList: formData.dutyFormRSPList,
    }
  }
  // 付款申请最低irr
  if (dynamicFormKeyList.includes('payment_updateIRR')) {
    backEndData.dynamicFormData.payment_updateIRR = {
      lowestIrr: formData.lowestIrr,
    }
  }
  //终审人
  if (dynamicFormKeyList.includes('projReview_pricingChooseAdjudicator')) {
    backEndData.dynamicFormData.projReview_pricingChooseAdjudicator = {
      adjudicator: formData?.adjudicator,
    }
  }
  // 项目评审、授信评审：增加「项目批复金额（元）」
  if (dynamicFormKeyList.includes('projReview_setApprovedAmount')) {
    backEndData.dynamicFormData.projReview_setApprovedAmount = {
      approvedAmount: formData?.approvedAmount,
    }
  }
  if (dynamicFormKeyList.includes('group_projReview_setApprovedAmount')) {
    backEndData.dynamicFormData.group_projReview_setApprovedAmount = {
      approvedAmount: formData?.approvedAmount,
    }
  }
  if (dynamicFormKeyList.includes('ftp_chooseJudges')) {
    backEndData.dynamicFormData.ftp_chooseJudges = (formData?.userId || []).map((v) => ({
      ...v,
      userId: v?.value,
    }))
  }
  // 是否符合集团授信提款条件（仅集团授信项目）
  if (dynamicFormKeyList.includes('projReview_setCreditWithdrawal') && shouldShowCreditWithdrawalForm(dynamicFormData)) {
    backEndData.dynamicFormData.projReview_setCreditWithdrawal = {
      groupCreditWithdrawalRisk: formData?.groupCreditWithdrawalRisk,
      groupCreditWithdrawalLaw: formData?.groupCreditWithdrawalLaw,
    }
  }
}
