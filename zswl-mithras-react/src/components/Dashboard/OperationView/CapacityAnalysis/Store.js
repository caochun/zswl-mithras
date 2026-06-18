import { makeAutoObservable } from '@zswl/admin'
import { DrawerStore, TableStore } from '@zswl/components'
import {
  initType,
  initProjStage,
  initQueryDate,
  formatQueryDate,
  sameYearQueryDate,
  lineSeriesItem,
} from '@/utils/domains/dashboard/DashboardUtilsOperation'
import Api from './api'

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

  curProjStage = initProjStage
  setCurProjStage = (stage) => {
    this.curProjStage = stage
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

  onValuesChange = (changedValues, allValues) => {
    const { queryDate, projStage } = allValues
    if (sameYearQueryDate(queryDate)) {
      this.setCurProjStage(projStage)
      this.setQueryDate(queryDate)
      this.getChartsData()
    }
  }

  departChartsData = []
  setDepartChartsData = (data) => {
    this.departChartsData = data
  }

  personChartsData = []
  setPersonChartsData = (data) => {
    this.personChartsData = data
  }

  getChartsData = async () => {
    this.setLoading(true)
    this.setDepartChartsData([])
    const res = await Api.postDashboardOperationPayStatistics({
      type: this.activityKey,
      projStage: this.curProjStage,
      ...formatQueryDate({ dateRange: this.queryDate }),
    })

    const transformResult = (key) => {
      if (!res) return []
      return res.map((item) => {
        return {
          name: item.bizDeptName,
          value: item[key]?.value ?? item[key],
          unit: item[key]?.unit,
        }
      })
    }
    const departResult = [
      {
        name: '部门总项目数',
        yAxisIndex: 0,
        data: transformResult('projSum'),
        type: 'bar',
        barWidth: 20,
        barGap: '40%',
        // label: { show: true, position: 'top' },
      },
      {
        name: '去年同期(部门总项目数)',
        yAxisIndex: 0,
        data: transformResult('lastProjSum'),
        type: 'bar',
        barWidth: 20,
        barGap: '40%',
        // label: { show: true, position: 'top' },
      },
      lineSeriesItem({
        name: '部门总金额',
        data: transformResult('amountSum'),
      }),
      lineSeriesItem({
        name: '去年同期(部门总金额)',
        data: transformResult('lastAmountSum'),
        lineStyle: {
          type: 'dashed',
        },
      }),
    ]
    const personResult = [
      {
        name: '人均项目数',
        yAxisIndex: 0,
        data: transformResult('personAverageProjSum'),
        type: 'bar',
        barWidth: 20,
        barGap: '40%',
        // label: { show: true, position: 'top' },
      },
      {
        name: '去年同期(人均项目数)',
        yAxisIndex: 0,
        data: transformResult('lastPersonAverageProjSum'),
        type: 'bar',
        barWidth: 20,
        barGap: '40%',
        // label: { show: true, position: 'top' },
      },
      lineSeriesItem({
        name: '人均金额',
        data: transformResult('personAverageAmountSum'),
      }),
      lineSeriesItem({
        name: '去年同期(人均金额)',
        data: transformResult('lastPersonAverageAmountSum'),
        lineStyle: {
          type: 'dashed',
        },
      }),
      lineSeriesItem({
        name: '件均金额',
        data: transformResult('pieceAverageAmountSum'),
      }),
      lineSeriesItem({
        name: '去年同期(件均金额)',
        data: transformResult('lastPieceAverageAmountSum'),
        lineStyle: {
          type: 'dashed',
        },
      }),
    ]
    this.setDepartChartsData(departResult)
    this.setPersonChartsData(personResult)
    this.setLoading(false)
  }
}
export default Store
