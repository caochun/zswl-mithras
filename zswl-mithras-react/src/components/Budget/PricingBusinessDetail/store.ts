import { DrawerStore, FormStore, PageStore, TableStore } from '@zswl/components'
import { makeAutoObservable, history } from '@zswl/admin'
import { message } from 'antd'
import newFtpBaseInfoApi from '@/api/newFtp/newFtpBaseInfoApi'
class Store {
  constructor() {
    makeAutoObservable(this)
  }
  form = new FormStore({})
  calculateDeductionFlag = 0
  calculateGuidanceFlag = 0
  descData = []
  getDesc = async ({ isFormApproval = false, businessVersion, id }) => {
    const formData = {}
    const descApi = isFormApproval
      ? newFtpBaseInfoApi.postCompareDescription
      : newFtpBaseInfoApi.postInfoDesclist
    const res = await descApi({ mainId: id, version: businessVersion })
    res.forEach((v) => {
      const type = v.descType?.value ?? v.descType
      formData[type] = v.descContent
    })
    setTimeout(() => {
      this.form.setFieldsValue(formData)
    }, 100)
    this.descData = res
    return res
  }
  page = new PageStore({
    request: async ({ id, isFormApproval, businessVersion }) => {
      const detail = await newFtpBaseInfoApi.postInfoDetail({ mainId: id })
      this.getDesc({ isFormApproval, businessVersion, id })
      this.calculateDeductionFlag = detail.calculateDeductionFlag
      this.calculateGuidanceFlag = detail.calculateGuidanceFlag
      return { detail }
    },
  })

  approvalLoading = false
  //变更日志
  changeLog = async (id, month) => {
    history.push(`/budget/pricing/business/detail/log/${id}?month=${month}`)
  }
  onCancel = async () => {
    history.push(`/budget/pricing/business`)
  }
  submitDisabled = false
  submitApproval = async () => {
    const id = this.page.getParams()?.id
    return await newFtpBaseInfoApi.postInfoSubmit({ mainId: id }).then((res) => {
      message.success('提交审批成功')
      this.page.init()
    })
  }

  beforeUpload = async () => {
    this.page.init()
  }
  submitDisabled = false
  setSubmitDisabled = (val) => {
    this.submitDisabled = val
  }
  beforeChangeDrawer = new DrawerStore({})
}
export default Store
