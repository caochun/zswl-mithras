import { makeAutoObservable } from '@zswl/admin'
import { DrawerStore, TableStore } from '@zswl/components'
import Api from '@/api/dashboard/pay'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  loading = false
  setLoading = (flag) => {
    this.loading = flag
  }

  xData = []
  setXData = (data) => {
    this.xData = data
  }

  chartsLineData = []
  setChartsLineData = (data) => {
    this.chartsLineData = data
  }

  getChartsData = async () => {
    this.setLoading(true)

    const lineData = await Api.postDashboardPayReceiptRate({})

    const xData = []
    const industryRate = []
    const publicRate = []
    const yearAverage = []

    lineData.monthList?.map((item) => {
      xData.push(item.month)
      industryRate.push({
        value: item.industryPayReceiptRate?.value,
        unit: item.industryPayReceiptRate?.unit,
      })
      publicRate.push({
        value: item.publicPayReceiptRate?.value,
        unit: item.publicPayReceiptRate?.unit,
      })
      yearAverage.push({ value: lineData.average?.value, unit: lineData.average?.unit })
    })

    this.setXData(xData)
    this.setChartsLineData([
      {
        name: '产业收益率',
        data: industryRate,
      },
      {
        name: '公用事业收益率',
        data: publicRate,
      },
      {
        name: '全年平均',
        data: yearAverage,
      },
    ])

    this.setLoading(false)
  }

  sumData = []
  setSumData = (data) => {
    this.sumData = data
  }

  listDrawer = new DrawerStore({})

  listTableStore = new TableStore({
    request: async (params) => {
      const { records, sumData } = await Api.postDashboardPayList({
        ...params,
        queryType: 'YEAR',
        queryDimension: 'RECEIPT',
      })
      this.setSumData(sumData)
      return records
    },
  })
}
export default Store
