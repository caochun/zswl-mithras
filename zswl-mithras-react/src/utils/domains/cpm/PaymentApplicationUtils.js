import PaymentApplicationDetailApi from '@/api/cpm/payment/paymentApplicationDetail'
import { validateModal } from '@/utils/modal'
import { Modal } from '@zswl/components'

export const checkCreditDate = async (params, functionCode = 'paymentMeetMinuteCreditDateCheck') => {
  const { effect } = await PaymentApplicationDetailApi.checkCreditDate(params, functionCode)
  return await validateModal(
    {
      title: '提示',
      content: `发起时间超出纪要中的授信到期日，是否继续提交？`,
    },
    effect
  )
}

export const validateAgreen = async (params) => {
  return PaymentApplicationDetailApi.validateAgreen(params)
}

export const postPaymentCheckApplyAmount = async (params) => {
  const result = await PaymentApplicationDetailApi.postPaymentCheckApplyAmount(params)
  if ('暂未纳入资金计划，请联系资金经理确认！' === result.tipMessage) {
    return new Promise(async (resolve, reject) => {
      if (result.needConfirmTips) {
        Modal.confirm({
          title: result.tipMessage,
          cancelText: '继续提交',
          okText: '确认',
          onOk: async () => {
            reject()
          },
          onCancel: async () => {
            resolve()
          },
        })
      } else {
        resolve()
      }
    })
  }
  return await validateModal(
    {
      title: result.tipMessage,
    },
    result.needConfirmTips
  )
}
