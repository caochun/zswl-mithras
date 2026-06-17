import { makeAutoObservable } from '@zswl/admin'
import { message } from 'antd'
import Api from './api'
import { OPERATIONAL_REVIEW, REFERENCE_MATERIALS } from './enum'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  activeTab = ''
  curTaskDefKey = ''
  loading = true
  showSyncBtn = false
  tabs = []
  currentTabData = []
  enumType = {}
  tagChange = (e) => {
    console.log(e.target.value)
    this.activeTab = e.target.value
  }
  getTabs = async (id) => {
    const result = await Api.getTab({ id })
    this.tabs = result
    this.activeTab = result?.[0]?.tabCode
    console.log('this.activeTab', this.activeTab)
  }
  getBasicMaterial = async ({ id, tabCode }) => {
    this.loading = true
    const result = await Promise.all([
      Api.getBasicMaterial({ id, tabCode, moduleCode: REFERENCE_MATERIALS }),
      Api.getBasicMaterial({ id, tabCode, moduleCode: OPERATIONAL_REVIEW }),
    ])
    this.currentTabData = result
    this.loading = false
  }
  getNonBasicMaterial = async ({ id, tabCode }) => {
    this.loading = true
    const result = await Promise.all([
      Api.getNonBasicMaterial({ id, tabCode, moduleCode: REFERENCE_MATERIALS }),
      Api.getNonBasicMaterial({ id, tabCode, moduleCode: OPERATIONAL_REVIEW }),
    ])
    this.currentTabData = result
    this.loading = false
  }
  refreshTable = async ({ id, tabCode, basic }) => {
    const result = basic
      ? await Promise.all([
          Api.getBasicMaterial({ id, tabCode, moduleCode: REFERENCE_MATERIALS }),
          Api.getBasicMaterial({ id, tabCode, moduleCode: OPERATIONAL_REVIEW }),
        ])
      : await Promise.all([
          Api.getNonBasicMaterial({ id, tabCode, moduleCode: REFERENCE_MATERIALS }),
          Api.getNonBasicMaterial({ id, tabCode, moduleCode: OPERATIONAL_REVIEW }),
        ])
    this.currentTabData = result
  }
  onBatchDownload = async (params) => {
    await Api.batchDownload(params)
    message.success('批量下载')
  }
  getOperationsDirDict = async (id) => {
    const res = await Api.getOperationsDirDict(id)
    this.enumType = res
  }
  getCurTaskDefKey = async (processInstanceId) => {
    const res = await Api.getCurTaskDefKey({ processInstanceId })
    this.curTaskDefKey = res
  }
}
export default Store
