import { PageStore, TableStore } from '@zswl/components'
import { makeAutoObservable, http, getQuery } from '@zswl/admin'
class Store {
  constructor() {
    makeAutoObservable(this)
  }
  cardData = {}
  warningList = new TableStore({
    request: async (params) => {
      try {
        const { uscc } = getQuery()
        this.cardData = await http.post('/customer/view/detail/opiStatistc', {
          ...params,
          uscc: uscc,
        })
        return await http.post('/customer/view/detail/opiInfo', {
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
