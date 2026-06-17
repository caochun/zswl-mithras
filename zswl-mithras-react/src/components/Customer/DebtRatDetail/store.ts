import { FormStore, PageStore, TableStore } from '@zswl/components'
import { history, makeAutoObservable } from '@zswl/admin'
import { message } from 'antd'
import debtRatApi from '@/api/customer/customerRat/debtRatApi'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  page = new PageStore({
    request: async (params) => {
      const { id, isFormApproval } = params
      const baseInfoDetail = await debtRatApi.postAmountDetail({ id })
      const customerDetail = await debtRatApi.postAmountProjInfo({
        id,
      })
      let paramInfo = {}
      let initialValues = {
        isRealEstateAdjust: baseInfoDetail?.isRealEstateAdjust,
        isStockRightsAdjust: baseInfoDetail?.isStockRightsAdjust,
      }
      paramInfo = await debtRatApi.postAmountParamInfo({ id })
      this.executeData = {
        executeCount: paramInfo.executeCount,
        executeCountLimit: paramInfo.executeCountLimit,
        projQuota: baseInfoDetail.projQuota,
      }
      paramInfo?.ratingParam?.forEach((item) => {
        initialValues[item.fieldName] = item.fieldValue
      })

      return { customerDetail, paramInfo, baseInfoDetail, initialValues }
    },
  })

  form = new FormStore({})
  executeData = {
    executeCount: 0,
    executeCountLimit: 3,
  }
  getParam = async (needSubmit = true) => {
    const formData = needSubmit
      ? await this.form.submit().catch((e) => {
          message.error('请填写完整问卷')
        })
      : this.form.getFieldsValue()
    const { isRealEstateAdjust, isStockRightsAdjust, ...rest } = formData
    const param = Object.entries(rest).map(([key, value]) => ({
      fieldName: key,
      fieldValue: value,
    }))
    return { param, formData }
  }
  calc = async () => {
    const id = this.page.getParams().id
    const pageData = this.page.getData()
    const { param } = await this.getParam()
    const params = {
      id,
      code: pageData.baseInfoDetail.modelCode,
      param,
    }
    const res = await debtRatApi.postAmountExecute(params)
    message.success('试算成功')
    this.executeData = res
  }
  reportData = {}
  getReport = async () => {
    const id = this.page.getParams().id
    const res = await debtRatApi.postAmountReport({ id })
    const initialValues = {}

    Object.values(res.evaluateBaseList).forEach((item) => {
      item.forEach((v) => {
        initialValues[v.fieldName] = v.value
      })
    })
    Object.values(res.creditMeasureListMap).forEach((item) => {
      item.forEach((v) => {
        initialValues[v.fieldName] = v.value
      })
    })

    this.reportData = res
    setTimeout(() => {
      this.form.setFieldsValue(initialValues)
    }, 300)
  }
  submitApproval = async () => {
    const { customerDetail } = this.page.getData()
    const id = this.page.getParams().id
    const res = await debtRatApi.postAmountEffect({ id })
    message.success('提交成功')
    const { projReviewId, bizType = 'ZL' } = customerDetail
    history.push(`/project/review/detail/${projReviewId}?typeId=review&bizType=${bizType}`)
  }
  save = async (operationType: boolean = false) => {
    const { param, formData } = await this.getParam()
    const { isRealEstateAdjust, isStockRightsAdjust, ...rest } = formData
    await debtRatApi.postAmountFinish({
      id: this.page.getParams().id,
      operationType,
      param,
      isRealEstateAdjust,
      isStockRightsAdjust,
    })
    message.success('保存成功')
  }
}
export default Store
