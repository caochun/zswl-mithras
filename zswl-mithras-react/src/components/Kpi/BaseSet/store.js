import { TableStore, ModalStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import baseSetApi from '@/api/kpi/baseSet/baseSetApi'
import { message } from 'antd'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  $table = new TableStore({
    request: (params) => {
      return baseSetApi.postBaseList({
        ...params,
      })
    },
  })

  typeInfo = null

  parameterModal = new ModalStore({
    onOpen: async (values) => {
      return values
    },
    onFinish: async (values) => {
      console.log(values)
    },
  })

  isEdit = false
  setIsEdit = (flag) => {
    this.isEdit = flag
  }

  editItem = (record) => {
    const { isEdit, ...rest } = record
    this.setIsEdit(isEdit)
    this.parameterModal.open(rest)
  }
  delete = async (params) => {
    await baseSetApi.postBaseRemove(params)
    message.success('删除成功')
    this.$table.search()
  }

  copyItemModal = new ModalStore({
    onOpen: (record) => {
      return record
    },
    onFinish: async (values) => {
      const id = await baseSetApi.postBaseCopy(values)
      this.$table.search()
      this.copyItemModal.close()
      this.setIsEdit(true)
      this.parameterModal.open({ effectMonth: values.effectMonth, id })
    },
  })
}
export default Store
