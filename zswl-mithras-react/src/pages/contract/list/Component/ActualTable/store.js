import DataUpload from '@/components/DataUpload'
import { timeFormat } from '@/utils'
import { makeAutoObservable } from '@zswl/admin'
import { FormStore, ModalStore } from '@zswl/components'
import { message } from 'antd'
import Api from './api'
import DetailApi from '@/api/contract/contractDetail'

class Store {
  constructor({
    onReceiveDataFromChild,
    onImportSuccess,
    isFormApproval,
    bizType,
    contractId,
    businessVersion,
    showTax,
    showIRRTipsOperation,
  }) {
    this.onReceiveDataFromChild = onReceiveDataFromChild
    this.onImportSuccess = onImportSuccess
    this.isFormApproval = isFormApproval
    this.bizType = bizType
    this.contractId = contractId
    this.businessVersion = businessVersion
    this.showTax = showTax
    this.irrTipsOperation = showIRRTipsOperation
    makeAutoObservable(this)
  }

  changeList = []
  beforeDetailList = []
  actualDetail = []
  irrTipsFlag = false
  irrTipsOperation = false
  setIrrTipsFlag = (flag) => {
    this.irrTipsFlag = !!flag
  }
  setIrrTipsOperation = (operation) => {
    this.irrTipsOperation = operation
  }

  /**
   * 刷新合同加权平均IRR校验结果，用于控制页面提示展示
   */
  refreshIrrTips = async () => {
    if (!this.irrTipsOperation || !this.contractId) {
      this.setIrrTipsFlag(false)
      return
    }
    try {
      const res = await DetailApi.checkIrr({ contractId: this.contractId, operation: this.irrTipsOperation })
      this.setIrrTipsFlag(!res)
    } catch (e) {
      this.setIrrTipsFlag(false)
    }
  }
  setActualDetail = (data) => {
    this.actualDetail = data
  }
  contractStatus
  contractProcessStatus
  // 获取实际租金表
  getActualList = async (id) => {
    if (this.isFormApproval) {
      const res = await Api.getActualListCompare({
        contractId: id,
        businessVersion: this.businessVersion,
      })
      const detailList = []
      const changeList = []
      const beforeDetailList = []
      res?.map((item) => {
        const beforeDetail = {}
        const detail = {}
        const isLog = {}
        Object.entries(item).forEach(([field, { value, isChange, beforeValue, lsitMap }]) => {
          if (field === 'rentActualList') {
            detail[field] = lsitMap
            beforeDetail[field] = beforeValue
          } else {
            detail[field] = value
            beforeDetail[field] = beforeValue
          }
          if (isChange) isLog[field] = true
        })
        detailList.push(detail)
        changeList.push(isLog)
        beforeDetailList.push(beforeDetail)
      })
      this.updateData(detailList)
      this.changeList = changeList
      this.beforeDetailList = beforeDetailList
    } else {
      const res = await Api.getActualList({ contractId: id })
      this.updateData(res ?? [])
    }
  }
  // 更新数据源
  updateData = (data) => {
    const { contractProcessStatus, contractStatus } = data[0] ?? {}
    this.contractProcessStatus = contractProcessStatus
    this.contractStatus = contractStatus
    this.setActualDetail(data)
    this.onReceiveDataFromChild(data)

    // 不含税租金（元）」、「税额（元）」
    if (this.showTax) {
      this.queryActualTax()
    }
  }

  // 导入实际租金表
  scopeData = null

  $createModal = new ModalStore({
    onOpen: (val) => {
      this.scopeData = val
    },
    onFinish: async (values) => {
      const { changeDate, file } = values
      const { fileList } = DataUpload.classify(file)
      await Api.postImportRent({
        contractId: this.contractId,
        file: fileList[0],
        changeDate: changeDate ? timeFormat(changeDate) : undefined,
        receiptId: this.scopeData?.receiptId || '',
      })
      this.$createModal.close()
      message.success('导入成功')
      this.getActualList(this.contractId)
      this.onImportSuccess?.()
    },
  })

  exportLoading = false
  // 导出
  handleMenuClick = async ({ key }, item, dataSource) => {
    if (key) {
      this.exportLoading = true
      const exportApiFn = key === '1' ? Api.postExportRentData : Api.postExportCashData
      await exportApiFn({
        receiptId: item.receiptId,
        dataSource,
        businessVersion: this.businessVersion,
      }).finally(() => {
        this.exportLoading = false
      })
    }
  }
  // 删除借据
  removeReceipt = async (item) => {
    await Api.postRemoveReceipt({
      receiptId: item.receiptId,
    })
    message.info('删除成功')
    this.getActualList(this.contractId)
  }

  showDrawer = false
  setShowDrawer = (flag) => {
    this.showDrawer = !!flag
  }

  actualTax = []
  queryActualTax = async () => {
    const result = await Api.postQueryActualTax({
      contractId: this.contractId,
    })
    this.actualTax = result
  }
  getActualTaxByReceiptId = (receiptId, fieldName) => {
    const current = this.actualTax.find((item) => item.receiptId === receiptId)
    return current?.[fieldName]
  }

  // 实际IRR测算
  baseForm = new FormStore({})

  handleOpen = async (record) => {
    const data = await Api.postIRRCalclate({ id: record.receiptId })
    return data
  }

  irrOnChange = (value, index) => {
    this.irr = { [index]: value || '' }
  }
  irr = {}
  irrOnBlur = async (record, index) => {
    const irr = this.irr[index] || ''
    try {
      if (irr == '') {
        return
      }
      if (irr > 1000) {
        message.info('不能大于1000')
        return
      }
      const irrPercent = (Number(irr) * 10000).toFixed(0)
      const params = {
        receiptId: record.receiptId,
        actualIrr: irrPercent,
      }
      await Api.postUpdateActualIRR(params)
      await this.refreshIrrTips()
    } catch (err) {
      console.log({ err })
    }
  }
}
export default Store
