import { makeAutoObservable } from '@zswl/admin'
import { DrawerStore, TableStore } from '@zswl/components'
import {
  initType,
  initQueryDate,
  initYearQueryDate,
  formatQueryDate,
  sameYearQueryDate,
  lineSeriesItem,
} from '@/utils/domains/dashboard/DashboardUtilsOperation'
import Api from '@/api/dashboard/operationView/launchCompleteApi'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  listDrawer = new DrawerStore({
    onOpen: () => {
      this.listDrawerTable.setParams({
        queryDate: this.queryDate,
      })
    },
  })

  loading = false
  setLoading = (flag) => {
    this.loading = flag
  }

  activityKey = initType
  setActivityKey = (tab) => {
    this.activityKey = tab
  }
  queryDate = initQueryDate
  setQueryDate = (date) => {
    this.queryDate = date
  }

  onValuesChange = (changedValues, allValues) => {
    const { queryDate } = allValues
    if (sameYearQueryDate(queryDate)) {
      this.setQueryDate(queryDate)
      this.getChartsData()
    }
  }

  sumData = {}
  setSumData = (data) => {
    this.sumData = data
  }

  averageData = {}
  setAverageData = (data) => {
    this.averageData = data
  }

  listDrawerTable = new TableStore({
    pagination: false,
    request: async (params) => {
      const { records, sumData, averageData } = await Api.postDashboardOperationPayList({
        ...params,
      })
      this.setSumData(sumData)
      this.setAverageData(averageData)
      return records
    },
  })

  chartsData = []
  setChartsData = (data) => {
    this.chartsData = data
  }
  getChartsData = async () => {
    this.setLoading(true)
    this.setChartsData([])
    const res = await Api.postDashboardOperationPayStatistics({
      type: this.activityKey,
      ...formatQueryDate({ dateRange: this.queryDate }),
    })

    const transformResult = (key) => {
      return res.map((item) => {
        return {
          name: item.bizDeptName,
          value: item[key]?.value,
          unit: item[key]?.unit,
        }
      })
    }
    const result = [
      {
        name: '预算投放',
        yAxisIndex: 0,
        data: transformResult('payPlanAmount'),
        type: 'bar',
        barWidth: 20,
        barGap: '40%',
        label: { show: true, position: 'top' },
      },
      {
        name: '实际投放',
        yAxisIndex: 0,
        data: transformResult('payAmount'),
        type: 'bar',
        barWidth: 20,
        barGap: '40%',
        label: { show: true, position: 'top' },
      },
      lineSeriesItem({
        name: '当期投放完成率',
        data: transformResult('currentPayFinishRate'),
      }),
      lineSeriesItem({
        name: '本年投放完成率',
        data: transformResult('yearPayFinishRate'),
      }),
    ]
    this.setChartsData(result)
    this.setLoading(false)
  }
}
export default Store
