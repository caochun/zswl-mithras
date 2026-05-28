// 概算租金表、实际租金表 文字替换
export const bizTypeMapText = {
  BL: {
    rentTitle: '支付表',
    rentText: '应收保理款',
    dateText: '支付日',
  },
  ZR: {
    rentTitle: '支付表',
    rentText: '回收款',
    dateText: '支付日',
  },
  ZL: {
    rentTitle: '租金表',
    rentText: '租金',
    dateText: '起租日',
  },
  ZZ: {
    rentTitle: '租金表',
    rentText: '租金',
    dateText: '起租日',
  },
}
// 报价方案详情对应字段
export const bizTypePriceDetailMap = {
  ZL: 'leasePriceModifyRSP',
  BL: 'factoringPriceRSP',
  ZZ: 'leasePriceModifyRSP',
  ZR: 'aocPriceRSP',
}
// 报价方案修改对应字段
export const bizTypePriceModifyMap = {
  ZL: 'leasePriceModifyREQ',
  BL: 'factoringPriceModifyREQ',
  ZZ: 'leasePriceModifyREQ',
  ZR: 'aocPriceModifyREQ',
}

export const contractOperationMap = {
  START_RENT: 'START_RENT',
  NEW_RECEIPT: 'NEW_RECEIPT',
  LPR_CHANGE: 'CHANGE_LPR',
  EARLY_REPAYMENT: 'CHANGE_REPAYMENT_IN_ADVANCE',
  EXTENSION: 'CHANGE_EXTENSION',
  CHANGE_REPAY_PLAN: 'CHANGE_REPAYMENT_PLAN',
  OTHER: 'CHANGE_OTHER',
  SETTLE_NORMAL: 'SETTLE_NORMAL',
  SETTLE_IN_ADVANCE: 'SETTLE_IN_ADVANCE',
}
