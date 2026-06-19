import { TableStore, ModalStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import Api from '@/api/budget/pricing/baseSet/ftpBaseSet'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  $table = new TableStore({
    request: async (params) => {
      const result = await Api.postSettingList({
        ...params,
      })
      const list = []
      Object.keys(result).map((key) => {
        list.push({
          title: key,
          category: result[key]?.[0].category,
          list: result[key] ?? [],
        })
      })
      return list
    },
  })
  typeInfo = null
  $editModal = new ModalStore({
    onOpen: async (values) => {
      return values
    },
    onFinish: async () => {},
  })
  editItem = (record) => {
    this.typeInfo = record
    this.$editModal.open(record)
  }
}
export default Store
