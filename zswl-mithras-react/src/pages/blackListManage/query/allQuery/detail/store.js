import { PageStore, TableStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import listLibraryApi from '@/api/blackList/listLibraryApi'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  page = new PageStore({
    request: async ({ id, ...rest }) => {
      const { list } = await listLibraryApi.postInfoList({ unifiedSocialCreditCode: id, ...rest })
      setTimeout(() => {
        this.table.setList(list)
      }, 10)
      return list?.[0] ?? {}
    },
  })
  table = new TableStore()
}
export default new Store()
