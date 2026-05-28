import { makeAutoObservable } from '@zswl/admin'
import { DrawerStore, TableStore } from '@zswl/components'
import Api from '@/api/manageReport/operation'
import { barSeriesItem, lineSeriesItem } from '@/pages/report/Operation/utils'
import { downLoadExcel } from '@/components/Excel'
import { hasValue } from '@/utils'

const amountFormat = (value, defaultValue = 0) => {
  return hasValue(value) ? value / (10000 * 10000) : defaultValue
}

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  sumData = {}
  setSumData = (data) => {
    this.sumData = data
  }

  table = new TableStore({
    pagination: false,
    request: async (params) => {
      const result = await Api.postYewuyunyingfenxiStatistic({
        ...params,
        groupType: 'DEPT',
      })
      this.getChartsData()
      return result
    },
  })

  export = async ({ columns }) => {
    const params = this.table.getParams()
    const postParams = {
      ...params,
      pageSize: 5000,
      page: 1,
      groupType: 'DEPT',
    }
    const res = await Api.postYewuyunyingfenxiStatistic(postParams)
    downLoadExcel({
      fileName: `业务运行分析报表`,
      dataSource: res,
      columns: [...columns],
    })
  }

  detailExport = async ({ columns }) => {
    const params = this.listDrawerTable.getParams()
    const postParams = {
      ...params,
      pageSize: 5000,
      page: 1,
    }
    const res = await Api.postYewuyunyingfenxiDetail({
      ...postParams,
    })
    downLoadExcel({
      fileName: `业务运行分析报表-明细表`,
      dataSource: res?.list,
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
      return await Api.postYewuyunyingfenxiDetail({
        ...params,
      })
    },
  })

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

  getChartsData = async () => {
    this.setLoading(true)
    this.setChartsData([])

    const params = this.table.getParams()
    const res = await Api.postYewuyunyingfenxiStatistic({
      ...params,
      groupType: 'MONTH',
    })

    const xData = res.map((item) => item.yearAndMonth)
    this.setXData(xData)

    const result = [
      barSeriesItem({
        name: '立项创建-金额',
        data: res.map((item) => {
          return {
            value: amountFormat(item.projEstablishCreateAmount),
            unit: '万元',
          }
        }),
      }),
      barSeriesItem({
        name: '评审创建-金额',
        data: res.map((item) => {
          return {
            value: amountFormat(item.projReviewCreateAmount),
            unit: '万元',
          }
        }),
      }),
      barSeriesItem({
        name: '租赁物创建-金额',
        data: res.map((item) => {
          return {
            value: amountFormat(item.leaseItemCreateAmount),
            unit: '万元',
          }
        }),
      }),
      barSeriesItem({
        name: '合同创建-金额',
        data: res.map((item) => {
          return {
            value: amountFormat(item.contractCreateAmount),
            unit: '万元',
          }
        }),
      }),
      barSeriesItem({
        name: '合同付款-金额',
        data: res.map((item) => {
          return {
            value: amountFormat(item.paymentCreateAmount),
            unit: '万元',
          }
        }),
      }),
      barSeriesItem({
        name: '合同投放-金额',
        data: res.map((item) => {
          return {
            value: amountFormat(item.paymentActualPayAmount),
            unit: '万元',
          }
        }),
      }),

      lineSeriesItem({
        name: '立项创建-数量',
        data: res.map((item) => {
          return {
            value: item.projEstablishCreateQuantity,
            unit: '个',
          }
        }),
      }),
      lineSeriesItem({
        name: '评审创建-数量',
        data: res.map((item) => {
          return {
            value: item.projReviewCreateQuantity,
            unit: '个',
          }
        }),
      }),
      lineSeriesItem({
        name: '租赁物创建-数量',
        data: res.map((item) => {
          return {
            value: item.leaseItemCreateQuantity,
            unit: '个',
          }
        }),
      }),
      lineSeriesItem({
        name: '合同创建-数量',
        data: res.map((item) => {
          return {
            value: item.contractCreateQuantity,
            unit: '个',
          }
        }),
      }),
      lineSeriesItem({
        name: '合同付款-数量',
        data: res.map((item) => {
          return {
            value: item.paymentCreateQuantity,
            unit: '个',
          }
        }),
      }),
      lineSeriesItem({
        name: '合同投放-数量',
        data: res.map((item) => {
          return {
            value: item.paymentActualPayQuantity,
            unit: '个',
          }
        }),
      }),
    ]
    this.setChartsData(result)
    this.setLoading(false)
  }
}
export default Store
