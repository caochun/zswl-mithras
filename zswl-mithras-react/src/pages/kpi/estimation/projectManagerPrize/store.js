import { TableStore, PageStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import Api from '@/api/kpi/estimation/guess'
import { message } from 'antd'
import { downLoadExcel } from '@/components/Excel'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  table = new TableStore({
    request: async (params) => {
      const data = await Api.postManagerList(params)
      return data
    },
  })
  export = async ({ columns }) => {
    const params = this.table.getParams()
    const postParams = {
      ...params,
      pageSize: 5000,
      page: 1,
    }
    const res = await Api.postManagerList(postParams)
    downLoadExcel({
      fileName: `项目经理奖金列表`,
      dataSource: res,
      columns: [...columns],
    })
    // message.success('导出成功')
  }

  detailPage = new PageStore({})

  detailExport = async ({ columns }) => {
    const params = this.detailTable.getParams()
    const { id } = this.detailPage.getParams()
    const [calculateDateYear, calculateDateMonth] = id?.split('-') || []
    const postParams = {
      ...params,
      calculateDateYear,
      calculateDateMonth,
      pageSize: 5000,
      page: 1,
    }
    const res = await Api.postManagerDetail(postParams)
    downLoadExcel({
      fileName: `项目经理奖金详情导出(${id})`,
      dataSource: res,
      columns: [...columns],
    })
    // message.success('导出成功')
  }

  detailTable = new TableStore({
    request: async (params) => {
      const { id } = this.detailPage.getParams()
      const [calculateDateYear, calculateDateMonth] = id?.split('-') || []
      const data = await Api.postManagerDetail({ ...params, calculateDateYear, calculateDateMonth })
      return data
    },
  })
}
export default new Store()
