import { makeAutoObservable } from '@zswl/admin'
import { downFile } from '@/utils'
import Api from '@/api/common/materialsApi'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  businessVersion
  projectDataDetail
  getProjectDataDetail = async (id) => {
    if (id) {
      const res = await Api.postEstablishList({
        groupCreditEstablishId: id,
        businessVersion: this.businessVersion,
      })
      if (res) {
        this.projectDataDetail = res
      }
      return res
    }
  }
  download = async (ids) => {
    const res = await Api.postProjDownload({ ids })
    downFile(res)
  }
}
export default new Store()
