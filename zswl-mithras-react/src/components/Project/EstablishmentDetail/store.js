import { makeAutoObservable, history } from '@zswl/admin'
import { ModalStore, PageStore } from '@zswl/components'
import { message } from 'antd'
import Api from './api'
import { compareDetail } from '@/utils'
import QSStore from '@/components/Project/EstablishmentDetail/QuotationScheme/store'
import customerRatApi from '@/api/customer/customerRat/customerRatApi'
class Store {
  constructor() {
    makeAutoObservable(this)
  }
  bizType
  projectId
  baseInfoShowValue = true //  基本信息编辑态
  QSShowValue = true // 报价方案编辑态
  QSZLShowValue = true // 报价方案-新增租赁编辑态

  approvalLoading = false
  setBaseInfoShowValue = (val) => {
    this.baseInfoShowValue = val
  }
  setQSShowValue = (val) => {
    this.QSShowValue = val
  }
  setQSZLShowValue = (val) => {
    this.QSZLShowValue = val
  }
  isProjSponsor = false //判断是不是主办
  page = new PageStore({
    request: async (params) => {
      const { id, businessVersion, isFormApproval } = params
      this.projectId = id
      this.businessVersion = businessVersion
      this.isFormApproval = isFormApproval
      this.newDetail = {}
      if (isFormApproval) {
        const res = await Api.postProjectBaseInfoDetailCompare({
          id: params.id,
          processInstanceId: params.processInstanceId,
          businessVersion: params.businessVersion,
        })
        this.bizType = res.bizType?.value
        this.isProjSponsor = res.isProjSponsor?.value
        return { ...compareDetail(res).newDetail, isProjSponsor: res.isProjSponsor?.value }
      } else {
        const res = await Api.postProjectBaseInfoDetail({
          id: params.id,
          processInstanceId: params.processInstanceId,
        })
        if (res) {
          this.isProjSponsor = res.isProjSponsor
          this.bizType = res.bizType
          return res
        }
      }
      return {}
    },
  })

  postProjectBaseInfoModify = async (params, callback) => {
    const { code, msg } = await Api.postProjectBaseInfoModify(params)
    if (code === 200) {
      message.success('保存成功')

      callback && callback()
    } else {
      message.info(msg)
    }
  }
  //变更日志
  changeLog = async (bizType, id) => {
    history.push(`/project/establishment/detail/log/${id}?bizType=${bizType}`)
  }
  ratTipsModal = new ModalStore({})
  // 提交审批
  validateBeforeSubmit = async (projEstablishId) => {
    if (!this.baseInfoShowValue) {
      message.info('基本信息未保存，请先保存后提交审批！')
      return
    }
    if (!this.QSShowValue) {
      message.info('报价方案未保存，请先保存后提交审批！')
      return
    }
    if (this.bizType === 'BL' && QSStore.newLeaseCredit && !this.QSZLShowValue) {
      message.info('报价方案-租赁方案未保存，请先保存后提交审批！')
      return
    }
    const detail = this.page.getData()
    const isPublic = ['PUBLIC_UTILITIES', 'CIVIL_CONSUMPTION'].includes(detail.riskControlIndustryClassify)
    const { ratingClientId, ratingMainClientId, ratingMainFinalScore } = detail
    const needTips = !ratingClientId || !ratingMainFinalScore || !ratingMainClientId
    if (isPublic && needTips) {
      this.ratTipsModal.open()
      return
    }
    this.submitApproval(projEstablishId)
  }
  tipsModal = new ModalStore({})
  submitApproval = async (id) => {
    try {
      this.tipsModal.close()
      this.approvalLoading = true
      const { code, msg } = await Api.submitApproval({ id })
      this.approvalLoading = false
      if (code === 200) {
        message.success('提交成功')
      } else {
        message.info(msg)
      }
    } catch (e) {
      this.approvalLoading = false
    }
  }
  goRat = async (id) => {
    const { clientName } = this.page.getData()
    const search = JSON.stringify({
      clientName,
    })

    window.open(`/customer/customerRat?search=${search}`)
  }
  newDetail = {}
  updateInfo = async () => {
    const id = this.page.getParams().id
    const detail = this.page.getData()
    const { ratingClientId } = detail
    const res = await customerRatApi.postEstablishUpdate({ id, ratingClientId })
    message.success('更新成功')
    this.newDetail = res
  }
  projectDataDetail
  getProjectDataDetail = async (id) => {
    if (id) {
      const res = await Api.postProjectDataDetail({
        projId: id,
        businessVersion: this.rootStore?.page.getParams().businessVersion,
      })
      if (res) {
        this.projectDataDetail = res
        return res
      }
    }
  }
}
export default Store
