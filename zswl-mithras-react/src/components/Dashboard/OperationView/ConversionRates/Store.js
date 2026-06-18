import { makeAutoObservable } from '@zswl/admin'
import { DrawerStore, TableStore } from '@zswl/components'
import Api from './api'
import {
  initType,
  initQueryDate,
  formatQueryDate,
  sameYearQueryDate,
  getOperationStatisticStage,
} from '@/dashboard/DashboardUtilsOperation'

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
    const depart_visitProjEstaPer = []
    const depart_visitDeliveryPer = []
    const depart_projEstaDueDiliPer = []
    const depart_dueDiligenceReviewPer = []
    const depart_dueDiliDeliveryPer = []
    const depart_reviewDeliveryPer = []

    departStageData.map((item) => {
      depart_visitProjEstaPer.push({
        name: item.bizDeptName,
        value: item.visitProjEstaPer?.value,
        unit: item.visitProjEstaPer?.unit,
      })
      depart_visitDeliveryPer.push({
        name: item.bizDeptName,
        value: item.visitDeliveryPer?.value,
        unit: item.visitDeliveryPer?.unit,
      })
      depart_projEstaDueDiliPer.push({
        name: item.bizDeptName,
        value: item.projEstaDueDiliPer?.value,
        unit: item.projEstaDueDiliPer?.unit,
      })
      depart_dueDiligenceReviewPer.push({
        name: item.bizDeptName,
        value: item.dueDiligenceReviewPer?.value,
        unit: item.dueDiligenceReviewPer?.unit,
      })
      depart_dueDiliDeliveryPer.push({
        name: item.bizDeptName,
        value: item.dueDiliDeliveryPer?.value,
        unit: item.dueDiliDeliveryPer?.unit,
      })
      depart_reviewDeliveryPer.push({
        name: item.bizDeptName,
        value: item.reviewDeliveryPer?.value,
        unit: item.reviewDeliveryPer?.unit,
      })
    })

    const depart_stageTimeArr = [
      depart_visitProjEstaPer,
      depart_visitDeliveryPer,
      depart_projEstaDueDiliPer,
      depart_dueDiligenceReviewPer,
      depart_dueDiliDeliveryPer,
      depart_reviewDeliveryPer,
    ]

    const depart_stageTimeData = depart_visitProjEstaPer.map((v, i) => {
      return {
        name: v?.name,
        data: depart_stageTimeArr.map((item) => item[i]?.value ?? '-'),
      }
    })

    // 各阶段的数据
    const stage_visitProjEstaPer = []
    const stage_visitDeliveryPer = []
    const stage_projEstaDueDiliPer = []
    const stage_dueDiligenceReviewPer = []
    const stage_dueDiliDeliveryPer = []
    const stage_reviewDeliveryPer = []

    termStageData.map((item) => {
      stage_visitProjEstaPer.push({
        name: getOperationStatisticStage(item.termName),
        value: item.visitProjEstaPer?.value,
        unit: item.visitProjEstaPer?.unit,
      })
      stage_visitDeliveryPer.push({
        name: getOperationStatisticStage(item.termName),
        value: item.visitDeliveryPer?.value,
        unit: item.visitDeliveryPer?.unit,
      })
      stage_projEstaDueDiliPer.push({
        name: getOperationStatisticStage(item.termName),
        value: item.projEstaDueDiliPer?.value,
        unit: item.projEstaDueDiliPer?.unit,
      })
      stage_dueDiligenceReviewPer.push({
        name: getOperationStatisticStage(item.termName),
        value: item.dueDiligenceReviewPer?.value,
        unit: item.dueDiligenceReviewPer?.unit,
      })
      stage_dueDiliDeliveryPer.push({
        name: getOperationStatisticStage(item.termName),
        value: item.dueDiliDeliveryPer?.value,
        unit: item.dueDiliDeliveryPer?.unit,
      })
      stage_reviewDeliveryPer.push({
        name: getOperationStatisticStage(item.termName),
        value: item.reviewDeliveryPer?.value,
        unit: item.reviewDeliveryPer?.unit,
      })
    })

    const stageTimeArr = [
      stage_visitProjEstaPer,
      stage_visitDeliveryPer,
      stage_projEstaDueDiliPer,
      stage_dueDiligenceReviewPer,
      stage_dueDiliDeliveryPer,
      stage_reviewDeliveryPer,
    ]

    const stageTimeData = stage_visitProjEstaPer.map((v, i) => {
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
