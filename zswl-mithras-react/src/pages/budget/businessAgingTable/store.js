import { ModalStore, PageStore, TableStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import businessAgingApi from '@/api/budget/aging/businessAgingApi'
import { message } from 'antd'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  page = new PageStore({
    request: (params) => {},
  })
  table = new TableStore({
    request: (params) => {
      return businessAgingApi.postInfoList(params)
    },
  })
  createModal = new ModalStore({
    onFinish: async (values) => {
      await businessAgingApi.postInfoAdd(values)
      message.success('添加成功')
      this.createModal.close()
      this.table.search()
    },
  })
  add = async () => {
    this.createModal.open()
  }
  delete = async () => {
    const { keys } = this.table.getSelected()
    await businessAgingApi.postInfoClose({ id: keys[0] })
    message.success('关闭成功')
    this.table.search()
  }
  complete = async () => {
    const { keys } = this.table.getSelected()
    await businessAgingApi.postInfoEffect({ id: keys[0] })
    message.success('更改状态成功')
    this.table.search()
  }
}
export default new Store()
