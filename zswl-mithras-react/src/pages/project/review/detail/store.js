import { makeAutoObservable, history, getQuery } from '@zswl/admin'
import { FormStore, PageStore, Modal, DrawerStore, ModalStore } from '@zswl/components'
import { message } from 'antd'
import Api from './api'
import { compareDetail, formScrollToField, hasValue } from '@/utils'
import cashApi from '@/api/project/projectCashflow'
import processModifyRemarkApi from '@/api/approval/processModifyRemarkApi'
import mathjs from '@/utils/math'
import FinancialReportStatisticsApi from '@/pages/project/review/Components/FinancialReportStatistics/api'
import customerRatApi from '@/api/customer/customerRat/customerRatApi'

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
  tempDtempDetail = {}
  init = (newProject, projectId) => {
    // this.baseInfoShowValue = !newProject
    this.QSShowValue = !newProject
    this.QSZLShowValue = !newProject
    this.projectId = projectId
  }
  page = new PageStore({
    request: async (params) => {
      let remarkArr
      this.newDetail = {}
      if (params?.approvalParams && params?.reconsiderParams) {
        remarkArr = await Promise.all([
          processModifyRemarkApi.postRemarkAll(
            params.approvalParams,
            'processmodifyremarkallprojreview'
          ),
          processModifyRemarkApi.postRemarkAll(
            params.reconsiderParams,
            'processmodifyremarkallprojreview'
          ),
        ])
          .then((res) => res)
          .catch((e) => {
            console.log(e)
            return [{}, {}]
          })
      }
      const [approvalDetail, reconsiderDetail] = remarkArr ?? []
      try {
        const { isFormApproval, modelKey, processInstanceId, businessVersion, id } = params
        if (isFormApproval) {
          const api =
            modelKey === 'ProjReviewCreateFlow'
              ? Api.postProjectPricingBaseInfoDetailCompare
              : Api.postProjectBaseInfoDetailCompare
          const res = await api({
            id,
            processInstanceId,
            businessVersion,
          })
          this.bizType = res.bizType?.value
          this.isProjSponsor = res.isProjSponsor?.value
          const { newDetail, isLog } = compareDetail(res)
          this.tempDtempDetail = {
            ...newDetail,
            isProjSponsor: res.isProjSponsor?.value,
            approvalDetail,
            reconsiderDetail,
            isLog,
          }
          return this.tempDtempDetail
        } else {
          this.res = await Api.postProjectBaseInfoDetail({
            id,
            processInstanceId,
          })
          this.bizType = this.res.bizType
          this.isProjSponsor = this.res.isProjSponsor
          this.tempDtempDetail = {
            ...this.res, approvalDetail, reconsiderDetail
          }
          return this.tempDtempDetail
        }
      } catch (e) {
        console.log(e)
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
          if (!needAppendClientType && !item.clientType && hasValue(item.clientId)) {
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
    history.push(`/project/review/detail/log/${id}?bizType=${bizType}`)
  }
  goRat = async (id) => {
    const { clientName } = this.page.getData()
    const search = JSON.stringify({
      clientName,
    })

    window.open(`/customer/customerRat?search=${search}`)
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
    this.irr = irr
    const projReviewId = this.page.getData()?.id
    const params = {
      projReviewId,
      irrPercent: (irr * 10000).toFixed(0),
    }
    await Api.priceIrrSave(params)
  }

  ratTipsModal = new ModalStore({})
  validateRat = async (id) => {
    const { ratingClientIsDone, ratingAmountIsDone, undoRatingClientList } = await Api.ratingCheck({
      id,
    })

    if (!ratingClientIsDone || !ratingAmountIsDone) {
      this.ratTipsModal.open({
        ratingClientIsDone,
        ratingAmountIsDone,
        undoRatingClientList,
      })
      return Promise.reject()
    }
    return Promise.resolve()
  }

  validateFile = async (id) => {
    const { isLack, detailList } = await Api.sourceSceneValidate({ id })

    if (isLack) {
      Modal.confirm({
        title: '提示',
        content: (
          <div>
            <div>
              交易结构中以下客户缺少必要的资料清单文件，请在资料清单模块维护完成后
              再发起评审相关流程！
            </div>
            {detailList.map((item) => (
              <div style={{ marginTop: 12 }}>
                <div>客户名称：{item.clientName}</div>
                <div>
                  缺少的必要文件：
                  <span style={{ color: 'red' }}>{item.lackMaterialsList.join('、')}</span>
                </div>
              </div>
            ))}
          </div>
        ),
        footer: null,
      })
      return Promise.reject()
    }
    return Promise.resolve()
  }
  beforeSubmit = async ({ id, onlyCheck }) => {
    await this.validateFile(id)
    await this.baseForm.validateFields().catch((e) => formScrollToField(e, this.baseForm))
    await Api.submitApproval({ id, onlyCheck })
  }
  submitApproval = async ({ id, onlyCheck, extParams, isInitCheckFinancial }) => {
    await this.validateFile(id)
    await this.validateRat(id)
    const detail = this.page.getData()

    if (isInitCheckFinancial) {
      const result = await FinancialReportStatisticsApi.checkResult({
        id,
      })
      if (result.checkResult === 0) {
        Modal.info({
          title: '提示',
          content: `承租人/担保人财报信息不完整，请点击【财务报表情况】填写财报不完整原因或在客户管理模块维护所需财报后再提交！`,
          okText: '财务报表情况',
          onOk: async () => {
            this.page.getParams().financialRef.current?.modal.open({
              canSubmit: true,
              ...extParams,
            })
          },
        })
        return
      }
    }
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
  debtDrawer = new DrawerStore()

  newDetail = {}
  updateInfo = async () => {
    const id = this.page.getParams().id
    const detail = this.page.getData()
    const { ratingClientId, ratingAmountId } = detail
    const res = await customerRatApi.postReviewUpdate({ id, ratingClientId, ratingAmountId })
    message.success('更新成功')
    this.newDetail = res
  }
  projectDataDetail
  getProjectDataDetail = async (id) => {
    if (id) {
      const res = await Api.postProjectDataDetail({
        projReviewId: id,
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
