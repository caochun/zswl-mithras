import publicInfoApi from '@/api/cpm/payment/publicInfoApi'
import { makeAutoObservable } from '@zswl/admin'
import { Modal, ModalStore, TableStore } from '@zswl/components'
import { message } from 'antd'

class Store {
  constructor({ paymentId }) {
    this.paymentId = paymentId
    makeAutoObservable(this)
  }
  paymentId = undefined
  getParams = () => {
    const { clientType, clientId, queryIntervalList, originClientType } =
      this.clientList.find((item) => item.clientId === this.clientId) ?? {}
    const { queryFrom, queryTo, id } =
      queryIntervalList?.find((item) => item.id === this.currentDate) ?? {}
    return {
      clientType,
      paymentId: this.paymentId,
      clientId: this.clientId,
      queryFrom,
      queryTo,
      id: this.currentDate,
      originClientType,
    }
  }
  createModal = new ModalStore({
    onFinish: async (values) => {
      if (this.editable) {
        message.error('请先保存当前编辑内容')
        return
      }
      const { clientId, clientType, paymentId } = this.getParams()
      const currentDate = await publicInfoApi.postCreateIntervalTable({
        ...values,
        clientId,
        paymentId,
        clientType,
      })
      this.currentDate = currentDate
      await this.getClientList()
      this.createModal.close()
      message.success('创建成功')
    },
  })
  publicModal = new ModalStore()
  currentInfo = {}
  table = new TableStore({
    pagination: false,
    request: async (params) => {
      const { clientId, paymentId, id } = this.getParams()
      if (!id) {
        this.currentInfo = {}
        return []
      }
      const { rowList, ...rest } = await publicInfoApi.postQueryIntervalTable({
        paymentId,
        clientId,
        id,
        ...params,
      })
      this.currentInfo = rest
      return rowList
    },
  })
  dateList = []
  clientList = []
  getClientList = async () => {
    if (!this.paymentId) return
    const res = await publicInfoApi.postClientList({ paymentId: this.paymentId })
    this.clientList = res
    const currentClient = this.clientId ?? res[0].clientId
    this.clientChange(currentClient)
  }
  clientId = undefined
  clientChange = (val, needMsg = false) => {
    if (this.editable && needMsg) {
      message.error('请先保存当前编辑内容')
      return
    }
    const isSome = val === this.clientId
    this.clientId = val

    const { queryIntervalList, clientType } = this.clientList.find((item) => item.clientId === val)
    this.dateList = queryIntervalList
    const { id } = this.dateList?.[0] ?? {}
    const currentId = isSome ? this.currentDate ?? id : id
    this.currentDateChange(currentId)
  }
  currentDate = undefined
  currentDateChange = async (val, needMsg = false) => {
    if (this.editable && needMsg) {
      message.error('请先保存当前编辑内容')
      return
    }

    this.currentDate = val
    setTimeout(() => {
      this.table.search()
    }, 0)
  }
  searchPublicInfo = () => {
    this.publicModal.open()
    this.getClientList()
  }
  createPublicInfo = () => {
    this.createModal.open()
  }
  download = async () => {
    await publicInfoApi.postInfoExport({ paymentId: this.paymentId })
    message.success('下载成功')
  }
  remove = async () => {
    Modal.confirm({
      title: '删除',
      content: '删除后将无法恢复！是否确定继续删除？',
      onOk: async () => {
        const { paymentId, id } = this.getParams()
        const clientId = await publicInfoApi.postDeleteIntervalTable({ paymentId, id })
        this.currentDate = clientId
        await this.getClientList()
        this.setEditable(false)
        message.success('删除成功')
      },
    })
  }
  editable = false
  setEditable = (edit) => {
    this.editable = edit
  }
  cancel = () => {
    this.setEditable(false)
  }
  save = async () => {
    const { list, values } = await this.table.submit()
    const rowList = list.map((item, index) => ({ ...item }))
    const { id, clientId, paymentId } = this.getParams()
    await publicInfoApi.postTableContent({
      paymentId,
      clientId,
      id,
      rowList,
    })
    await this.getClientList()
    // await this.table.search()
    message.success('保存成功')
    this.setEditable(false)
  }
  getNewData = async () => {
    const { id, clientId, paymentId } = this.getParams()
    const res = await publicInfoApi.postQueryOuterPublic({ publicInfoQueryId: id })
    if (res) {
      await this.table.search()
      message.success('重新取数成功')
    } else {
      message.error('重新取数失败')
    }
  }
}

export default Store
