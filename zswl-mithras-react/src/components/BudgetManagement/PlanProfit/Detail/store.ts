import { makeAutoObservable } from 'mobx'
import { PageStore, TableStore } from '@zswl/components'
import profitMeasurementApi from '@/api/budgetManagement/profitMeasurementApi'
import { formatOtherSummary, formatPutTable, formatSummary } from './utils'
import { message } from 'antd'
import { history } from '@zswl/admin'

export default class Store {
  constructor() {
    makeAutoObservable(this)
  }
  page = new PageStore({
    request: async (params) => {
      const { id } = this.page.getParams()
      const res = await profitMeasurementApi.postProfitInfo({
        ...params,
        id,
      })
      return res
    },
  })
  budgetSummary = {}
  budgetSummaryTable = new TableStore({
    request: async (params) => {
      const { id } = this.page.getParams()
      const { sumData, deptDataList } = await profitMeasurementApi.postProfitSummary({
        ...params,
        budgetPlanProfitId: id,
      })
      this.budgetSummary = sumData
      return formatSummary(deptDataList)
    },
  })
  putColumns = []
  putProgressList = new TableStore({
    pagination: false,
    request: async (params) => {
      const { id } = this.page.getParams()
      const res = await profitMeasurementApi.postProfitProcess({
        ...params,
        budgetPlanProfitId: id,
      })
      const { columns, dataSource } = formatPutTable(res)
      this.putColumns = columns
      return dataSource
    },
  })
  budgetDetailList = new TableStore({
    pagination: false,
    request: async (params) => {
      const { id } = this.page.getParams()
      const res = await profitMeasurementApi.postDetailHistory({
        ...params,
        id,
      })
      return res
    },
  })
  newBusinessList = new TableStore({
    pagination: false,
    request: async (params) => {
      const { id } = this.page.getParams()
      const res = await profitMeasurementApi.postDetailFeature({
        ...params,
        id,
      })
      return res
    },
  })
  sendNotice = async () => {
    const { id } = this.page.getParams()
    await profitMeasurementApi.postDetailNotifyCreatePlanPay({
      id,
    })
    message.success('发送成功')
    this.page.init()
  }
  goPlan = () => {
    const { budgetPlanPayId } = this.page.getData()
    history.push(`/budgetManagement/placementPlan/detail/${budgetPlanPayId}`)
  }
  confirmBudget = async () => {
    const { id } = this.page.getParams()
    await profitMeasurementApi.postProfitConfirm({
      id,
    })
    await this.page.init()
    message.success('确认成功')
  }
  otherColumns = { columns: [], rowSpan: 9 }
  otherBudgetSummaryList = new TableStore({
    pagination: false,
    request: async (params) => {
      const { id } = this.page.getParams()
      const res = await profitMeasurementApi.postProfitSummaryOther({
        ...params,
        budgetPlanProfitId: id,
      })
      const { columns, dataSource } = formatOtherSummary(res)
      this.otherColumns = columns
      return dataSource
    },
  })
  // 列表编辑态索引
  editIndex = -1
  editItem = ({ rowIndex }) => {
    this.editIndex = rowIndex
  }
  cancelEdit = () => {
    this.editIndex = -1
  }
  confirmEdit = async ({ record }) => {
    const { values, list } = await this.newBusinessList.submit()
    const newValues = JSON.parse(JSON.stringify(values))
    const { [record.id]: editData, ...rest } = newValues
    await profitMeasurementApi.postDeptModify({
      id: record.id,
      ...editData,
      ...rest,
    })
    message.success('更新成功')
    this.editIndex = -1
    this.newBusinessList.search()
  }
}
