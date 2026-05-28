import { TableStore, Modal, ModalStore, App } from '@zswl/components'
import { makeAutoObservable, history } from '@zswl/admin'
import Api from '@/api/financial/orgManage'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  table = new TableStore({
    request: (searchData) => {
      return Api.postAgencyList(searchData)
    },
  })
  /**
   * 撤回
   */
  delete = () => {
    Modal.confirm({
      title: '是否确定删除？',
      onOk: async () => {
        const { keys } = this.table.getSelected()

        await Api.postAgencyRemove({ ids: keys })
        this.table.search()
      },
    })
  }

  createModal = new ModalStore({
    onFinish: async (values) => {
      try {
        const data = await Api.postAgencyAdd(values)
        this.createModal.close()
        history.push(`/financial/guarantee/detail/${data}`)
      } catch (e) {
        if (e === '获取天眼查信息失败，请确认是否手工录入该客户信息') {
          Modal.confirm({
            title: e,
            onOk: async () => {
              const data = await Api.postAgencyAddhalf(values)
              history.push(`/financial/guarantee/detail/${data}`)
              this.createModal.close()
            },
          })
        }
      }
    },
  })
}
export default new Store()
