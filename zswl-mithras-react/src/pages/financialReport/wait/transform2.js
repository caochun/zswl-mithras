import financialReportApi from '@/api/financialReport/financialReportApi'

// 实体经济服务数据配置
const entityEconomyServiceColumns = [
  {
    item: '本年租赁业务累计投放额',
    itemLevel: 0,
    periodKey: 'tyagLeasBusiAmtAbop',
    amountKey: 'tyagLeasBusiAmtAotc',
    endPeriodKey: 'tyagLeasBusiAmtAeop',
  },
  {
    item: '其中：本年制造业租赁累计投放额',
    itemLevel: 1,
    periodKey: 'tyagMnftLeasAmtAbop',
    amountKey: 'tyagMnftLeasAmtAotc',
    endPeriodKey: 'tyagMnftLeasAmtAeop',
  },
  {
    item: '本年产业链租赁累计投放额',
    itemLevel: 2,
    periodKey: 'tyagIndtLeasAmtAbop',
    amountKey: 'tyagIndtLeasAmtAotc',
    endPeriodKey: 'tyagIndtLeasAmtAeop',
  },
  {
    item: '本年服务民生消费租赁累计投放额',
    itemLevel: 2,
    periodKey: 'tyagConsLeasAmtAbop',
    amountKey: 'tyagConsLeasAmtAotc',
    endPeriodKey: 'tyagConsLeasAmtAeop',
  },
  {
    item: '本年科技金融建设租赁累计投放额',
    itemLevel: 2,
    periodKey: 'tyagSatyLeasAmtAbop',
    amountKey: 'tyagSatyLeasAmtAotc',
    endPeriodKey: 'tyagSatyLeasAmtAeop',
  },
  {
    item: '本年绿色金融建设租赁累计投放额',
    itemLevel: 2,
    periodKey: 'tyagGrenLeasAmtAbop',
    amountKey: 'tyagGrenLeasAmtAotc',
    endPeriodKey: 'tyagGrenLeasAmtAeop',
  },
  {
    item: '本年普惠金融建设租赁累计投放额',
    itemLevel: 2,
    periodKey: 'tyagIcveLeasAmtAbop',
    amountKey: 'tyagIcveLeasAmtAotc',
    endPeriodKey: 'tyagIcveLeasAmtAeop',
  },
  {
    item: '本年养老金融建设租赁累计投放额',
    itemLevel: 2,
    periodKey: 'tyagPensLeasAmtAbop',
    amountKey: 'tyagPensLeasAmtAotc',
    endPeriodKey: 'tyagPensLeasAmtAeop',
  },
  {
    item: '本年海洋金融建设租赁累计投放额',
    itemLevel: 2,
    periodKey: 'tyagOceaLeasAmtAbop',
    amountKey: 'tyagOceaLeasAmtAotc',
    endPeriodKey: 'tyagOceaLeasAmtAeop',
  },
  {
    item: '本年开放金融建设租赁累计投放额',
    itemLevel: 2,
    periodKey: 'tyagOpenLeasAmtAbop',
    amountKey: 'tyagOpenLeasAmtAotc',
    endPeriodKey: 'tyagOpenLeasAmtAeop',
  },
  {
    item: '本年累计服务客户数',
    itemLevel: 0,
    periodKey: 'tyagServCustNumAbop',
    amountKey: 'tyagServCustNumAotc',
    endPeriodKey: 'tyagServCustNumAeop',
    itemProps: {
      precision: 0,
    },
  },
  {
    item: '历年累计租赁业务金额（含本年）',
    itemLevel: 0,
    periodKey: 'oyagLeasBusiAmtAbop',
    amountKey: 'oyagLeasBusiAmtAotc',
    endPeriodKey: 'oyagLeasBusiAmtAeop',
  },
  {
    item: '历年累计服务客户数（含本年）',
    itemLevel: 0,
    periodKey: 'oyagServCustNumAbop',
    amountKey: 'oyagServCustNumAotc',
    endPeriodKey: 'oyagServCustNumAeop',
    itemProps: {
      precision: 0,
    },
  },
  {
    item: '本年累计实际缴纳各类税金',
    itemLevel: 0,
    periodKey: 'thsyTaxpAmtAbop',
    amountKey: 'thsyTaxpAmtAotc',
    endPeriodKey: 'thsyTaxpAmtAeop',
  },
  {
    item: '其中：（1）增值税',
    itemLevel: 1,
    periodKey: 'incrTaxAbop',
    amountKey: 'incrTaxAotc',
    endPeriodKey: 'incrTaxAeop',
  },
  {
    item: '（2）企业所得税',
    itemLevel: 3,
    periodKey: 'corpInctAbop',
    amountKey: 'corpInctAotc',
    endPeriodKey: 'corpInctAeop',
  },
  {
    item: '（3）其他税金',
    itemLevel: 3,
    periodKey: 'othTaxAbop',
    amountKey: 'othTaxAotc',
    endPeriodKey: 'othTaxAeop',
  },
  {
    item: '历年累计实际缴纳各类税金（含本年）',
    itemLevel: 0,
    periodKey: 'otyTaxpAmtAbop',
    amountKey: 'otyTaxpAmtAotc',
    endPeriodKey: 'otyTaxpAmtAeop',
  },
  {
    item: '表外业务余额',
    itemLevel: 0,
    periodKey: 'ofblAmtAbop',
    amountKey: 'ofblAmtAotc',
    endPeriodKey: 'ofblAmtAeop',
  },
]

/**
 * 转换实体经济服务数据
 * @param {Object} data - 原始数据对象
 * @returns {Array} 转换后的数据数组
 */
export const transformEntityEconomyServiceData = (data) => {
  if (!data) return []

  return entityEconomyServiceColumns.map((column, index) => ({
    rowNum: index + 1,
    id: `entityEconomyService_${index + 1}`,
    parentId: data.id,
    item: column.item,
    itemLevel: column.itemLevel,
    period: data[column.periodKey],
    amount: data[column.amountKey],
    endPeriod: data[column.endPeriodKey],
    itemProps: column.itemProps,
  }))
}

/**
 * 实体经济服务数据修改API
 * @param {Object} data - 包含id和各列数据的对象
 * @returns {Promise} API调用结果
 */
export const entityEconomyServiceModifyApi = ({ reportInstanceId, dataList }) => {
  const params = {
    reportInstanceId,
    id: dataList[0].parentId,
    ...entityEconomyServiceColumns.reduce((acc, col, index) => {
      const record = dataList[index]
      if (record.period !== undefined) acc[col.periodKey] = Number(record.period)
      if (record.amount !== undefined) acc[col.amountKey] = Number(record.amount)
      if (record.endPeriod !== undefined) acc[col.endPeriodKey] = Number(record.endPeriod)
      return acc
    }, {}),
  }

  return financialReportApi.postModifyEntityEconomyService(params)
}
