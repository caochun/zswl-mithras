import { makeAutoObservable } from '@zswl/admin'
import { Modal, ModalStore, TableStore } from '@zswl/components'
import Api from './api'
import { message } from 'antd'
import creditReportApi from '@/api/credit/creditReportApi'

class Store {
  constructor({
    contractId,
    flowId,
    modelKey,
    taskActivityId,
    paymentId,
    taskStatus,
    creditSearchId,
  }) {
    this.contractId = contractId
    this.paymentId = paymentId
    this.flowId = flowId
    this.modelKey = modelKey
    this.taskActivityId = taskActivityId
    this.taskStatus = taskStatus
    this.creditSearchId = creditSearchId
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
  formatTableData = (data) => {
    const res = []
    data?.map((item) => {
      res.push(
        {
          clientType: item.clientType,
          fieldName: '客户名称',
          systemValue: item.clientName,
          tycValue: item.clientTycName,
          compareValue: item.clientNameCompare,
        },
        {
          clientType: item.clientType,
          fieldName: '法人代表',
          systemValue: item.corpRepresent,
          tycValue: item.corpTycRepresent,
          compareValue: item.corpRepresentCompare,
        },
        {
          clientType: item.clientType,
          fieldName: '股东信息',
          systemValue: item.shareHolderInfo,
          tycValue: item.shareHolderTycInfo,
          compareValue: item.shareHolderInfoCompare,
        }
      )
    })
    return res
  }

  // 付款申请提交的方法
  submitFn = null
  // 工商信息校验
  checkCompare = async () => {
    const { paymentId, contractId, flowId, creditSearchId } = this
    let res
    if (paymentId) {
      res = await Api.paymentClientBusinessCompare({ paymentId, flowId })
    } else if (contractId) {
      res = await Api.contractClientBusinessCompare({ contractId, flowId })
    } else {
      res = await creditReportApi.postCompareBusiness({ creditSearchId, flowId })
    }

    this.setCompareData(res)
    const hasLesseeChange = res.some(
      (item) => item.changeFlag === 1 && item.clientType === '承租人'
    )
    const hasGuarantorChange = res.some(
      (item) => item.changeFlag === 1 && item.clientType === '担保人'
    )

    if (hasLesseeChange || hasGuarantorChange) {
      let text = ''
      if (hasLesseeChange) text += '承租人'
      if (hasGuarantorChange) text += '/担保人'
      this.tipsConfirmContent = `存在交易结构中${text}信息与外数校验不一致，请查看处理！`
      this.tipsConfirmModal.open()
    } else {
      if (this.submitFn) {
        this.submitFn()
        return
      }
      Modal.info({
        title: '工商信息校验',
        content: `交易结构中法人客户信息与外数校验一致，请知悉！`,
      })
    }
  }

  // 审批意见

  optionList = []
  setOptionList = (data) => {
    this.optionList = data
  }
  getOpinionList = async () => {
    const res = await Api.clientBusinessOpinionList({
      contractId: this.contractId,
      pageSize: 500,
    })
    const isCanEdit = this.canEditOpinion()
    const list = res?.list || []

    if (!isCanEdit) {
      this.setOptionList(list)
    } else {
      const resultIndex = list.findIndex(
        (item) =>
          item.moduleName === this.modelKey &&
          item.nodeName === this.taskActivityId &&
          item.flowId === this.flowId
      )

      // 为了把当前可编辑项放在最前面
      const targetResult = resultIndex > -1 ? list.splice(resultIndex, 1) : []
      const newResult = [
        resultIndex === -1 && {
          flowId: this.flowId,
          moduleName: this.modelKey,
          nodeName: this.taskActivityId,
          opinion: '',
        },
        targetResult?.[0],
        ...list,
      ].filter(Boolean)

      this.setOptionList(newResult)
    }
  }

  canEditOpinion = () => {
    const canEditTask = ['1'].includes(this.taskStatus)
    // 收付款审批
    if (this.modelKey === 'PaymentCreateFlow') {
      // 发起人、项目经理、付款审核岗
      return (
        canEditTask &&
        ['userTask_startUser', 'userTask_projectmanager', 'userTask_loanReviewPost'].includes(
          this.taskActivityId
        )
      )
    } else if (
      [
        'ContractCreateFlow',
        'ContractModifyFlow',
        'ContractExtensionFlow',
        'ContractEarlySettleFlow',
        'ContractEarlyRepayFlow',
        'ContractChangeRepayPlanFlow',
      ].includes(this.modelKey)
    ) {
      // 合同创建、变更
      // 发起人、运营
      return canEditTask && ['userTask_startUser', 'Activity_0wrrxch'].includes(this.taskActivityId)
    }
    return false
  }

  // nodelable
  formatNodeName = (taskActivityId) => {
    let name = ''
    if (taskActivityId === 'userTask_startUser') name = '项目经理'
    if (taskActivityId === 'userTask_projectmanager') name = '项目经理'
    if (taskActivityId === 'userTask_loanReviewPost') name = '放款审核岗'
    if (taskActivityId === 'Activity_0wrrxch') name = '运营管理岗'
    return name
  }
}
export default Store
