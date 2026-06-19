import { PageStore, TableStore } from '@zswl/components'
import { makeAutoObservable, getQuery } from '@zswl/admin'
import { getOpinionInfo, getOpinionStatistics } from '@/api/customerView/customerDetailApi'
class Store {
  constructor() {
    makeAutoObservable(this)
  }
  cardData = {}
  warningList = new TableStore({
    request: async (params) => {
      try {
        const { uscc } = getQuery()
        this.cardData = await getOpinionStatistics({
          ...params,
          uscc: uscc,
        })
        return await getOpinionInfo({
          ...params,
          uscc: uscc,
        })
      } catch (error) {
        return []
      }
    },
  })

  async refsh() {}
}
export default new Store()
