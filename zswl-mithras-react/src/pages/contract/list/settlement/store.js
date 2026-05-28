import { makeAutoObservable, history } from '@zswl/admin'
import { message, Modal } from 'antd'
import { PageStore } from '@zswl/components'
import { timeFormat } from '@/utils'
import Api from './api'

class Store {
  constructor(data) {
    makeAutoObservable(this)
    this.isFormApproval = data?.isFormApproval
    this.canEdit = data?.canEdit
  }

  settleDetail = {}
  page = new PageStore({
    request: async ({ id, planType, businessVersion }) => {
      const res = await Api.getSettleDetail({
        planType,
        contractId: id,
        needReal: this.isFormApproval ? (this.canEdit ? 1 : 0) : 1,
        businessVersion,
      })
      this.settleDetail = { ...res }
      return res
    },
  })

  saveNormalData = async (values) => {
    await Api.updateSettleNormal({
      ...this.settleDetail,
      ...values,
      originalDeadline: this.settleDetail.originalDeadline
        ? timeFormat(this.settleDetail.originalDeadline)
        : undefined,
      id: this.settleDetail?.id,
      contractId: this.page.getParams().id,
    })
    this.page.init()
  }
  saveInadvanceData = async (values) => {
    await Api.updateSettleInadvance({
      ...values,
      originalDeadline: this.settleDetail.originalDeadline
        ? timeFormat(this.settleDetail.originalDeadline)
        : undefined,
      applySettleDate: values.applySettleDate ? timeFormat(values.applySettleDate) : undefined,
      id: this.settleDetail?.id,
      contractId: this.page.getParams().id,
    })
    this.page.init()
  }

  onSubmit = async () => {
    await Api.submitSettle({
      settlePlanType: this.page.getParams().planType,
      contractId: this.page.getParams().id,
      settlePlanId: this.settleDetail.id,
    })
    message.info('提交成功')
    this.page.init()
  }

  cancelFlow = async () => {
    Modal.confirm({
      title: `是否取消操作？`,
      onOk: async () => {
        await Api.cancelFlow({
          contractId: this.page.getParams().id,
        })
        message.success('操作成功')
        setTimeout(() => {
          history.push(`/contract/list`)
        }, 500)
      },
    })
  }
}
export default Store
