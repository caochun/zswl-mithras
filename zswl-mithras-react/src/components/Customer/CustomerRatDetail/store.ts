import { App, FormStore, Modal, PageStore, TableStore } from '@zswl/components'
import { history, makeAutoObservable } from '@zswl/admin'
import customerRatApi from '@/api/customer/customerRat/customerRatApi'
import { message } from 'antd'
import moment from 'moment'
import { scrollToAnchor } from '@/utils/document'
import ProcessApi from '@/api/process/flowExecution'
export { getApprovalText, indexCheck } from '@/utils/customerRat'

const formatListJson = (data = {}) => {
  const { values, list } = data
  return (list ?? []).map(({ dataType, fieldValue, fieldName, date, ...rest }) => {
    let newValue = fieldValue
    if (dataType === 'time') newValue = newValue && moment(newValue).format('YYYY-MM-DD HH:mm:ss')
    if (dataType === 'date') newValue = newValue && moment(newValue).format('YYYY-MM-DD')
    return {
      fieldValue: newValue,
      fieldName,
      date: date && moment(date).format('YYYY-MM-DD HH:mm:ss'),
    }
  })
}
class Store {
  constructor({model}) {
    makeAutoObservable(this)
    if(model){
      this.model = model
    }
  }
  page = new PageStore({
    request: async (params) => {
      const baseInfoDetail = await customerRatApi.postClientDetail({ id: params?.id })
      this.model = baseInfoDetail.modelCode
      const customerDetail = await customerRatApi.postClientInfo({
        clientId: baseInfoDetail.clientId,
        modelCode:this.model
      })
      let paramInfo = {}
      let initialValues = {}
      paramInfo = await customerRatApi.postClientParamInfo({ id: params?.id })
      this.executeData = {
        executeCount: paramInfo.executeCount,
        executeCountLimit: paramInfo.executeCountLimit,
        score: baseInfoDetail.score,
        firstScore: baseInfoDetail.firstScore,
        adjustScore: baseInfoDetail.adjustScore,
      }
      // 暂未构造三层级结构
      paramInfo?.ratingParam?.forEach((item) => {
        initialValues[item.fieldName] = item.fieldValue
      })

      return { customerDetail, paramInfo, baseInfoDetail, initialValues }
    },
  })
  hasApprovalOption = false
  setHasApprovalOption = (val) => {
    this.hasApprovalOption = val
  }
  quantitativeTable = new TableStore({
    request: (params) => {
      return []
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
    const tableData = await this.quantitativeTable?.submit().catch((e) => {
      message.error('存在未完成定量指标')
    })
    const newForm = Object.entries(formData).reduce((pre, [key, value]) => {
      if(key.endsWith('-extra') || !value){
        return pre
      }
      const [a, b] = value.split('-')
      if(formData[key+'-'+a+'-extra']){
        return pre.concat({
          fieldName: key,
          fieldValue: a,
          extra:formData[key+'-'+a+'-extra']
        })
      }else if(!key.endsWith('-extra')){
        return pre.concat({
          fieldName: key,
          fieldValue: a,
          extra:b ? parseInt(b) : undefined
        })
      }
      return pre
    }, [])
    const param = [...newForm, ...formatListJson(tableData)]
    return param
  }
  calc = async () => {
    const id = this.page.getParams().id
    const pageData = this.page.getData()
    const param = await this.getParam()
    const params = {
      id,
      code: pageData.baseInfoDetail.modelCode,
      param,
    }
    const res = await customerRatApi.postClientExecute(params)
    message.success('试算成功')
    this.executeData = res
  }
  reportData = {}
  getReport = async () => {
    const id = this.page.getParams().id
    const res = await customerRatApi.postClientReport({ id })
    let formData = {}
    const { adjustEventList } = res
    if (adjustEventList?.[0]) {
      const { fieldName, approvalOpinion, approvalStatus } = adjustEventList[0]
      formData = {
        [`${fieldName}_approvalOpinion`]: approvalOpinion,
        [`${fieldName}_approvalStatus`]: approvalStatus,
      }
      this.form.setFieldsValue(formData)
    }

    this.reportData = { ...res, formData }
  }
  renderCount = 0
  submitApproval = async (params = {}) => {
    const { isFormApproval, taskId } = this.page.getParams()
    if (taskId) {
      await ProcessApi.passProcess({
        message: params.adjustOpinion,
        buttonKey: 'SUBMIT',
        taskId,
      })
    } else {
      const res = await customerRatApi.postClientEffect({
        id: this.page.getParams().id,
        message: params.adjustOpinion,
        ...params,
      })
    }
    message.success('提交成功')
    history.push(`/customer/customerRat?refresh=true`)
  }
  save = async (operationType: boolean = false) => {
    const executeCount = this.executeData?.executeCount
    if (executeCount === 0) {
      Modal.info({
        title: '提示',
        content: '请先试算，并查看评级结果',
      })
      return Promise.reject()
    }
    const param = await this.getParam(operationType)
    await customerRatApi.postClientFinish({
      id: this.page.getParams().id,
      operationType,
      param,
    })
    message.success('保存成功')
  }

  saveApprovalInfo = async (record, val, dataIndex) => {
    const id = this.page.getParams().id
    await customerRatApi.postClientIndexApproval({
      id,
      ratingApprovalRSP: {
        [dataIndex]: val,
        fieldName: record.fieldName,
      },
    })
  }
  afterSubmit = async (params) => {
    await this.submitApproval(params)
  }
}
export default Store
