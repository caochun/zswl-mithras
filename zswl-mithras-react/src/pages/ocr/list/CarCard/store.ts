import { Modal, ModalStore, PageStore, TableStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import { message } from 'antd'
import vehicleCertificateApi from '@/api/ocr/vehicleCertificateApi'
import DataUpload from '@/components/DataUpload'
import { uniqueId } from 'lodash'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  vatInvoiceCount = {}
  getVatInvoiceCount = async (leaseholdId) => {
    const res = await vehicleCertificateApi.postVehicleCount({ leaseholdId })
    this.vatInvoiceCount = res
  }
  page = new PageStore({
    request: async (params) => {},
  })
  table = new TableStore({
    request: async (params) => {
      const leaseItemInfoId = this.page.getParams().id
      this.getVatInvoiceCount(leaseItemInfoId)
      const res = await vehicleCertificateApi.postVehicleList({
        ...params,
        leaseItemInfoId,
        status: this.selectedType,
      })
      return res
    },
  })
  batchModal = new ModalStore({
    onFinish: async (values) => {
      const { vehicleIds } = this.batchModal.getInitialValues() ?? {}
      const leaseholdId = this.page.getParams().id
      const params = {
        ids: vehicleIds,
        leaseholdId,
        ...values,
      }
      await vehicleCertificateApi.postVehicleUpdate(params)
      message.success('更新成功')
      this.table.search()
      this.batchModal.close()
    },
  })
  delete = async (item, operateType = 'DELETE', after = true) => {
    const { keys, rows } = this.table.getSelected()
    const lists = item ? [item] : rows
    const vehicleIds = []
    const changeRecordIds = []
    lists.forEach((item) => {
      item.id && vehicleIds.push(item.id)
      if (item?.changeRecordRspList?.length) {
        item.changeRecordRspList.forEach(({ changeRecordList, fileId }) => {
          changeRecordIds.push(fileId)
        })
      }
    })

    const leaseItemInfoId = this.page.getParams().id
    await vehicleCertificateApi.postVehicleDelete({
      vehicleIds,
      leaseItemInfoId,
      changeRecordIds,
      operateType,
    })
    message.success('删除成功')
    if (after) {
      this.table.search()
      this.getVatInvoiceCount(leaseItemInfoId)
    }
  }
  batchDownload = async () => {
    const { keys, rows } = this.table.getSelected()
    const leaseItemInfoId = this.page.getParams().id
    const vehicleIds = rows.map((v) => v.id)
    let params = { vehicleIds, leaseItemInfoId }
    if (rows.length === 0) {
      let { page, pageSize, ...rest } = this.table.getParams()
      params = { ...rest, leaseItemInfoId }
    }
    return await vehicleCertificateApi.postVehicleExportExcel(params)
  }
  selectedType = undefined
  setSelectedType = (val) => {
    this.selectedType = val
    this.table.reset()
  }
  compareModal = new ModalStore({
    onOpen: async ({ selected }) => {
      const newList = selected
        .reduce((pre, cur) => {
          const { changeRecordRspList, registrationPageNo, id, ...rest } = cur
          const commonParams = {
            registrationPageNo,
            id,
          }
          const newChangeRecordList = (changeRecordRspList || [])?.map((v) => ({
            ...v,
            ...commonParams,
            isHomePage: false,
          }))
          if (rest.fileId) pre.push({ ...rest, ...commonParams, isHomePage: true })
          pre.push(...newChangeRecordList)
          return pre
        }, [])
        .filter((v) => v.fileId)
      const ids = newList.map((item) => item.fileId)
      const res = await DataUpload.queryFileData(ids)
      const newSelected = newList.map((item) => {
        const url = res.find((i) => i.id == item.fileId)?.fileUrl
        return { ...item, url }
      })
      return newSelected
    },
    onFinish: async (values) => {},
  })
  lock = async (isLock, vehicleIds) => {
    const leaseItemInfoId = this.page.getParams().id
    const params = { vehicleIds, leaseItemInfoId }
    const func = isLock
      ? vehicleCertificateApi.postVehicleUnlock
      : vehicleCertificateApi.postVehicleLock
    await func(params)
    message.success(isLock ? '解锁成功' : '锁定成功')
    this.table.search()
  }
  batchLock = async () => {
    const leaseItemInfoId = this.page.getParams().id
    const { keys, rows } = this.table.getSelected()
    const vehicleIds = rows.map((v) => v.id)
    if (vehicleIds.length === 0) {
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
        await vehicleCertificateApi.postVehicleLock({
          vehicleIds,
          leaseItemInfoId,
        })
        message.success('操作成功')
        this.table.search()
      },
    })
  }
  fileModal = new ModalStore({})
}
export default new Store()
