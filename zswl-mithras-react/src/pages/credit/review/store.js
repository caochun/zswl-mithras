import { TableStore, Modal, ModalStore } from '@zswl/components'
import { makeAutoObservable, history } from '@zswl/admin'
import groupCreditReviewApi from '@/api/credit/groupCreditReviewApi'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  $table = new TableStore({
    request: (searchData) => {
      return groupCreditReviewApi.postInfoList(searchData)
    },
  })

  $createModal = new ModalStore({
    onFinish: async (values) => {
      Modal.confirm({
        title: '是否进行授信评审?',
        content: values.projectName.label,
        onOk: async () => {
          const data = await groupCreditReviewApi.postInfoAdd({
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
    const { list } = await groupCreditReviewApi.getClientList({ clientName: e, effected: true })
    return list
  }
}
export default new Store()
