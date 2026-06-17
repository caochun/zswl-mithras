import { makeAutoObservable } from '@zswl/admin'
import { DrawerStore, TableStore } from '@zswl/components'
import Api from './api'
import {
  initType,
  initQueryDate,
  formatQueryDate,
  sameYearQueryDate,
  getOperationStatisticStage,
} from '@/utils/dashboardOperation'

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
      const { records, sumData, averageData } = await Api.postDashboardOperationTimeList({
        ...params,
      })
      this.setSumData(sumData)
      this.setAverageData(averageData)
      return records
    },
  })

  onValuesChange = (changedValues, allValues) => {
    const { queryDate } = allValues
    if (sameYearQueryDate(queryDate)) {
      this.setQueryDate(queryDate)
      this.getChartsData()
    }
  }
  chartsBarData = []
  setChartsBarData = (data) => {
    this.chartsBarData = data
  }
  chartsLineData = []
  setChartsLineData = (data) => {
    this.chartsLineData = data
  }
  getChartsData = async () => {
    this.setLoading(true)
    this.setChartsBarData([])
    this.setChartsLineData([])

    const params = {
      type: this.activityKey,
      ...formatQueryDate({ dateRange: this.queryDate }),
    }
    const departStageData = await Api.postDashboardOperationTimeStatistics(params)
    const termStageData = await Api.postDashboardOperationTimeTerm(params)

    // 各部门、各阶段的数据
    const depart_projEstaTotalTime = []
    const depart_dueDiligenceTotalTime = []
    const depart_reviewTotalTime = []
    const depart_summaryTotalTime = []
    const depart_projEstaPaidInTotalTime = []
    const depart_reviewPaidInTotalTime = []

    departStageData.map((item) => {
      depart_projEstaTotalTime.push({
        name: item.bizDeptName,
        value: item.projEstaTotalTime?.value,
        unit: item.projEstaTotalTime?.unit,
      })
      depart_dueDiligenceTotalTime.push({
        name: item.bizDeptName,
        value: item.dueDiligenceTotalTime?.value,
        unit: item.dueDiligenceTotalTime?.unit,
      })
      depart_reviewTotalTime.push({
        name: item.bizDeptName,
        value: item.reviewTotalTime?.value,
        unit: item.reviewTotalTime?.unit,
      })
      depart_summaryTotalTime.push({
        name: item.bizDeptName,
        value: item.summaryTotalTime?.value,
        unit: item.summaryTotalTime?.unit,
      })
      depart_projEstaPaidInTotalTime.push({
        name: item.bizDeptName,
        value: item.projEstaPaidInTotalTime?.value,
        unit: item.projEstaPaidInTotalTime?.unit,
      })
      depart_reviewPaidInTotalTime.push({
        name: item.bizDeptName,
        value: item.reviewPaidInTotalTime?.value,
        unit: item.reviewPaidInTotalTime?.unit,
      })
    })

    const depart_stageTimeArr = [
      depart_projEstaTotalTime,
      depart_dueDiligenceTotalTime,
      depart_reviewTotalTime,
      depart_summaryTotalTime,
      depart_projEstaPaidInTotalTime,
      depart_reviewPaidInTotalTime,
    ]

    const depart_stageTimeData = depart_projEstaTotalTime.map((v, i) => {
      return {
        name: v?.name,
        data: depart_stageTimeArr.map((item) => item[i]?.value ?? '-'),
      }
    })

    // 各阶段的数据
    const stage_projEstaTotalTime = []
    const stage_dueDiligenceTotalTime = []
    const stage_reviewTotalTime = []
    const stage_summaryTotalTime = []
    const stage_projEstaPaidInTotalTime = []
    const stage_reviewPaidInTotalTime = []

    termStageData.map((item) => {
      stage_projEstaTotalTime.push({
        name: getOperationStatisticStage(item.termName),
        value: item.projEstaTotalTime?.value,
        unit: item.projEstaTotalTime?.unit,
      })
      stage_dueDiligenceTotalTime.push({
        name: getOperationStatisticStage(item.termName),
        value: item.dueDiligenceTotalTime?.value,
        unit: item.dueDiligenceTotalTime?.unit,
      })
      stage_reviewTotalTime.push({
        name: getOperationStatisticStage(item.termName),
        value: item.reviewTotalTime?.value,
        unit: item.reviewTotalTime?.unit,
      })
      stage_summaryTotalTime.push({
        name: getOperationStatisticStage(item.termName),
        value: item.summaryTotalTime?.value,
        unit: item.summaryTotalTime?.unit,
      })
      stage_projEstaPaidInTotalTime.push({
        name: getOperationStatisticStage(item.termName),
        value: item.projEstaPaidInTotalTime?.value,
        unit: item.projEstaPaidInTotalTime?.unit,
      })
      stage_reviewPaidInTotalTime.push({
        name: getOperationStatisticStage(item.termName),
        value: item.reviewPaidInTotalTime?.value,
        unit: item.reviewPaidInTotalTime?.unit,
      })
    })

    const stageTimeArr = [
      stage_projEstaTotalTime,
      stage_dueDiligenceTotalTime,
      stage_reviewTotalTime,
      stage_summaryTotalTime,
      stage_projEstaPaidInTotalTime,
      stage_reviewPaidInTotalTime,
    ]

    const stageTimeData = stage_projEstaTotalTime.map((v, i) => {
      return {
        name: v?.name,
        data: stageTimeArr.map((item) => item[i]?.value ?? '-'),
      }
    })

    this.setChartsBarData(depart_stageTimeData)
    this.setChartsLineData(stageTimeData)
    this.setLoading(false)
  }
}
export default Store
