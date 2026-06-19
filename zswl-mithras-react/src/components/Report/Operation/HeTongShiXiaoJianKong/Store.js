import { makeAutoObservable } from '@zswl/admin'
import { DrawerStore, TableStore } from '@zswl/components'
import Api from '@/api/report/operationView/contractTimeMonitorApi'
import { barSeriesItem, lineSeriesItem } from '@/utils/domains/report/ReportUtils'
import { downLoadExcel } from '@/components/Excel'
import { hasValue } from '@/utils'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  table = new TableStore({
    pagination: false,
    request: async (params) => {
      const {
        processStartDateFrom: queryDateFrom,
        processStartDateTo: queryDateTo,
        ...rest
      } = params
      const result = await Api.statisticList({
        queryDateFrom,
        queryDateTo,
        ...rest,
      })
      this.getChartsData(result)
      return result
    },
  })

  export = async ({ columns }) => {
    const params = this.table.getParams()
    const { processStartDateFrom: queryDateFrom, processStartDateTo: queryDateTo, ...rest } = params
    const postParams = {
      queryDateFrom,
      queryDateTo,
      ...rest,
      pageSize: 5000,
      page: 1,
    }
    const res = await Api.statisticList(postParams)
    downLoadExcel({
      fileName: `合同时效监控报表`,
      dataSource: res,
      columns: [...columns],
    })
  }

  listDrawer = new DrawerStore({
    onOpen: (record) => {
      const { processStartDateFrom, processStartDateTo } = this.table.getParams()
      this.listDrawerTable.setParams({
        ...this.table.getParams(),
        processStartDate: [moment(processStartDateFrom), moment(processStartDateTo)],
        bizDeptId: record.bizDeptId,
      })
    },
  })

  listDrawerTable = new TableStore({
    request: async (params) => {
      const {
        processStartDateFrom: queryDateFrom,
        processStartDateTo: queryDateTo,
        ...rest
      } = params
      return await Api.detail({
        queryDateFrom,
        queryDateTo,
        ...rest,
      })
    },
  })
  detailExport = async ({ columns }) => {
    const params = this.listDrawerTable.getParams()
    const { processStartDateFrom: queryDateFrom, processStartDateTo: queryDateTo, ...rest } = params
    const postParams = {
      queryDateFrom,
      queryDateTo,
      ...rest,
      pageSize: 5000,
      page: 1,
    }
    const res = await Api.detail({
      ...postParams,
    })
    downLoadExcel({
      fileName: `合同时效监控报表-明细表`,
      dataSource: res,
      columns: [...columns],
    })
  }

  loading = false
  setLoading = (flag) => {
    this.loading = flag
  }

  chartsData = []
  setChartsData = (data) => {
    this.chartsData = data
  }

  xData = []
  setXData = (data) => {
    this.xData = data
  }

  getChartsData = async (res) => {
    this.setLoading(true)
    this.setChartsData([])

    const xData = res.map((item) => item.bizDeptName)
    this.setXData(xData)

    const result = [
      lineSeriesItem({
        name: '全流程平均时效(工作日)',
        yAxisIndex: 0,
        data: res.map((item) => {
          return {
            value: item.averageDuration,
          }
        }),
      }),
      lineSeriesItem({
        name: '运营经办平均时效(工作日)',
        yAxisIndex: 0,
        data: res.map((item) => {
          return {
            value: item.averageDurationYYJB,
          }
        }),
      }),
      lineSeriesItem({
        name: '运营部平均时效(工作日)',
        yAxisIndex: 0,
        data: res.map((item) => {
          return {
            value: item.averageDurationYYB,
          }
        }),
      }),
      lineSeriesItem({
        name: '法务部平均时效(工作日)',
        yAxisIndex: 0,
        data: res.map((item) => {
          return {
            value: item.averageDurationFWB,
          }
        }),
      }),
      lineSeriesItem({
        name: '财务部平均时效(工作日)',
        yAxisIndex: 0,
        data: res.map((item) => {
          return {
            value: item.averageDurationCWB,
          }
        }),
      }),
    ]
    this.setChartsData(result)
    this.setLoading(false)
  }
}
export default Store
