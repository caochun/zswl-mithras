import { makeAutoObservable } from '@zswl/admin'
import { DrawerStore, TableStore } from '@zswl/components'
import Api from './api'
import { initType } from '@/utils/dashboardOperation'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  loading = false
  setLoading = (flag) => {
    this.loading = flag
  }

  activityKey = initType
  setActivityKey = (tab) => {
    this.activityKey = tab
  }

  // 部门数据
  chartsBarData = []
  setChartsBarData = (data) => {
    this.chartsBarData = data
  }

  chartsLineData = []
  setChartsLineData = (data) => {
    this.chartsLineData = data
  }

  xData = []
  setXData = (data) => {
    this.xData = data
  }

  transformData = (result) => {
    const res = []
    result.forEach((stage) => {
      stage.monthCollectStatisticsDetailList.forEach((dept) => {
        const existingDept = res.find((item) => item.name === dept.deptName)
        if (existingDept) {
          existingDept.data.push({
            value: dept.incomeAmount?.value,
            unit: dept.incomeAmount?.unit,
          })
        } else {
          res.push({
            name: dept.deptName,
            data: [
              {
                value: dept.incomeAmount?.value,
                unit: dept.incomeAmount?.unit,
              },
            ],
          })
        }
      })
    })
    return res
  }

  getChartsData = async () => {
    this.setLoading(true)
    this.setChartsBarData([])
    this.setChartsLineData([])

    const params = {
      type: this.activityKey,
    }

    const res = await Api.postDashboardMonthcollectStatistics(params)

    const departStageData = this.transformData(res)

    const xData = []
    const incomeTarget = []
    const incomeCompletionRate = []
    res.map((item) => {
      xData.push(item.incomeMonth)
      incomeCompletionRate.push({
        value: item.incomeCompletionRate?.value,
        unit: item.incomeCompletionRate?.unit,
      })
      incomeTarget.push({ value: item.incomeTarget?.value, unit: item.incomeTarget?.unit })
    })
    this.setXData(xData)

    this.setChartsLineData([
      {
        name: '累计收入完成率',
        data: incomeCompletionRate,
      },
      {
        name: '收入目标',
        data: incomeTarget,
      },
    ])
    this.setChartsBarData(departStageData)
    this.setLoading(false)
  }

  listDrawer = new DrawerStore({
    onOpen: () => {
      this.listDrawerTable.setParams({
        queryDate: this.queryDate,
      })
    },
  })

  listDrawerTable = new TableStore({
    request: async (params) => {
      return await Api.postList({
        ...params,
      })
    },
  })

  drawerDownloadExcel = async () => {
    const params = this.listDrawerTable.getParams()
    await Api.postDownloadList({
      ...params,
      pageSize: 5000,
    })
  }
}
export default Store
