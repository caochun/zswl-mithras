import { makeAutoObservable } from '@zswl/admin'
import { DrawerStore, TableStore } from '@zswl/components'
import Api from '@/api/manageReport/operation'
import { downLoadExcel } from '@/components/Excel'
import { hasValue } from '@/utils'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  table = new TableStore({
    request: async (params) => {
      return await Api.postYunyingdaibanStatistic({
        ...params,
      })
    },
  })

  export = async ({ columns }) => {
    const params = this.table.getParams()
    const postParams = {
      ...params,
      pageSize: 5000,
      page: 1,
    }
    const res = await Api.postYunyingdaibanStatistic(postParams)
    downLoadExcel({
      fileName: `运营待办管理报表`,
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
    const res = await Api.postYunyingdaibanDetail({
      ...postParams,
    })
    downLoadExcel({
      fileName: `运营待办管理报表-明细表`,
      dataSource: res,
      columns: [...columns],
    })
  }

  listDrawer = new DrawerStore({
    onOpen: (idList) => {
      this.listDrawerTable.setParams({
        ...this.table.getParams(),
        processInstanceIdList: idList,
      })
    },
  })

  listDrawerTable = new TableStore({
    request: async (params) => {
      return await Api.postYunyingdaibanDetail({
        ...params,
      })
    },
  })
}
export default Store
