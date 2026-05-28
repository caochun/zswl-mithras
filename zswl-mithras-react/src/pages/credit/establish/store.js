import { TableStore, Modal, ModalStore, App } from '@zswl/components'
import { makeAutoObservable, history } from '@zswl/admin'
import Api from '@/api/groupCredit/projectApprovalBaseinfo'

const { getData } = App
class Store {
  constructor() {
    makeAutoObservable(this)
  }
  table = new TableStore({
    request: (searchData) => {
      return Api.postInfoList(searchData)
    },
  })
  options = getData().optionsType
  getKeyOptionsLabelMap = (key) => {
    const obj = {}
    if (this.options && this.options[key]) {
      this.options[key].forEach((item) => {
        const { label, value } = item
        obj[value] = label
      })
    }
    return obj
  }
  /**
   * 撤回
   */
  remove = () => {
    Modal.confirm({
      title: '是否确定关闭立项？',
      onOk: async () => {
        const { keys } = this.table.getSelected()
        if (keys === 0) {
          return
        }
        await Api.remove({ ids: keys })
        this.table.search()
      },
    })
  }

  createModal = new ModalStore({
    onFinish: async (values) => {
      const data = await Api.postInfoAdd(values)
      this.createModal.close()
      this.table.search()
      history.push(`/credit/establish/detail/${data.id}?newProject=true`)
    },
  })
}
export default new Store()
