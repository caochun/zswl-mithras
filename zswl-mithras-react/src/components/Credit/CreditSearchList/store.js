import { TableStore, PageStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import creditReportApi from '@/api/credit/creditReportApi'
import creditSearchProjectApi from '@/api/credit/creditSearchProjectApi'
import creditSearchClientApi from '@/api/credit/creditSearchClientApi'
import { message } from 'antd'

import { downLoadExcel } from '@/components/Excel'

const MODULE_TYPE = 'CREDIT_REPORT_SELECT'
class Store {
  constructor(params) {
    this.params = params
    makeAutoObservable(this)
  }
  params = {}
  page = new PageStore({})
  table = new TableStore({
    request: async (params) => {
      //业务类型 PROJ_ESTABLISH:项目立项 PROJ_REVIEW:项目评审 GROUP_CREDIT_ESTABLISH:授信立项 GROUP_CREDIT_REVIEW：授信评审 PAYMENT：付款申请
      const { bizSource = 'creditList', ...rest } = this.params
      const func = {
        creditList: creditReportApi.postBaseList,
        PROJ_ESTABLISH: creditSearchProjectApi.postProjectList,
        PROJ_REVIEW: creditSearchProjectApi.postProjectList,
        GROUP_CREDIT_ESTABLISH: creditSearchProjectApi.postProjectList,
        GROUP_CREDIT_REVIEW: creditSearchProjectApi.postProjectList,
        PAYMENT: creditSearchProjectApi.postProjectList,
        clientList: creditSearchClientApi.postClientList,
      }
      const res = await func[bizSource]({
        ...rest,
        ...params,
        createFrom: params.selectTimeFrom,
        createTo: params.selectTimeTo,
        projIdDataType: bizSource,
      })
      return res
    },
  })

  handleDelete = async (id) => {
    const { bizSource = 'creditList', ...rest } = this.params
    const func = {
      creditList: creditReportApi.getBaseDelete,
      PROJ_ESTABLISH: creditSearchProjectApi.getProjectDelete,
      PROJ_REVIEW: creditSearchProjectApi.getProjectDelete,
      GROUP_CREDIT_ESTABLISH: creditSearchProjectApi.getProjectDelete,
      GROUP_CREDIT_REVIEW: creditSearchProjectApi.getProjectDelete,
      PAYMENT: creditSearchProjectApi.getProjectDelete,
      clientList: creditSearchClientApi.getClientDelete,
    }
    await func[bizSource]({ id })
    message.success('删除成功')
    this.table.search()
  }

  loading = false
  batchDownload = async (e) => {
    const { rows } = this.table.getSelected()
    if (e.key === '1') {
      const getTableData = async ({ tableStore }) => {
        const params = tableStore.getParams()
        const postParams = {
          ...params,
          pageSize: 9999,
          page: 1,
        }
        const res = await tableStore.request(postParams)
        const columns = tableStore.getOptimizedColumns()
        const dataSource = res.list
        return { dataSource, columns }
      }
      const { dataSource, columns } = await getTableData({ tableStore: this.table })
      const transformDataSource = (dataSource) => {
        const newDataSource = []
        dataSource.forEach(({ clientNameList, cscCodeList, selectGoalList, ...rest }) => {
          clientNameList?.forEach((item, index) => {
            newDataSource.push({
              ...rest,
              ...item,
              clientNameList: [item],
              cscCodeList: [cscCodeList[index]],
              selectGoalList: [selectGoalList[index]],
            })
          })
        })
        return newDataSource
      }
      await downLoadExcel({
        fileName: `征信报告查询列表`,
        dataSource: transformDataSource(dataSource),
        columns: [...columns],
      })
    } else {
      const reportFileIds = rows.map((item) => item.reportFileIds).flat()
      if (reportFileIds.length === 0) {
        message.error('请选择要下载的报告')
        return
      }
      await this.handleDownload({
        id: '1',
        reportFileIds,
      })
    }
  }
  handleDownload = async (record) => {
    await batchDownloadFile({
      mainId: record.id,
      moduleType: MODULE_TYPE,
      materialsTypes: ['CLIENT_CREDIT_REPORT'],
      fileId: record.reportFileIds,
    })
  }
  handlePreview = async (fileIds) => {
    fileIds.forEach((fileId) => {
      window.open(`/preview/pdfPreview/${fileId}`)
    })
  }
  handleSync = async (id) => {
    await creditReportApi.postBaseSync({ id })
    message.success('同步成功')
    this.table.search()
  }
}

export default Store
