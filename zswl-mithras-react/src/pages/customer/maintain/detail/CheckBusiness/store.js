import { makeAutoObservable } from '@zswl/admin'
import { Modal, ModalStore, TableStore } from '@zswl/components'
import Api from './api'
import { message } from 'antd'

class Store {
  constructor({ clientId, flowId, modelKey, taskActivityId, taskStatus }) {
    this.clientId = clientId
    this.flowId = flowId
    this.modelKey = modelKey
    this.taskActivityId = taskActivityId
    this.taskStatus = taskStatus
    makeAutoObservable(this)
  }

  // 工商信息Table 原始数据
  compareData = []
  setCompareData = (data) => {
    this.compareData = data
  }

  // 提示弹窗
  tipsConfirmModal = new ModalStore({})
  tipsConfirmContent = ''

  // 详细弹窗
  compareModal = new ModalStore({
    onFinish: async (values) => {
      if (this.canEditOpinion()) {
        const result = values.content.find(
          (item) =>
            item.nodeName === this.taskActivityId &&
            item.moduleName === this.modelKey &&
            item.flowId === this.flowId
        )
        await Api.clientBusinessOpinionAdd({
          flowId: this.flowId,
          moduleName: this.modelKey,
          nodeName: this.taskActivityId,
          contractId: this.contractId,
          opinion: result?.opinion,
        })
        message.success('操作成功')
      }
      this.compareModal.close()
    },
  })

  // 工商信息Table
  tableStore = new TableStore({
    pagination: false,
    request: () => {
      return this.formatTableData(this.compareData)
    },
  })
  // 工商信息Table 格式化
  formatTableData = (item) => {
    const res = []
    res.push(
      {
        fieldName: '客户名称',
        systemValue: item.clientName,
        tycValue: item.clientTycName,
        compareValue: item.clientNameCompare,
      },
      {
        fieldName: '法人代表',
        systemValue: item.corpRepresent,
        tycValue: item.corpTycRepresent,
        compareValue: item.corpRepresentCompare,
      },
      {
        fieldName: '股东信息',
        systemValue: item.shareHolderInfo,
        tycValue: item.shareHolderTycInfo,
        compareValue: item.shareHolderInfoCompare,
      }
    )
    return res
  }

  // 付款申请提交的方法
  submitFn = null
  // 工商信息校验
  checkCompare = async () => {
    const res = await Api.corpCommerceValid({
      clientId: this.clientId,
    })

    this.setCompareData(res)
    const isChange = res.changeFlag

    if (isChange) {
      this.tipsConfirmContent = `存在客户信息与外数校验不一致，请查看处理！`
      this.tipsConfirmModal.open()
    } else {
      Modal.info({
        title: '工商信息校验',
        content: `客户信息与外数校验一致，请知悉！`,
      })
    }
  }
}
export default Store
