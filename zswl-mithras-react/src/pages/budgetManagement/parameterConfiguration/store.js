import { TableStore, ModalStore } from '@zswl/components' 
import { makeAutoObservable } from '@zswl/admin'
import { message } from 'antd'
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
      console.log('result',result)
      const list = []
      Object.keys(result).map((key) => {
        list.push({
          title: key,
          createTime: result[key]?.[0].category,
          createByName: result[key]?.[0].paramName,
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
    onFinish: async (values) => {
      console.log(values)
    },
  })
  editItem = (record) => {
    this.typeInfo = record
    this.$editModal.open(record)
  }
}
export default Store
