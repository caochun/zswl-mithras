import { ModalStore, PageStore, TableStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import Api from '@/api/lease/trackingApi'
import { message } from 'antd'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  page = new PageStore({})
  table = new TableStore({
    request: (params) => {
      const { createBy, ...defaultParams } = this.page.getParams()
      return Api.postTrackEventList({ ...defaultParams, ...params })
    },
  })
  close = async () => {
    const { keys } = this.table.getSelected()
    await Api.getTrackEventClose({ id: keys[0] })
    message.success('成功关闭')
    this.table.search()
  }
  export = async () => {
    const { keys } = this.table.getSelected()
    const { page, pageSize, ...params } = this.table.getParams()
    const newParams = !keys.length ? params : { ids: keys }
    await Api.postTrackEventDownload(newParams)
    message.success('导出成功')
  }
}
export default Store
