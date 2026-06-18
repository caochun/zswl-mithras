import { PageStore, Modal } from '@zswl/components'
import { makeAutoObservable, history } from '@zswl/admin'
import { userIsProjSponsor } from '@/utils'
import { message } from 'antd'
import Api from './api'

const TypeTextMap = {
  REPAYMENT: '调整方案',
  EXTEND: '展期方案',
}
class Store {
  constructor() {
    makeAutoObservable(this)
  }

  // 是否主办
  isProjSponsor = false
  page = new PageStore({
    request: async ({ adjustId, businessVersion }) => {
      const res = await Api.getDetail({ adjustId, businessVersion })
      if (res) {
        this.isProjSponsor = userIsProjSponsor(res?.projSponsorUserId)
        return {
          ...res,
          typeText: TypeTextMap[res.afterLeaseAdjustType],
        }
      }
      return {}
    },
  })
  showValue = true
  setShowValue = (v) => {
    this.showValue = v
  }
  onSubmit = async () => {
    if (!this.showValue) {
      const text = this.page.getData().typeText
      message.info(`${text}未保存，请先保存后提交审批！`)
      return
    }
    await Api.submit({
      adjustId: this.page.getData().id,
    })
    message.success('提交成功')
    this.setShowValue(true)
  }
  onSave = async (values) => {
    await Api.modifyInfo({
      id: this.page.getData().id,
      extensionmonth: values.extensionmonth,
      adjustExplain: values.adjustExplain,
      riskControlManagerId: values.riskControlManagerId,
      legalManagerUserId: values.legalManagerUserId,
    })
    message.success('保存成功')
    this.page.init()
    this.setShowValue(true)
  }

  cancelFlow = async () => {
    Modal.confirm({
      title: `是否取消操作？`,
      onOk: async () => {
        await Api.cancelFlow({
          mainId: this.page.getData().id,
          cancelType: this.page.getData().afterLeaseAdjustType,
        })
        message.info('操作成功')
        setTimeout(() => {
          history.push(`/afterLease/adjust`)
        }, 500)
      },
    })
  }
}
export default Store
