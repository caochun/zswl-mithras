import { TableStore, ModalStore, PageStore } from '@zswl/components'
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
      const data = await Api.postContractList(params)
      this.setExpandKeys([])
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
    const res = await Api.postContractList(postParams)
    downLoadExcel({
      fileName: `合同维度导出`,
      dataSource: res.list,
      columns: [...columns],
    })
    // message.success('导出成功')
  }

  detailPage = new PageStore({})

  detailExport = async ({ columns, childColumns }) => {
    const { id: contractId } = this.detailPage.getParams()
    const params = this.detailTable.getParams()
    const postParams = {
      ...params,
      contractId,
      pageSize: 5000,
      page: 1,
    }

    const res = await Api.postContractDetail(postParams)
    const list = []
    res.list.map((item) => {
      item.weightInfoList?.map((childItem) => {
        list.push({ ...item, ...childItem })
      })
    })
    downLoadExcel({
      fileName: `合同维度详情导出`,
      dataSource: list,
      columns: [...columns, ...childColumns],
    })
    // message.success('导出成功')
  }

  detailTable = new TableStore({
    request: async (params) => {
      const { id: contractId } = this.detailPage.getParams()
      const postParams = {
        ...params,
        contractId,
      }
      const data = await Api.postContractDetail(postParams)
      return data
    },
  })

  calculationModal = new ModalStore({
    onFinish: async (values) => {
      await Api.postGuessCalculate(values)
      message.success('计算成功')
      this.calculationModal.close()
      this.table.search()
    },
  })

  // 展开
  expandKeys = []
  setExpandKeys = (keys) => {
    this.expandKeys = keys
  }
}
export default new Store()
