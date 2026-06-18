import { hasValue } from '@/utils'
import { http, makeAutoObservable } from '@zswl/admin'
import { Modal, ModalStore, PageStore, TableStore } from '@zswl/components'
import { message } from 'antd'
import { all, create } from 'mathjs'
import Api from './api'
export {
  checkCreditDate,
  postPayMentCheckApplyAmount,
  validateAgreen,
} from '@/utils/domains/cpm/PaymentApplicationUtils'

const mathjs = create(all)

class Store {
  constructor({ businessVersion } = {}) {
    this.businessVersion = businessVersion
    makeAutoObservable(this)
  }

  applicationEditStatus = false
  setApplicationEditStatus = (val) => {
    this.applicationEditStatus = val
  }

  page = new PageStore({
    request: async (params) => {
      const res = await Api.getPaymentDetail({ ...params, businessVersion: this.businessVersion })
      return res ?? {}
    },
  })

  // 支付明细
  paymentPlanTable = new TableStore({
    pagination: false,
    request: async (params) => {
      return this.page.getData().planedDetails?.map((item) => ({
        ...item,
        businessVersion: this.businessVersion,
        editId: item.id,
      }))
    },
  })
  //  交易结构
  transactionTable = new TableStore({
    request: async () => {
      const data = { paymentId: this.page.getParams().id }
      const res = await http.post('/payment/transactionStructureInfo', data)
      return res
    },
  })

  getLessees = () => {
    const transactionTableData = this.transactionTable.getList()
    const userNames = []
    transactionTableData.map((item) => {
      if (['MAIN_LESSSEE', 'JOINT_LESSEE'].includes(item.transactionStructureType)) {
        userNames.push(item.clientName)
      }
    })
    return [...new Set(userNames)]?.join(',')
  }

  // 本次申请保存
  saveApplicationForm = async (values) => {
    const {
      applyPaymentAmount,
      applyPaymentDate,
      earnestMoney,
      downPayment,
      consultingFee,
      nominalPrice,
      remark,
      downPaymentType,
      retentionMoney,
      retentionMoneyType,
    } = values
    await Api.getPaymentDetailModify({
      ...values,
      applyPaymentDate,
      applyPaymentAmount,
      earnestMoney,
      retentionMoney,
      retentionMoneyType,
      downPayment,
      consultingFee,
      nominalPrice,
      details: this.paymentPlanTable.getList(),
      id: this.page.getParams().id,
      remark,
      downPaymentType,
      contractId: this.page.getData()?.contractId,
    })
    this.applicationEditStatus = false
    this.page.init()
  }

  // 新增/编辑 支付明细
  createApplicationModal = new ModalStore({
    onOpen: async (data) => {
      if (data) return data
      const contractId = this.page.getData().contractId
      const res = await Api.getSellerInfo({ contractId })
      return { ...res }
    },
    onFinish: async (values, initValues) => {
      if (initValues?.id) {
        const currentList = this.paymentPlanTable.getList()
        const index = currentList.findIndex((item) => item.editId === initValues.editId)
        currentList.splice(index, 1, {
          ...values,
          id: initValues.id,
          paymentAmount: hasValue(values.paymentAmount) ? mathjs.multiply(mathjs.bignumber(values.paymentAmount), 10000).toString() : undefined,
          paymentId: this.page.getParams().id,
        })
        this.paymentPlanTable.setList(currentList)
      } else {
        this.paymentPlanTable.addRow({
          ...values,
          editId: this.paymentPlanTable.getList().length + 1,
          paymentAmount: hasValue(values.paymentAmount) ? mathjs.multiply(mathjs.bignumber(values.paymentAmount), 10000).toString() : undefined,
          paymentId: this.page.getParams().id,
        })
      }
      this.createApplicationModal.close()
    },
  })

  // 审批日志
  journalTable = new TableStore({})
  publicModal = new ModalStore({})
  checkPublic = async (afterFunc) => {
    const paymentId = this.page.getParams().id
    const customerList = await Api.publicCheck({ paymentId })
    if (customerList.length) {
      this.publicModal.open({ customerList })
      return
    }
    afterFunc?.()
  }
  beforeSubmit = async () => {
    await checkCreditDate({ paymentId: this.page.getParams().id })
    //付款申请-运营经办/复核提交流程时
    const paymentId = this.page.getParams().id
    let params = {
      paymentId,
    }
    const result = await Api.postPayMentCheckApplyAmount({
      ...params,
    })
    if (result.isOverApprovedAmount || result.isOverContractAmount) {
      Modal.confirm({
        title: result.isOverContractAmount ? '付款申请金额已超出合同金额，是否继续提交？' : '付款申请金额已超出项目批复金额，是否继续提交？',
        onOk: async () => {
          if (result.needConfirmTips) {
            Modal.confirm({
              title: result.tipMessage,
              onOk: async () => {
                this.validateBeforeSubmit()
              },
            })
          } else {
            this.validateBeforeSubmit()
          }
        },
      })
    } else if (result.needConfirmTips) {
      if ('暂未纳入资金计划，请联系资金经理确认！' === result.tipMessage) {
        Modal.confirm({
          title: result.tipMessage,
          cancelText: '继续提交',
          okText: '确认',
          onCancel: async () => {
            if (result.isOverApprovedAmount || result.isOverContractAmount) {
              Modal.confirm({
                title: result.isOverContractAmount ? '付款申请金额已超出合同金额，是否继续提交？' : '付款申请金额已超出项目批复金额，是否继续提交？',
                onOk: async () => {
                  this.validateBeforeSubmit()
                },
              })
            } else {
              this.validateBeforeSubmit()
            }
          },
        })
        return
      }
      Modal.confirm({
        title: result.tipMessage,
        onOk: async () => {
          if (result.isOverApprovedAmount || result.isOverContractAmount) {
            Modal.confirm({
              title: result.isOverContractAmount ? '付款申请金额已超出合同金额，是否继续提交？' : '付款申请金额已超出项目批复金额，是否继续提交？',
              onOk: async () => {
                this.validateBeforeSubmit()
              },
            })
          } else {
            this.validateBeforeSubmit()
          }
        },
      })
    } else {
      this.validateBeforeSubmit()
    }
  }

  // 提交审批
  validateBeforeSubmit = async () => {
    if (this.applicationEditStatus) {
      message.info('请先保存本次申请！')
      return
    }

    const paymentId = this.page.getParams().id
    const hasClientOpinion = await Api.postPaymentCheckClientOpinion({ id: paymentId })

    const nextHandle = async () => {
      const res = await Api.paymentTimeOutCheck({ id: paymentId })
      if (res === 1) {
        Modal.confirm({
          title: '提示',
          content: `距离最新项目评审流程审批通过日期已超过6 个月，是否继续提交？`,
          onOk: () => {
            this.page.getParams().businessRef.current.setSubmitFn(() => this.submit())
            this.page.getParams().businessRef.current.store.checkCompare()
          },
        })
      } else {
        this.page.getParams().businessRef.current.setSubmitFn(() => this.submit())
        this.page.getParams().businessRef.current.store.checkCompare()
      }
    }

    if (hasClientOpinion) {
      Modal.confirm({
        title: '该合同存在未处理完成的舆情信息，请关注舆情处理流程进度！',
        onOk: async () => {
          await nextHandle()
        },
      })
    } else {
      await nextHandle()
    }
  }

  // 风险提醒
  tipsModal = new ModalStore({})
  submit = async () => {
    await Api.postPaymentSubmit({ id: this.page.getParams().id })
    message.success('提交成功')
    this.page.getParams().businessRef.current.store.tipsConfirmModal.close()
  }

  filesPreview = ({ id }) => {
    if (id) {
      window.open(`/preview/reportPreview/${id}`)
    }
  }
}
export default Store
