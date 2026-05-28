import { makeAutoObservable, history } from '@zswl/admin'
import Api from '../api'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  arrivedForProcessingLoading = false
  setArrivedForProcessingLoading = (flag) => {
    this.arrivedForProcessingLoading = flag
  }

  arrivedForProcessing = []
  setArrivedForProcessing = (data) => {
    this.arrivedForProcessing = data
  }
  getArrivedForProcessing = async () => {
    this.setArrivedForProcessingLoading(true)
    const res = await Api.postDashboardOperationToDoStatistics({ type: 'ARRIVE' })
    this.setArrivedForProcessing(res)
    this.setArrivedForProcessingLoading(false)
  }

  arrivedForProcessingLoading = false
  setArrivedForProcessingLoading = (flag) => {
    this.arrivedForProcessingLoading = flag
  }

  willArrivedProcessingLoading = false
  setWillArrivedProcessingLoading = (flag) => {
    this.willArrivedProcessingLoading = flag
  }
  willArrivedProcessing = []
  setWillArrivedProcessing = (data) => {
    this.willArrivedProcessing = data
  }
  getWillArrivedForProcessing = async () => {
    this.setWillArrivedProcessingLoading(true)
    const res = await Api.postDashboardOperationToDoStatistics({ type: 'WILL_ARRIVE' })
    this.setWillArrivedProcessing(res)
    this.setWillArrivedProcessingLoading(false)
  }

  goProcess = (data) => {
    const search = { processInstanceIdList: data.processInstanceIdList }
    window.open(`/process/query?search=${JSON.stringify(search)}`)
    // history.push(`/process/query?search=${JSON.stringify(search)}`)
  }
}
export default Store
