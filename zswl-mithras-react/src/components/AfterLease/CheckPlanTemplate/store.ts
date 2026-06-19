import { message } from 'antd'
import { history, makeAutoObservable } from '@zswl/admin'
import { ModalStore, PageStore, Modal } from '@zswl/components'
import Api from '@/api/afterLease/rentalInspectionReport'
import planApi from '@/api/afterLease/rentalInspectionPlan'
import { TEMPLATE_LIST } from './enum'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  active = undefined
  activeTag = undefined
  id = undefined
  baseInfo = {}
  tagChange = (e) => {
    this.activeTag = e.target.value
  }
  idChange = async ({ id, businessVersion }) => {
    this.id = id
    const res = await planApi.postInfoGet({ id, businessVersion })
    this.baseInfo = res
    this.reportTypeChange(res.reportType)
  }
  reportTypeChange = (reportType) => {
    const index = TEMPLATE_LIST.findIndex((v) => v.value === reportType)
    const active = index === -1 ? 0 : index
    const selectTemp = TEMPLATE_LIST[active]
    this.active = active
    this.activeTag = selectTemp?.children[0].value
  }
  submitModal = new ModalStore({
    onOpen: () => {
      return {
        active: this.active,
      }
    },
    onFinish: async ({ active }) => {
      const selectTemp = TEMPLATE_LIST[active]
      const reportType = selectTemp.value
      const { version } = await planApi.postReporttypeChange({ reportType, id: this.id })
      this.active = active
      this.activeTag = selectTemp.children[0].value
      // this.baseInfo.reportTemplateType = version
      this.baseInfo.reportTemplateType = 'V3'
      this.submitModal.close()
    },
  })

  submit = async () => {
    await planApi.postProcessSubmit({ id: this.id })
    message.success('提交审批成功')
    history.push(`/afterLease/checkPlan`)
  }
  downloadReport = async () => {
    const res = await Api.postSingleDownload({ id: this.id })
    return res
  }
  page = new PageStore({})

  // 定时调用接口
  lastConnect = Date.now()
  connectNet = () => {
    let intervalMinutes = 5 * 60 * 1000 // 5分钟的毫秒数
    // let intervalMinutes = 1000 // 5分钟的毫秒数
    setInterval(() => {
      let currentTime = Date.now()
      let timeDiff = currentTime - this.lastConnect
      if (timeDiff >= intervalMinutes) {
        this.lastConnect = currentTime
        planApi.keepAlive()
      }
    }, intervalMinutes)
  }
}
export default Store
