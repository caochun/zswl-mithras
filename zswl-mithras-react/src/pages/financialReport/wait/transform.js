import financialReportApi from '@/api/financialReport/financialReportApi'

// 利润表数据配置
const profitColumns = [
  {
    title: '一、主营业务收入',
    itemLevel: 0,
    actmField: 'mainBusiIncmActm',
    tyagField: 'mainBusiIncmTyag',
    cplyField: 'mainBusiIncmCply',
  },
  {
    title: '减：主营业务成本',
    itemLevel: 1,
    actmField: 'mainBusiCostActm',
    tyagField: 'mainBusiCostTyag',
    cplyField: 'mainBusiCostCply',
  },
  {
    title: '主营业务税金及附加',
    itemLevel: 2,
    actmField: 'mainBusiTaxAddActm',
    tyagField: 'mainBusiTaxAddTyag',
    cplyField: 'mainBusiTaxAddCply',
  },
  {
    title: '二、主营业务利润',
    itemLevel: 0,
    actmField: 'mainBusiProfActm',
    tyagField: 'mainBusiProfTyag',
    cplyField: 'mainBusiProfCply',
  },
  {
    title: '加：其他业务利润',
    itemLevel: 1,
    actmField: 'othBusiProfActm',
    tyagField: 'othBusiProfTyag',
    cplyField: 'othBusiProfCply',
  },
  {
    title: '减：营业费用',
    itemLevel: 1,
    actmField: 'busiFeeActm',
    tyagField: 'busiFeeTyag',
    cplyField: 'busiFeeCply',
  },
  {
    title: '管理费用',
    itemLevel: 2,
    actmField: 'magFeeActm',
    tyagField: 'magFeeTyag',
    cplyField: 'magFeeCply',
  },
  {
    title: '财务费用',
    itemLevel: 2,
    actmField: 'finFeeActm',
    tyagField: 'finFeeTyag',
    cplyField: 'finFeeCply',
  },
  {
    title: '资产减值损失',
    itemLevel: 2,
    actmField: 'ipoaLossActm',
    tyagField: 'ipoaLossTyag',
    cplyField: 'ipoaLossCply',
  },
  {
    title: '信用减值损失',
    itemLevel: 2,
    actmField: 'credDecrLossActm',
    tyagField: 'credDecrLossTyag',
    cplyField: 'credDecrLossCply',
  },
  {
    title: '三、营业利润',
    itemLevel: 0,
    actmField: 'busiProfActm',
    tyagField: 'busiProfTyag',
    cplyField: 'busiProfCply',
  },
  {
    title: '加：投资收益',
    itemLevel: 1,
    actmField: 'ivsmPayfActm',
    tyagField: 'ivsmPayfTyag',
    cplyField: 'ivsmPayfCply',
  },
  {
    title: '营业外收入',
    itemLevel: 2,
    actmField: 'noprIncmActm',
    tyagField: 'noprIncmTyag',
    cplyField: 'noprIncmCply',
  },
  {
    title: '减：营业外支出',
    itemLevel: 2,
    actmField: 'noprPayActm',
    tyagField: 'noprPayTyag',
    cplyField: 'noprPayCply',
  },
  {
    title: '四、利润总额',
    itemLevel: 0,
    actmField: 'profGamtActm',
    tyagField: 'profGamtTyag',
    cplyField: 'profGamtCply',
  },
  {
    title: '减：所得税费用',
    itemLevel: 1,
    actmField: 'inctFeeActm',
    tyagField: 'inctFeeTyag',
    cplyField: 'inctFeeCply',
  },
  {
    title: '五、净利润',
    itemLevel: 0,
    actmField: 'netProfActm',
    tyagField: 'netProfTyag',
    cplyField: 'netProfCply',
  },
]

/**
 * 转换利润表数据的函数
 * @param {Object} data - 利润表数据对象
 * @returns {Array} 转换后的表格数据数组
 */
export const transformProfitData = (data) => {
  if (!data) return []

  return profitColumns.map(
    ({ title: item, itemLevel, actmField, tyagField, cplyField, ...rest }, index) => ({
      item,
      itemLevel,
      mainBusiIncmActm: data[actmField],
      mainBusiIncmTyag: data[tyagField],
      mainBusiIncmCply: data[cplyField],
      rowNum: index + 1,
      id: index + 1,
      ...rest,
      parentId: data.id,
    })
  )
}

// 经营情况数据配置
const businessSituationColumns = [
  {
    title: '总收入',
    itemLevel: 0,
    abopField: 'totIncmAbop',
    aotcField: 'totIncmAotc',
    aeopField: 'totIncmAeop',
  },
  {
    title: '其中：经营租赁业务收入',
    itemLevel: 1,
    abopField: 'operLeasBusiIncmAbop',
    aotcField: 'operLeasBusiIncmAotc',
    aeopField: 'operLeasBusiIncmAeop',
  },
  {
    title: '融资租赁业务收入',
    itemLevel: 1,
    abopField: 'fnlBusiIncmAbop',
    aotcField: 'fnlBusiIncmAotc',
    aeopField: 'fnlBusiIncmAeop',
  },
  {
    title: '其中：利息收入',
    itemLevel: 2,
    abopField: 'intrIncmAbop',
    aotcField: 'intrIncmAotc',
    aeopField: 'intrIncmAeop',
  },
  {
    title: '费用收入',
    itemLevel: 3,
    abopField: 'feeIncmAbop',
    aotcField: 'feeIncmAotc',
    aeopField: 'feeIncmAeop',
  },
  {
    title: '其他收入',
    itemLevel: 2,
    abopField: 'othIncmAbop',
    aotcField: 'othIncmAotc',
    aeopField: 'othIncmAeop',
  },
  {
    title: '租赁资产',
    itemLevel: 0,
    abopField: 'leasAstAbop',
    aotcField: 'leasAstAotc',
    aeopField: 'leasAstAeop',
  },
  {
    title: '其中：经营租赁资产',
    itemLevel: 1,
    abopField: 'operLeasAstAbop',
    aotcField: 'operLeasAstAotc',
    aeopField: 'operLeasAstAeop',
  },
  {
    title: '融资租赁资产',
    itemLevel: 2,
    abopField: 'finLeasAstAbop',
    aotcField: 'finLeasAstAotc',
    aeopField: 'finLeasAstAeop',
  },
  {
    title: '其中：直接租赁资产',
    itemLevel: 2,
    abopField: 'dirtLeasAstAbop',
    aotcField: 'dirtLeasAstAotc',
    aeopField: 'dirtLeasAstAeop',
  },
  {
    title: '售后回租资产',
    itemLevel: 3,
    abopField: 'slbkAstAbop',
    aotcField: 'slbkAstAotc',
    aeopField: 'slbkAstAeop',
  },
  {
    title: '跨省融资租赁资产余额（承租人为省外）',
    itemLevel: 0,
    abopField: 'iprvFnlAstBalAbop',
    aotcField: 'iprvFnlAstBalAotc',
    aeopField: 'iprvFnlAstBalAeop',
  },
  {
    title: '其中：跨省售后回租资产余额',
    itemLevel: 2,
    abopField: 'iprvSlbkAstBalAbop',
    aotcField: 'iprvSlbkAstBalAotc',
    aeopField: 'iprvSlbkAstBalAeop',
  },
  {
    title: '融资租赁投放额',
    itemLevel: 0,
    abopField: 'fnlRelsAbop',
    aotcField: 'fnlRelsAotc',
    aeopField: 'fnlRelsAeop',
  },
  {
    title: '其中：直接租赁投放额',
    itemLevel: 1,
    abopField: 'dirtLeasRelsAbop',
    aotcField: 'dirtLeasRelsAotc',
    aeopField: 'dirtLeasRelsAeop',
  },
  {
    title: '售后回租投放额',
    itemLevel: 2,
    abopField: 'slbkRelsAbop',
    aotcField: 'slbkRelsAotc',
    aeopField: 'slbkRelsAeop',
  },
  {
    title: '固定收益类证券投资余额',
    itemLevel: 0,
    abopField: 'fixPayfScrIvsmAbop',
    aotcField: 'fixPayfScrIvsmAotc',
    aeopField: 'fixPayfScrIvsmAeop',
  },
  {
    title: '国债余额',
    itemLevel: 0,
    abopField: 'treaAbop',
    aotcField: 'treaAotc',
    aeopField: 'treaAeop',
  },
  {
    title: '资产减值损失准备',
    itemLevel: 0,
    abopField: 'ipoaLossAbop',
    aotcField: 'ipoaLossAotc',
    aeopField: 'ipoaLossAeop',
  },
]

/**
 * 转换经营情况数据的函数
 * @param {Object} data - 经营情况数据对象
 * @returns {Array} 转换后的表格数据数组
 */
export const transformBusinessSituationData = (data) => {
  if (!data) return []

  return businessSituationColumns.map(
    ({ title: item, itemLevel, abopField, aotcField, aeopField, ...rest }, index) => ({
      item,
      itemLevel,
      totIncmAbop: data[abopField],
      totIncmAotc: data[aotcField],
      totIncmAeop: data[aeopField],
      rowNum: index + 1,
      id: index + 1,
      ...rest,
      parentId: data.id,
    })
  )
}

/**
 * 业务情况数据修改API
 * @param {Object} data - 包含id和各列数据的对象
 * @returns {Promise} API调用结果
 */
export const businessSituationModifyApi = ({ dataList, reportInstanceId }) => {
  console.log('dataList: ', dataList)
  const params = {
    id: dataList[0].parentId,
    reportInstanceId,
  }
  businessSituationColumns.forEach((item, index) => {
    params[item.abopField] = dataList[index].totIncmAbop
    params[item.aotcField] = dataList[index].totIncmAotc
    params[item.aeopField] = dataList[index].totIncmAeop
  })

  return financialReportApi.postModifyBusinessSituation(params)
}

/**
 * 利润表数据修改API
 * @param {Object} data - 包含id和各列数据的对象
 * @returns {Promise} API调用结果
 */
export const profitModifyApi = async ({ reportInstanceId, dataList: data }) => {
  const params = {
    reportInstanceId,
    id: data[0].parentId,
    ...profitColumns.reduce((acc, col, index) => {
      acc[col.actmField] = data[index].mainBusiIncmActm
      acc[col.tyagField] = data[index].mainBusiIncmTyag
      acc[col.cplyField] = data[index].mainBusiIncmCply
      return acc
    }, {}),
  }
  return await financialReportApi.postModifyCompanyProfit(params)
}
