import { makeAutoObservable, history, getQuery } from '@zswl/admin'
import { FormStore, PageStore, Modal } from '@zswl/components'
import { message } from 'antd'
import Api from '@/api/project/component/PriceDetail/api'
import { compareDetail, formScrollToField, hasValue } from '@/utils'
import cashApi from '@/api/project/projectPriceCashflow'
import approvalRemarkApi from '@/api/project/approvalRemarkApi'
import mathjs from '@/utils/math'
import reviewApi from '@/api/project/projReviewDetail'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  QSShowValue = true // 报价方案编辑态
  QSZLShowValue = true // 报价方案-新增租赁编辑态
  bizType
  projectId
  res = {}
  isProjSponsor = false
  init = (newProject, projectId) => {
    // this.baseInfoShowValue = !newProject
    this.QSShowValue = !newProject
    this.QSZLShowValue = !newProject
    this.projectId = projectId
  }
  page = new PageStore({
    request: async (params) => {
      let remarkArr
      if (params?.approvalParams && params?.reconsiderParams) {
        remarkArr = await Promise.all([
          approvalRemarkApi.postReviewRemarkAll(params.approvalParams),
          approvalRemarkApi.postReviewRemarkAll(params.reconsiderParams),
        ])
          .then((res) => res)
          .catch((e) => {
            console.log(e)
            return [{}, {}]
          })
      }
      const [approvalDetail, reconsiderDetail] = remarkArr ?? []
      if (params.isFormApproval) {
        const api =
          params.modelKey === 'ProjReviewPricingApprovalFlow'
            ? Api.postProjectPricingBaseInfoDetailCompare
            : Api.postProjectBaseInfoDetailCompare
        const res = await api({
          id: params.id,
          processInstanceId: params.processInstanceId,
          businessVersion: params.businessVersion,
        })
        this.bizType = res.bizType?.value
        this.isProjSponsor = res.isProjSponsor?.value
        const { newDetail, isLog } = compareDetail(res)
        return {
          ...newDetail,
          isProjSponsor: res.isProjSponsor?.value,
          approvalDetail,
          reconsiderDetail,
          isLog,
        }
      } else {
        this.res = await Api.postProjectBaseInfoDetail({
          id: params.id,
          processInstanceId: params.processInstanceId,
        })
        this.bizType = this.res.bizType
        this.isProjSponsor = this.res.isProjSponsor
        return { ...this.res, approvalDetail, reconsiderDetail }
      }
    },
  })
  approvalLoading = false
  setQSShowValue = (val) => {
    this.QSShowValue = val
  }
  setQSZLShowValue = (val) => {
    this.QSZLShowValue = val
  }
  irrChange = () => {}

  evaluationSubjectIdValue = undefined
  setEvaluationSubjectIdValue = (value) => {
    this.evaluationSubjectIdValue = value
  }

  postProjectBaseInfoModify = async (values) => {
    const {
      area,
      lesseeInfo,
      guaranteeInfo,
      pledgorInfo,
      mortgagorInfo,
      debtorInfo,
      creditorInfo,
      supplierInfo,
      ...rest
    } = values
    const [province, city, district] = area ?? []

    let flag = true

    const getClientId = (items, needAppendClientType) => {
      if (JSON.stringify(items) === JSON.stringify([{}])) {
        return []
      }
      return items
        ?.map((item) => {
          if (!needAppendClientType && !item?.clientType && hasValue(item?.clientId)) {
            flag = false
          }
          return {
            ...item,
            clientId: item.clientId?.value,
            clientType: needAppendClientType ? 'CORPORATION' : item.clientType, // 自动为承租人添加类型默认为法人
            stockRiskExposure: mathjs.toNonExponentialPlus(
              mathjs.format(mathjs.multiply(item.stockRiskExposure, 10000))
            ),
          }
        })
        .filter((i) => i.clientId || i.clientName)
    }

    const params = {
      ...rest,
      evaluationSubjectId: this.evaluationSubjectIdValue,
      province,
      city,
      district,
      lesseeInfo: getClientId(lesseeInfo, true),
      guaranteeInfo: getClientId(guaranteeInfo),
      pledgorInfo: getClientId(pledgorInfo),
      mortgagorInfo: getClientId(mortgagorInfo),
      debtorInfo: getClientId(debtorInfo),
      creditorInfo: getClientId(creditorInfo, true),
      supplierInfo: supplierInfo?.map(({ clientName }) => clientName).join(','),
      id: this.projectId,
    }

    await Api.postProjectBaseInfoModify(params)
    this.page.init()
    this.getProjectDataDetail(this.projectId)
  }

  //变更日志
  changeLog = async (bizType, id) => {
    history.push(`/project/price/detail/log/${id}?bizType=${bizType}`)
  }
  baseForm = new FormStore({})
  handleOpen = async () => {
    const id = this.page.getData()?.id
    const data = await cashApi.postIrrCalculate({ id })
    return data
  }
  irr = ''

  irrChange = async (value) => {
    const irr = this.baseForm.getFieldValue('irr')
    if (this.irr === irr) return
    const projPricingId = this.page.getData()?.id
    const params = {
      projPricingId,
      irrPercent: (irr * 10000).toFixed(0),
    }
    await Api.priceIrrSave(params)
    this.irr = irr
  }

  beforeSubmit = async ({ id, onlyCheck }) => {
    await this.baseForm.validateFields().catch((e) => formScrollToField(e, this.baseForm))
    await Api.submitApproval({ id, onlyCheck })
  }
  submitApproval = async ({ id, onlyCheck, extParams = {} }) => {
    await this.baseForm.validateFields().catch((e) => formScrollToField(e, this.baseForm))

    try {
      this.approvalLoading = true
      const res = await Api.submitApproval({ id, onlyCheck, ...extParams })
      await this.page.init()
      this.approvalLoading = false
      message.success('提交成功')
    } catch (e) {
      console.log(e)
      this.approvalLoading = false
    }
  }
  notice = async (id) => {
    try {
      let data = {
        noticeSource: 'PROJ_REVIEW',
        messageType: 'REMIND',
        content: this.res.projCode,
        needQa: true,
        relation: this.res.projName,
        to: [this.res.riskControlManagerId, this.res.legalManagerUserId],
        from: JSON.parse(localStorage.getItem('userInfo')).userName,
        pcurl: `${window.location.pathname}${window.location.search}`,
        businessId: this.projectId,
      }
      await Api.messageNotice(data)
      message.info('通知成功！')
    } catch (e) {
      console.log(e)
    }
  }
  projectDataDetail
  getProjectDataDetail = async (id) => {
    if (id) {
      const res = await Api.postProjectDataDetail({
        projPricingId: id,
        businessVersion: this.businessVersion,
      })

      if (res) {
        this.projectDataDetail = res
        return res
      }
    }
  }
}
export default Store
