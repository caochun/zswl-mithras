import { TableStore, Modal, ModalStore } from '@zswl/components'
import { makeAutoObservable, history } from '@zswl/admin'
import Api from './api'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  $table = new TableStore({
    request: (searchData) => {
      return Api.getList(searchData)
    },
  })

  $createModal = new ModalStore({
    onFinish: async (values) => {
      Modal.confirm({
        title: '是否进行授信评审?',
        content: values.projectName.label,
        onOk: async () => {
          const data = await Api.postProjectReview({
            groupCreditEstablishId: values.id,
          })
          this.$createModal.close()
          this.$table.search()
          history.push(`/credit/review/detail/${data.id}?newProject=true`)
        },
      })
    },
  })

  getClientList = async (e) => {
    const { list } = await Api.getClientList({ clientName: e, effected: true })
    return list
  }
}
export default new Store()
