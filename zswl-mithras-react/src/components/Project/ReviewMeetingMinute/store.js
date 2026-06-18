import { App, PageStore, FormStore, Modal } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import { userIsProjSponsor } from '@/utils'
import { message } from 'antd'
import Api from './api'

const TypeTextMap = {
  REPAYMENT: '调整方案',
  EXTEND: '展期方案',
}
class Store {
  constructor() {
    makeAutoObservable(this)
  }
  options = App.getData().optionsType
  trackEventText = ''
  form = new FormStore({})
  init = {}
  projReviewType = 'PROJ_REVIEW_BASE'
  // 基本信息编辑态
  baseInfoShowValue = false
  setBaseInfoShowValue = (val) => {
    this.baseInfoShowValue = val
  }
  setProjReviewType = (val) => {
    this.projReviewType = val
  }
  // 是否主办
  isProjSponsor = false
  page = new PageStore({
    request: async ({ adjustId, businessVersion }) => {
      const res = await Api.getDetail({ adjustId, businessVersion })
      if (res) {
        this.isProjSponsor = userIsProjSponsor(res?.projSponsorUserId)
        return {
          ...res,
          typeText: TypeTextMap[res.afterLeaseAdjustType],
        }
      }
      return {}
    },
  })
  showValue = true
  setShowValue = (v) => {
    this.showValue = v
  }
  onSubmit = async (businessKey, processInstanceId) => {
    let data = this.form.getFieldValue()
    if (data.resolutionInfoDetails.length === 1 && !data.resolutionInfoDetails[0]?.otherMessage) {
      this.form.setFieldsValue({ resolutionInfoDetails: [] })
    }
    if (data.pledgeMeasuresDetails.length === 1 && !data.pledgeMeasuresDetails[0].otherMessage) {
      this.form.setFieldsValue({ pledgeMeasuresDetails: [] })
    }
    if (
      data.guaranteeMeasureDetails.length === 1 &&
      !data.guaranteeMeasureDetails[0].otherMessage
    ) {
      this.form.setFieldsValue({ guaranteeMeasureDetails: [] })
    }
    data.meetMinuteCode = `[${data.reportIssuanceYear}]评审字第[${data.meetMinuteSequence}]号`
    const { votingResult } = data
    await this.form.validateFields([{name:'votingResult'}])
    const validFields =
      votingResult === 'VETO' ? ['reportIssuanceYear', 'meetMinuteSequence'] : null

    await this.form
      .validateFields(validFields)
      .then((e) => {
        let functionCode =
          this.projReviewType === 'PROJ_REVIEW_BASE'
            ? 'projReviewMeetMinuteBaseInfoSubmit'
            : 'groupCreditReviewMeetMinuteBaseInfoSubmit'
        Api.postInfoSubmit(
          {
            id: this.init.id,
            projReviewId: businessKey,
            projFlowId: processInstanceId,
            ...data,
            specialContractTerms: JSON.stringify(data.specialContractTerms),
            conditionsBeforeDisbursement: JSON.stringify(data.conditionsBeforeDisbursement),
          },
          functionCode
        ).then(() => {
          this.onInit({
            projReviewId: businessKey,
            projFlowId: processInstanceId,
            projReviewType: this.projReviewType,
          })
          this.setBaseInfoShowValue(false)
          message.success('提交成功')
        })
      })
      .catch((e) => {
        this.setBaseInfoShowValue(true)
      })
  }
  onInit = async ({ projReviewId, projFlowId, isEffect = false, projReviewType = '' }) => {
    if (projReviewType) {
      this.setProjReviewType(projReviewType)
    }
    let functionCode =
      projReviewType === 'PROJ_REVIEW_BASE'
        ? 'projReviewMeetMinuteBaseInfoDetail'
        : 'groupCreditReviewMeetMinuteBaseInfoDetail'
    const res = await Api.postInfoDetail(
      {
        projReviewId: projReviewId,
        projFlowId: projFlowId,
        projReviewType: projReviewType,
      },
      functionCode
    )
    if (!res) {
      this.init = null
      return
    }
    let dataInit = {
      ...res,
      specialContractTerms:
        res.specialContractTermsList?.length > 0 ? res.specialContractTermsList : [''],
      conditionsBeforeDisbursement:
        res.conditionsBeforeDisbursementList?.length > 0
          ? res.conditionsBeforeDisbursementList
          : [''],
      resolutionInfoDetails: res.resolutionInfoDetails ?? [],
      pledgeMeasuresDetails: res.pledgeMeasuresDetails ?? [],
      guaranteeMeasureDetails: res.guaranteeMeasureDetails ?? [],
      preLeasePeriodFlag: res.preLeasePeriodFlag ?? 1,
      limitRequirement: res.limitRequirement ?? '满足风险策略及现行准入制度关于区域限额及客户集中度管控要求。'
    }
    if (res?.id) {
      this.onTrackEventList(res.id, res.businessKey, res.processInstanceId)
    }

    if (
      !dataInit.resolutionInfoDetails.some((item) => {
        return (
          item.otherMessage !== null &&
          item.otherMessage !== undefined &&
          item?.clientRole === 'OTHER'
        )
      })
    ) {
      dataInit.resolutionInfoDetails.unshift({ otherMessage: '', clientRole: 'OTHER' })
    }

    if (
      !dataInit.guaranteeMeasureDetails.some((item) => {
        return item.otherMessage !== null && item.otherMessage !== undefined
      })
    ) {
      dataInit.guaranteeMeasureDetails.unshift({ otherMessage: '' })
    }

    if (
      !dataInit.pledgeMeasuresDetails.some((item) => {
        return item.otherMessage !== null && item.otherMessage !== undefined
      })
    ) {
      dataInit.pledgeMeasuresDetails.unshift({ otherMessage: '' })
    }
    this.init = dataInit.id ? dataInit : null
    this.form.setFieldsValue({ ...dataInit })
    this.setBaseInfoShowValue(false)
  }
  onSave = async (businessKey, processInstanceId) => {
    let data = this.form.getFieldValue()

    if (data.resolutionInfoDetails.length === 1 && !data.resolutionInfoDetails[0].otherMessage) {
      this.form.setFieldsValue({ resolutionInfoDetails: [] })
    }
    if (data.pledgeMeasuresDetails.length === 1 && !data.pledgeMeasuresDetails[0].otherMessage) {
      this.form.setFieldsValue({ pledgeMeasuresDetails: [] })
    }
    if (
      data.guaranteeMeasureDetails.length === 1 &&
      !data.guaranteeMeasureDetails[0].otherMessage
    ) {
      this.form.setFieldsValue({ guaranteeMeasureDetails: [] })
    }
    data.meetMinuteCode = `[${data.reportIssuanceYear}]评审字第[${data.meetMinuteSequence}]号`
    data.specialContractTerms = JSON.stringify(data.specialContractTerms)
    data.conditionsBeforeDisbursement = JSON.stringify(data.conditionsBeforeDisbursement)
    let functionCode =
      this.projReviewType === 'PROJ_REVIEW_BASE'
        ? 'projReviewMeetMinuteBaseInfoModify'
        : 'groupCreditReviewMeetMinuteBaseInfoModify'
    await Api.postInfoModify(
      { projReviewId: businessKey, projFlowId: processInstanceId, ...data },
      functionCode
    )
    message.success('保存成功')
    this.onInit({
      id: data.id,
      projReviewId: businessKey,
      projFlowId: processInstanceId,
      isEffect: processInstanceId ? 0 : 1,
      projReviewType: this.projReviewType,
    })
  }
  onCustomers = (businessKey, meetMinuteId) => {
    let functionCode =
      this.projReviewType === 'PROJ_REVIEW_BASE'
        ? 'projReviewMeetMinuteGetRelatedCustomers'
        : 'groupCreditReviewMeetMinuteGetRelatedCustomers'
    const list = Api.postRelatedCustomers(
      { projReviewId: businessKey, meetMinuteId: meetMinuteId || '' },
      functionCode
    )
    return list
  }

  onTrackEventList = async (id, businessKey, processInstanceId) => {
    let functionCode =
      this.projReviewType === 'PROJ_REVIEW_BASE'
        ? 'projReviewMeetMinuteTrackEventList'
        : 'groupCreditReviewMeetMinuteTrackEventList'
    const res = await Api.postTrackEventList(
      { id: id, projReviewId: businessKey, projFlowId: processInstanceId },
      functionCode
    )
    this.trackEventText = res
  }
  onTrackEventClose = async (id) => {
    let functionCode =
      this.projReviewType === 'PROJ_REVIEW_BASE'
        ? 'projReviewTrackEventClose'
        : 'groupCreditReviewTrackEventClose'
    await Api.getTrackEventClose({ id: id }, functionCode)
  }
  cancelFlow = async () => {
    Modal.confirm({
      title: `是否取消操作？`,
      onOk: async () => {
        this.form.setFieldsValue(this.init)
        message.info('操作成功')
        this.setBaseInfoShowValue(false)
      },
    })
  }
}
export default Store
