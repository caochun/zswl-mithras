import { TableStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import Api from '@/api/cpm/contractCpmApi'
import DetailStore from '../detail/store'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  table = new TableStore({
    request: ({ createDate, industryType, ...rest }) => {
      return []
    },
  })

  contractList = async (projCode) => {
    await Api.contractList({ projCode })
  }
  cashFlowTable = new TableStore({
    request: async ({ cashtype, ...rest }) => {
      const res = await Api.contractDetailList({
        cashtype,
        ...rest,
        contractId: rest.contractId || DetailStore.getContractD(),
      })
      return res
    },
  })
  cashDetailList = async (exportRentCodeList) => {
    await Api.cashDetailList({ exportRentCodeList })
  }
}
export default new Store()
