import { makeAutoObservable, runInAction } from 'mobx'
import { PageStore, TableStore } from '@zswl/components'
import profitMeasurementApi from '@/api/budgetManagement/profitMeasurementApi'
import { message } from 'antd'

export default class Store {
  constructor() {
    makeAutoObservable(this)
  }
  page = new PageStore({})
  table = new TableStore({
    pagination: false,
    request: async (params) => {
      const { budgetPlanProfitId, belongDeptId } = this.page.getParams()
      const res = await profitMeasurementApi.postDetailDept({
        ...params,
        belongDeptId,
        budgetPlanProfitId,
      })
      return res
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
    const { values, list } = await this.table.submit()
    const newValues = JSON.parse(JSON.stringify(values))
    const { [record.id]: editData, ...rest } = newValues
    await profitMeasurementApi.postDeptModify({
      id: record.id,
      ...editData,
      ...rest,
    })
    message.success('更新成功')
    this.editIndex = -1
    this.table.search()
  }
}
