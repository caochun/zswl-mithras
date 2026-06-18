import financialReportApi from '@/api/report/financialReportApi'
import {
  transformProfitData,
  transformBusinessSituationData,
  profitModifyApi,
  businessSituationModifyApi,
} from './transform'
import { balanceSheetModifyApi, transformBalanceData } from './transformBalanceData'

import {
  businessColumns,
  economyColumns,
  profitColumns,
  businessSituationColumns,
  mainBusinessColumns,
  relatedColumns,
} from './TableColumns'
import { entityEconomyServiceModifyApi, transformEntityEconomyServiceData } from './transform2'
import {
  basicSituationColumns,
  executiveInfoColumns,
  legalInfoColumns,
  majorMattersBasicColumns,
  majorMattersReportColumns,
  shareholderChangeColumns,
  shareholderInfoColumns,
} from './RealtimeTableColumns'
import {
  baseInfoModifyApi,
  majorMattersBasicColumnsTransform,
  transformBaseInfoData,
} from './baseInfoTransform'
import { balanceColumns, topTenColumns } from './TableCoumns2'

// 统一配置对象，包含columns和api
export const tableConfig = {
  // 实时报表配置
  J0001: {
    title: '基本情况统计表',
    columns: basicSituationColumns, // 基本情况统计表
    transform: transformBaseInfoData,
    api: financialReportApi.postDetailBasicSituation,
    modifyApi: baseInfoModifyApi,
    needAdd: false,
  },
  J0002: {
    title: '股东股权信息一览表-股东股权信息',
    columns: shareholderInfoColumns, // 股东股权信息一览表-股东股权信息
    api: financialReportApi.postDetailShahStorInfo,
    modifyApi: financialReportApi.postModifyShahStorInfo,
    unit: '万元',
  },
  J0003: {
    title: '股东股权信息一览表-股东变更记录',
    columns: shareholderChangeColumns, // 股东股权信息一览表-股东变更记录
    api: financialReportApi.postDetailShahChangeInfo,
    modifyApi: financialReportApi.postModifyShahChangeInfo,
  },
  J0004: {
    title: '高管信息一览表',
    columns: executiveInfoColumns, // 高管信息一览表
    api: financialReportApi.postDetailSeniorExecutiveInfo,
    modifyApi: financialReportApi.postModifySeniorExecutiveInfo,
  },
  J0005: {
    title: '业务情况信息',
    columns: businessSituationColumns, // 业务情况信息
    api: financialReportApi.postDetailBusinessSituation,
    modifyApi: businessSituationModifyApi,
    transform: transformBusinessSituationData,
    unit: '万元',
    needAdd: false,
  },
  J0006: {
    title: '服务实体经济情况表',
    columns: economyColumns, // 服务实体经济情况表
    api: financialReportApi.postDetailEntityEconomyService,
    modifyApi: entityEconomyServiceModifyApi,
    transform: transformEntityEconomyServiceData,
    needAdd: false,
    unit: '万元',
  },
  J0007: {
    title: '资产负债表',
    columns: balanceColumns, // 资产负债表
    api: financialReportApi.postDetailBalanceSheetPartial,
    modifyApi: balanceSheetModifyApi,
    transform: transformBalanceData,
    needAdd: false,
    unit: '元',
  },
  J0008: {
    title: '公司利润表',
    columns: profitColumns, // 公司利润表数据表
    api: financialReportApi.postDetailCompanyProfit,
    modifyApi: profitModifyApi,
    transform: transformProfitData,
    needAdd: false,
    unit: '元',
  },
  J0009: {
    title: '主要业务清单',
    columns: mainBusinessColumns, // 主要业务清单
    api: financialReportApi.postDetailMainBusiness,
    modifyApi: financialReportApi.postModifyMainBusiness,
    unit: '万元',
  },
  J0010: {
    title: '对外融资清单',
    columns: businessColumns, // 对外融资清单
    api: financialReportApi.postDetailExternalFinancing,
    modifyApi: financialReportApi.postModifyExternalFinancing,
    unit: '万元',
  },
  J0011: {
    title: '最大十家客户（含集团）集中度统计信息',
    columns: topTenColumns, // 最大十家客户（含集团）集中度统计信息
    api: financialReportApi.postDetailTop10ClientConcentration,
    modifyApi: financialReportApi.postModifyTop10ClientConcentration,
    unit: '万元',
  },
  J0012: {
    title: '关联方信息汇总表',
    columns: relatedColumns, // 关联方信息汇总表
    api: financialReportApi.postDetailRelation,
    modifyApi: financialReportApi.postModifyRelation,
    unit: '万元',
  },
  J0013: {
    title: '涉法涉讼涉访信息表',
    columns: legalInfoColumns, // 涉法涉讼涉访信息表
    api: financialReportApi.postDetailLawInvolvedVisitRelatedInfo,
    modifyApi: financialReportApi.postModifyLawInvolvedVisitRelatedInfo,
  },
  J0014: {
    title: '重大事项报告表-基本信息',
    columns: majorMattersBasicColumns, // 重大事项报告表-基本信息
    api: financialReportApi.postDetailMajorMattersBasicReport,
    modifyApi: ({ reportInstanceId, dataList: params }) => {
      const newParams = {
        corpName: params[0]?.content,
        leglCptl: params[0]?.content2,
        busiAddr: params[1]?.content,
        corpLegpName: params[1]?.content2,
        brchInsNum: params[2]?.content,
        chrmName: params[2]?.content2,
        gmgrName: params[3]?.content,
        inftContMode: params[3]?.content2,
        reportInstanceId: reportInstanceId,
        id: params[0]?.parentId,
      }
      return financialReportApi.postModifyMajorMattersBasicReport(newParams)
    },
    transform: majorMattersBasicColumnsTransform,
    unit: '万元',
    needAdd: false,
  },
  J0015: {
    title: '重大事项报告表-重大事项报告情况',
    columns: majorMattersReportColumns, // 重大事项报告表-重大事项报告情况
    api: financialReportApi.postDetailMajorMattersEventReport,
    modifyApi: financialReportApi.postModifyMajorMattersEventReport,
    unit: '万元',
  },
}
