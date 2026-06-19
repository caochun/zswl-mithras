import { ModalStore, PageStore, TableStore, Modal } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import ocrInvoiceApi from '@/api/ocr/ocrInvoiceApi'
import { message } from 'antd'
import DataUpload from '@/components/DataUpload'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  vatInvoiceCount = {}
  getVatInvoiceCount = async (leaseholdId) => {
    const res = await ocrInvoiceApi.postVatInvoiceCount({ leaseholdId: +leaseholdId })
    this.vatInvoiceCount = res
  }
  page = new PageStore({
    request: async (params) => {
      await this.amountCheck()
    },
  })
  defaultExpandedRowKeys = []
  onExpand = (expanded, record) => {
    this.defaultExpandedRowKeys = !expanded
      ? this.defaultExpandedRowKeys.filter((v) => v !== record.id)
      : [...this.defaultExpandedRowKeys, record.id]
  }
  table = new TableStore({
    request: async (params) => {
      const leaseholdId = this.page.getParams().id
      this.getVatInvoiceCount(leaseholdId)
      const { list, ...rest } = await ocrInvoiceApi.postVatInvoiceList({
        ...params,
        leaseholdId,
        verifyResult: this.selectedType,
      })
      this.defaultExpandedRowKeys = list.map((v) => v.id)
      return {
        list: list.map((v) => {
          const { invoiceProductList, ...rest } = v
          if (invoiceProductList?.length === 1) {
            const { invoiceId, ...restProductList } = invoiceProductList[0]
            return { ...restProductList, ...rest }
          }
          return v
        }),
        ...rest,
      }
    },
  })
  batchModal = new ModalStore({
    onFinish: async (values) => {
      const { vatInvoiceIds } = this.batchModal.getInitialValues() ?? {}
      const leaseItemInfoId = this.page.getParams().id
      await ocrInvoiceApi.postVatInvoiceUpdate({ ...values, vatInvoiceIds, leaseItemInfoId })
      message.success('更新成功')
      this.table.search()
      this.batchModal.close()
    },
  })
  delete = async (item) => {
    const { keys } = this.table.getSelected()
    const vatInvoiceIds = item?.id ? [item.id] : keys

    await ocrInvoiceApi.postVatInvoiceDelete({ vatInvoiceIds })
    message.success('删除成功')
    this.table.search()
  }
  batchLock = async () => {
    const { keys, rows } = this.table.getSelected()
    if (keys.length === 0) {
      message.info('请先选中')
      return
    }
    if (rows.some((item) => item.locked)) {
      message.info('存在已锁定的记录，请重新选择')
      return
    }
    Modal.confirm({
      title: `确认锁定？`,
      onOk: async () => {
        await ocrInvoiceApi.postVatInvoiceLocked({
          vatInvoiceIds: keys,
          isLocked: true,
        })
        message.success('操作成功')
        this.table.search()
      },
    })
  }

  batchDownload = async () => {
    const { keys } = this.table.getSelected()
    let params = { vatInvoiceIds: keys }
    if (keys.length === 0) {
      let { page, pageSize, ...rest } = this.table.getParams()
      params = rest
    }
    return await ocrInvoiceApi.postVatInvoiceExportExcel({
      ...params,
      leaseholdId: this.page.getParams().id,
    })
  }
  selectedType = undefined
  setSelectedType = (val) => {
    this.selectedType = val
    this.table.reset()
    // this.table.search({ page: 1 })
  }
  compareModal = new ModalStore({
    onOpen: async ({ selected }) => {
      const ids = selected.map((item) => item.fileId)
      const res = await DataUpload.queryFileData(ids)
      const newSelected = selected.map((item) => {
        const url = res.find((i) => i.id == item.fileId)?.fileUrl
        return { ...item, url }
      })
      return newSelected
    },
    onFinish: async (values) => {},
  })
  lock = async (item) => {
    const leaseholdId = this.page.getParams().id

    Modal.confirm({
      title: `确认解锁？`,
      onOk: async () => {
        await ocrInvoiceApi.postVatInvoiceLocked({
          isLocked: !item.locked,
          vatInvoiceIds: [item.id],
          leaseholdId,
        })
        message.success('操作成功')
        this.table.search()
      },
    })
  }
  amountSymbol = undefined
  amountCheck = async () => {
    const leaseholdId = +this.page.getParams().id
    this.amountSymbol = await ocrInvoiceApi.postVatInvoiceAmountCheckout({ leaseholdId })
  }
  fileModal = new ModalStore({})
}
export default new Store()
