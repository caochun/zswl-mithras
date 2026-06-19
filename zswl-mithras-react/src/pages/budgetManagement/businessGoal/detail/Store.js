import { makeAutoObservable } from '@zswl/admin'
import { PageStore, TableStore } from '@zswl/components'
import DataUpload from '@/components/DataUpload'
import Api from '@/api/kpi/performanceManage'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
    page = new PageStore({
    request: async (params) => {
      const { id } = params
      return await Api.postKpiPerformanceManageMainDetail({ id })
    },
  })

  saveData = async (values) => {
    await Api.postKpiPerformanceManageModify({
      id: this.page.getParams().id,
      status: values.status,
    })
    this.page.init()
  }

  handleExport = async () => {
    await Api.postKpiPerformanceManageExport({
      year: this.page.getData().year,
    })
  }
  handleImport = async (file) => {
    const { fileList } = DataUpload.classify(file)
    await Api.postKpiPerformanceManageImportant({
      file: fileList[0],
      mainId: this.page.getData().id,
    })
    this.getManageList()
  }

  mangeList = {}
  getManageList = async () => {
    const res = await Api.postKpiPerformanceManageList({
      id: this.page.getParams().id,
    })
    this.mangeList = res
    this.companyGoalTable.search()
    this.departGoalTable.search()
    this.personGoalTable.search()
  }

  companyGoalTable = new TableStore({
    request: async (params) => {
      return this.mangeList.companyListVOList ?? []
    },
  })
  departGoalTable = new TableStore({
    request: async (params) => {
      return this.mangeList.deptListVOList ?? []
    },
  })
  personGoalTable = new TableStore({
    request: async (params) => {
      return this.mangeList.personalListVOList ?? []
    },
  })
}
export default Store
