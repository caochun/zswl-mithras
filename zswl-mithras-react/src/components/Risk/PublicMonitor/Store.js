import { TableStore, ModalStore } from '@zswl/components'
import { makeAutoObservable, history } from '@zswl/admin'
import { message } from 'antd'
import Api from './api'

class Store {
  constructor(chiName) {
    this.chiName = chiName
    makeAutoObservable(this)
  }
  chiName = ''

  curRecord = {}
  setCurRecord = (data) => {
    this.curRecord = data
  }

  changeInfoDetail = {}
  setChangeInfoDetail = (data) => {
    this.changeInfoDetail = data
  }

  courtAnnounceDetail = {}
  setCourtAnnounceDetail = (data) => {
    this.courtAnnounceDetail = data
  }

  courtSessionDetail = {}
  setCourtSessionDetail = (data) => {
    this.courtSessionDetail = data
  }

  caseInfoDetail = {}
  setCaseInfoDetail = (data) => {
    this.caseInfoDetail = data
  }

  $table = new TableStore({
    request: async (params) => {
      const newParams = { ...params }
      if (this.chiName) {
        newParams.chiName = this.chiName
      }
      const data = await Api.postMonitorList(newParams)
      return data
    },
  })

  $handleModal = new ModalStore({
    onOpen: (record) => {
      this.setCurRecord(record)
      return { ...record }
    },
    onFinish: async (values) => {
      await Api.postMonitorHandle({
        id: values.id,
        advisement: values.advisement,
      })
      message.success('操作成功')
      this.$handleModal.close()
      this.$table.search()
    },
  })

  $closeModal = new ModalStore({
    onOpen: (record) => {
      return record
    },
    onFinish: async (values) => {
      await Api.postMonitorClose(values)
      message.success('操作成功')
      this.$closeModal.close()
      this.$table.search()
    },
  })
  changeInfoModal = new ModalStore({})
  courtAnnounceModal = new ModalStore({})
  courtSessionModal = new ModalStore({})
  caseInfoModal = new ModalStore({})

  handleOpenModal = async ({ newTypeOpinion, id }) => {
    const params = { id }
    if (newTypeOpinion === 'CHANGE_INFO') {
      const res = await Api.getChangeInfo(params)
      this.setChangeInfoDetail(res ?? {})
      this.changeInfoModal.open()
    } else if (newTypeOpinion === 'COURT_ANNOUNCE') {
      const res = await Api.getCourtAnnounce(params)
      this.setCourtAnnounceDetail(res ?? {})
      this.courtAnnounceModal.open()
    } else if (newTypeOpinion === 'COURT_SESSION') {
      const res = await Api.getCourtSession(params)
      this.setCourtSessionDetail(res ?? {})
      this.courtSessionModal.open()
    } else if (newTypeOpinion === 'CASE_INFO') {
      const res = await Api.getCaseInfo(params)
      this.setCaseInfoDetail(res ?? {})
      this.caseInfoModal.open()
    }
  }

  seeFlowDetail = async ({ id }) => {
    const processInstanceId = await Api.getProcessInstanceId({ id })
    history.push(
      `/process/query/detail/${processInstanceId}?tab=apply&diff=processInstanceId&nav=myquery`
    )
  }

  handleJump = (record) => {
    const { linkAddress, dataSource, newsUrl } = record
    if(dataSource && dataSource === 'XINSIGHT' && newsUrl) {
      window.open(newsUrl)
    }
    if (linkAddress) window.open(linkAddress)
  }

  clientList = []
  onSearch = async(val) => {
    const res = await Api.getClientList({clientName:val})
    this.clientList = res.list.map((item) => ({
      label: item.clientName,
      value: item.id,
    }))
  }
  addValues
  // 新增舆情
  $addModal = new ModalStore({
    onFinish: async () => {
      if(this.addValues){
        const res = await Api.postMonitorConfirm({clientId:this.addValues.id})
        if(res){
          const {clientName,id,creditCode} = res
          history.push(`/risk/publicMonitor/detail/${id}?clientName=${clientName}&creditCode=${creditCode}`)
          this.$addModal.close()
          this.$table.search()
        }
      }
    },
  })
  postMonitorDelete = async (params) => {
    await Api.postMonitorDelete(params)
    message.success('删除成功')
    this.$table.search()
  }
}
export default Store
