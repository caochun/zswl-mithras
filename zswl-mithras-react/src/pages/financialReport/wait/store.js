import { App, Modal, ModalStore, PageStore, TableStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import Api from '@/api/common/fileList'
import financialReportApi from '@/api/financialReport/financialReportApi'
import { downFile, downUrl } from '@/utils'
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
import { message } from 'antd'
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
class Store {
  type = null
  currentId = null

  constructor({ listType }) {
    this.listType = listType
    makeAutoObservable(this)
  }
  page = new PageStore({
    request: (params) => {
      return Api.getDetail({ id: params?.id })
    },
  })

  table = new TableStore({
    request: (params) => {
      const waitReportStatusList = params.reportStatusList?.length
        ? params.reportStatusList
        : ['WAIT', 'FAILURE']
      return financialReportApi.postReportPageList({
        ...params,
        refereePage: this.listType === 'finish' ? 'reported' : undefined,
        reportStatusList: this.listType === 'wait' ? waitReportStatusList : ['SUCCESS'],
      })
    },
  })
  approvalTable = new TableStore({
    request: (params) => {
      const { reportPeriod, reportPeriodCategory, ...rest } = params
      const reportPeriodCategoryList = reportPeriodCategory ? [reportPeriodCategory] : []
      return financialReportApi.postReportPageList({
        processStatusList: ['APPROVAL_PASS'],
        reportStatusList: ['WAIT', 'FAILURE'],
        refereePage: 'apply',
        reportPeriodCategoryList,
        ...params,
      })
    },
  })
  handleChangeCategory = (value) => {
    const { associationReportCategoryRefPeriod } = App.getData().optionsType
    const periodCategory = associationReportCategoryRefPeriod.find(
      (item) => item.label === value
    )?.value
    const form = this.addModal.getFormStore()
    const isRealTime = periodCategory === 'REALTIME'
    form.setFieldsValue({
      periodCategory,
      year: moment(),
      period: isRealTime ? moment().format('YYYY-MM-DD') : '',
    })
  }
  addModal = new ModalStore({
    onFinish: async (values) => {
      const { year, periodCategory, period, reportCategoryCode } = values
      const response = await financialReportApi.postReportCreate({
        year: moment(year).year(),
        periodCategory,
        period: periodCategory === 'REALTIME' ? 0 : period,
        reportCategoryCode,
      })
      this.table.search()
      this.currentId = response
      this.addModal.close()
      this.detailModal.open({ reportCategoryCode, processStatus: 'UN_SUBMIT' })
    },
  })
  detailModal = new ModalStore({})

  // 金融局上报弹窗
  reportModal = new ModalStore({
    onFinish: async () => {
      const { rows, keys } = this.approvalTable.getSelected()
      const { importSuccess, errorMessageList } = await financialReportApi.postReportPush({
        ids: keys,
      })
      if (importSuccess) {
        message.success('金融局上报成功')
        this.table.search()
        this.reportModal.close()
      } else {
        Modal.error({
          width: 800,
          title: '金融局上报失败！',
          content: (
            <div style={{ maxHeight: '400px', overflowY: 'auto' }}>
              {errorMessageList.map((item, index) => (
                <div key={index}>{item}</div>
              ))}
            </div>
          ),
        })
      }
    },
  })

  /**
   * 打开金融局上报弹窗
   */
  openReportModal = async () => {
    const params = this.table.getParams()
    this.reportModal.open()
  }
  handleNewReport = () => {
    this.addModal.open({ year: moment() })
  }
  handleDownloadTemplate = async () => {
    const { reportCategoryCode } = this.detailModal.getInitialValues()
    // 处理模板下载
    const url = await financialReportApi.postTemplateFileUrl({
      reportCategoryCode,
    })
    downFile(url)
  }
  import = async (file) => {
    console.log('file: ', file)
    // const { fileList } = DataUpload.classify(files)
    const { importSuccess, errorMessageList } = await financialReportApi.postReportImport({
      reportInstanceId: this.currentId,
      file,
    })
    if (importSuccess) {
      message.success('导入成功')
    } else {
      Modal.error({
        width: 800,
        title: '导入失败！不满足以下勾稽关系无法导入！',
        content: (
          <div style={{ maxHeight: '400px', overflowY: 'auto' }}>
            {errorMessageList.map((item, index) => (
              <div key={index}>{item}</div>
            ))}
          </div>
        ),
      })
    }
  }
  submit = async () => {
    const { rows, keys } = this.table.getSelected()
    const reportInstanceIdList = rows.map((item) => item.reportInstanceId)
    const { importSuccess, errorMessageList } = await financialReportApi.postFlowSubmit({
      reportInstanceIdList,
    })
    if (importSuccess) {
      message.success('提交审批成功')
      this.table.search()
    } else {
      Modal.error({
        width: 800,
        title: '提交审批失败！以下报表未维护完整！',
        content: (
          <div style={{ maxHeight: '400px', overflowY: 'auto' }}>
            {errorMessageList.map((item, index) => (
              <div key={index}>{item}</div>
            ))}
          </div>
        ),
      })
    }
  }
  handleDelete = async ({ id }) => {
    const response = await financialReportApi.postReportDelete({ id })
    this.table.search()
  }
  handleDetail = (record) => {
    this.currentId = record.reportInstanceId // 保存当前记录ID
    this.detailModal.open(record)
  }
}
export default Store
