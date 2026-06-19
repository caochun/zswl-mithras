import { makeAutoObservable } from '@zswl/admin'
import { PageStore } from '@zswl/components'
import Api from '@/api/preview/reportPreview'
class Store {
  constructor() {
    makeAutoObservable(this)
  }
  page = new PageStore({
    request: async (params) => {
      const res = await Api.postMaterialsPreview(params)
      return res
    },
  })
  onlyOfficeParams
  onlyOffice = async (params) => {
    const res = await Api.postOnlyOfficeParams(params)
    if (res && window.DocsAPI) {
      this.onlyOfficeParams = res
    }
  }
}
export default new Store()
