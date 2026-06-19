import { makeAutoObservable } from '@zswl/admin'
import { TableStore, Modal } from '@zswl/components'
import { message } from 'antd'
import Api from '@/api/process/flowModelApi'
class Store {
  constructor() {
    makeAutoObservable(this, { clientId: false })
  }

  table = new TableStore({
    request: async (parameter) => {
      return await Api.getModelList({ ...parameter })
    },
  })
  del = async (modelId) => {
    await Api.del({ modelId })
    message.success('删除成功！')
    this.table.search()
  }
  publish = async (modelId) => {
    await Api.publish({ modelId })
    message.success('发布成功！')
    this.table.search()
  }
}
//export default new Store()
export default Store
