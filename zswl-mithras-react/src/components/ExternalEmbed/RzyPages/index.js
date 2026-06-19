import RzyIframe from '../RzyIframe'

const createRzyPage = (title) => {
  const RzyPage = () => <RzyIframe title={title} />

  return RzyPage
}

export const RzyDealerMaintain = createRzyPage('经销商维护')
export const RzyDealerSearch = createRzyPage('经销商查询')
export const RzyVendorCustomerManage = createRzyPage('厂商客户管理')
export const RzyVendorCreditLimitManage = createRzyPage('厂商授信额度管理')
export const RzyVendorProductDefinition = createRzyPage('厂商产品定义')
export const RzyApplicationManage = createRzyPage('进件管理')
export const RzyApplicationRecord = createRzyPage('进件记录')
export const RzyVendorContractManage = createRzyPage('厂商合同管理')
export const RzyVendorContractSeal = createRzyPage('厂商合同盖章')
export const RzyVendorElectronicContractSeal = createRzyPage('厂商电子合同盖章')
export const RzyPaymentApplicationMaintain = createRzyPage('付款申请维护')
export const RzyPaymentPay = createRzyPage('付款支付')
export const RzyPaymentReverse = createRzyPage('付款反冲')
