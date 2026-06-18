import { makeAutoObservable } from '@zswl/admin'
import { ModalStore, PageStore } from '@zswl/components'
import Api from '@/api/afterLease/rentalInspectionReport'
import { TEMPLATE_LIST } from '../enum'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  baseData
  contentData
  summaryData
  saveBase = async () => {
    const data = await Api.postBaseSave({})
  }
  saveContent = async () => {
    const data = await Api.postContentSave({})
  }
  saveSummary = async () => {
    const data = await Api.postSummarySave({})
  }
  getBase = async (id) => {
    const data = await Api.postBaseGet({ id })
    this.baseData = data
  }
  getContent = async (id) => {
    const data = await Api.postContentGet({ id })
    this.contentData = data
  }
  getSummary = async (id) => {
    const data = await Api.postSummaryGet({ id })
    this.summaryData = data
  }
  getData = (id) => {
    this.getBase(id)
    this.getContent(id)
    this.getSummary(id)
  }
}
export default Store
