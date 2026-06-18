import { makeAutoObservable } from '@zswl/admin'
import { TableStore, ModalStore } from '@zswl/components'
import { message } from 'antd'
import DataUpload from '@/components/DataUpload'
import Api from '@/api/lease/maintainApi'

class Store {
  constructor(data) {
    this.isFormApproval = data?.isFormApproval
    this.id = data?.id
    this.businessVersion = data?.businessVersion
    this.baseStore = data?.baseStore
    this.projCode = data?.projCode
    makeAutoObservable(this)
  }

  formatList = (value) => {
    const { list, ...rest } = value
    const result = []
    list?.map((item) => {
      const obj = {}
      item.dataList?.map((i) => {
        obj[i.key] = i.value
      })
      result.push({
        ...obj,
        id: item.itemId,
        matchColumns:item.matchColumns
      })
    })
    return {
      list: result,
      ...rest,
    }
  }
  // 动态标题列
  headerList = []
  setHeaderList = (value) => {
    this.headerList = value ?? []
  }
  // 租赁成本
  totalCost = ''
  setTotalCost = (value) => {
    this.totalCost = value
  }
  // 租赁物总额
  totalAmount = ''
  setTotalAmount = (value) => {
    this.totalAmount = value
  }
  uploadedOcrFile = undefined
  $table = new TableStore({
    request: async (params) => {
      const result = await Api.postLeaseItemPageList({
        id: this.id,
        ...params,
      })
      this.setHeaderList(result.headerList)
      this.setTotalCost(result.totalCost)
      this.setTotalAmount(result.leaseItemTotalAmount)
      this.uploadedOcrFile = result.uploadedOcrFile
      return this.formatList(result.pageList)
    },
  })

  onBatchRemove = async () => {
    const { keys } = this.$table.getSelected()
    await Api.postLeaseItemRemove({
      ids: keys,
    })
    message.success('删除成功')
    this.$table.search()
    const { fileListRef } = this.baseStore.page.getParams()
    fileListRef?.current?.search()
  }
  onBatchExport = async () => {
    const { keys } = this.$table.getSelected()
    await Api.postLeaseItemExport({
      id: this.id,
      itemIds: keys,
    })
    message.success('导出成功')
  }
  onFileChange = async (file) => {
    const { fileList } = DataUpload.classify(file)
    await Api.postLeaseItemImport({
      file: fileList[0],
      id: this.id,
    })
    this.$table.search()
    const { fileListRef } = this.baseStore.page.getParams()
    fileListRef?.current?.search()
  }

  saveTotalAmount = async (value) => {
    await Api.postLeaseItemTotalamountSave({
      id: this.id,
      totalAmount: value,
    })
    message.success('保存成功')
  }
  exportTemplate = async () => {
    const res = await Api.postLeaseItemTemplateDownload({ id: this.id })
    if (!res.success && res.msg) {
      message.info(res.msg)
    }
  }
  // 查重
  checkDuplicate = async () => {
    const res = await Api.postLeaseCheckDuplicate({id:this.id})
    // if (res?.matchFlag) {
    this.createModal.open(res.projList)
    // }
  }
  // 查重清单弹框
  createModal = new ModalStore()
}
export default Store
