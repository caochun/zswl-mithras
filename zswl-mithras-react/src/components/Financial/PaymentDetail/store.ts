import { message } from 'antd'
import { history, makeAutoObservable } from '@zswl/admin'
import { PageStore } from '@zswl/components'
import paymentApprovalApi from '@/api/financial/paymentApprovalApi'
import fundReceiptRepayBaseInfoApi from '@/api/financial/fundReceiptRepayBaseInfoApi'
import { compareDetail } from '@/utils'
class Store {
  constructor() {
    makeAutoObservable(this)
  }
  page = new PageStore({
    request: async ({ isFormApproval, ...rest }) => {
      const baseInfo = await fundReceiptRepayBaseInfoApi.postFinancingType({ ...rest })

      if (baseInfo?.financingType === 'DIRECT') return { newDetail: {}, baseInfo }
      const params = {
        ...rest,
      }
      if (!isFormApproval) {
        const res = await fundReceiptRepayBaseInfoApi.postInfoDetail({ id: params?.id })
        return { newDetail: res, baseInfo }
      }
      const res = await fundReceiptRepayBaseInfoApi.postInfoDetailCompare({ id: params?.id })
      const { newDetail, isLog } = compareDetail(res)
      return { newDetail, isLog, baseInfo }
    },
  })
  submitApproval = async (id: string, version?: string) => {
    await paymentApprovalApi.postRepaySubmit({ id, version }).then((res) => {
      message.success('提交审批成功')
    })
  }
  changeLog = (id: string) => {
    history.push(`/financial/payment/detail/log/${id}`)
  }
  saveData = async (data: any) => {
    const { id } = this.page.getParams()
    const res = await fundReceiptRepayBaseInfoApi.postInfoModify({ ...data, id })
    await this.page.init()
    return res
  }
}
export default Store
