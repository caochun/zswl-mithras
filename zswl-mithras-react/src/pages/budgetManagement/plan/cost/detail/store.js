import costBudgetDetialApi from '@/api/budgetManagement/costBudgetDetialApi'
import { AmountColumn } from '@/components/Format'
import { makeAutoObservable } from '@zswl/admin'
import { TableStore } from '@zswl/components'

const rowData = [
  { title: '存量租金回笼', dataIndex: 'projectRentHistory' },
  { title: '存量本金回笼', dataIndex: 'projectPrincipalHistory' },
  { title: '存量利息回笼', dataIndex: 'projectInterestHistory' },
  { title: '归还存量借款本金', dataIndex: 'financePrincipalHistory' },
  { title: '归还存量借款利息', dataIndex: 'financeInterestHistory' },
  { title: '还贷存量', dataIndex: 'financeRepayHistory' },
  { title: '退回保证金', dataIndex: 'projectDepositHistory' },
  { title: '新增保证金', dataIndex: 'projectDepositFeature' },
  { title: '新增服务咨询费', dataIndex: 'projectConsultingFeeFeature' },
  { title: '投放', dataIndex: 'projectPayFeature' },
  { title: '新增租金回笼', dataIndex: 'projectRentFeature' },
  { title: '新增本金回笼', dataIndex: 'projectPrincipalFeature' },
  { title: '新增利息回笼', dataIndex: 'projectInterestFeature' },
  { title: '资金缺口', dataIndex: 'fundGap' },
]

const INIT_FORMAT = 10000 * 10000
class Store {
  id = null
  constructor({ id }) {
    makeAutoObservable(this)
    this.id = id
  }
  columns = [{ title: '指标', dataIndex: 'title', width: 150, fixed: 'left' }]

  // 生成动态列
  generateColumns = (data) => {
    if (!data?.length) return [{ title: '指标', dataIndex: 'title', width: 150, fixed: 'left' }]

    const dynamicColumns = data.map((item, index) => {
      const month = item.month || index + 1
      return AmountColumn({
        title: `${item.year || '2025'}年${month}月`,
        dataIndex: `month${month}`,
        initFormat: INIT_FORMAT,
        width: 150,
      })
    })
    console.log('dynamicColumns: ', dynamicColumns)

    return [{ title: '指标', dataIndex: 'title', width: 150, fixed: 'left' }, ...dynamicColumns]
  }
  // 转换数据结构
  transformList = (data) => {
    if (!data?.length) return []

    return rowData.map((row) => {
      const result = {
        title: row.title,
      }

      data.forEach((item, index) => {
        const month = item.month || index + 1
        result[`month${month}`] = item[row.dataIndex] || 0
      })

      return result
    })
  }
  list = new TableStore({
    pagination: false,
    request: async (params) => {
      const response = await costBudgetDetialApi.postDetailList({
        budgetPlanCostId: this.id,
        ...params,
      })
      this.columns = this.generateColumns(response)
      return this.transformList(response)
    },
  })
}

export default Store
