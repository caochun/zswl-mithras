import { TableStore, Modal, ModalStore } from '@zswl/components'
import { makeAutoObservable, history } from '@zswl/admin'
import Api from '@/api/financial/creditManage'
import { message } from 'antd'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  sumData = {}
  table = new TableStore({
    request: async (searchData) => {
      const data = await Api.postCreditList(searchData)
      this.sumData = data.sum || {}
      return data.records
    },
  })
  /**
   * 删除
   */
  delete = () => {
    Modal.confirm({
      title: '是否确定刪除授信？',
      onOk: async () => {
        const { keys } = this.table.getSelected()
        await Api.postCreditRemove({ ids: keys })
        this.table.search()
      },
    })
  }

  createModal = new ModalStore({
    onFinish: async (values) => {
      const data = await Api.postCreditAdd(values)
      this.createModal.close()
      history.push(`/financial/credit/detail/${data}?newProject=true`)
      this.table.search()
    },
  })
  invalid = async () => {
    const { keys } = this.table.getSelected()
    await Api.postInvalid({ id: keys[0] })
    this.table.search()
    message.success('操作成功')
  }
}
export default new Store()
