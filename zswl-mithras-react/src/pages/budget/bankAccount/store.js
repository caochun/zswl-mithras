import { makeAutoObservable } from '@zswl/admin'
import { ModalStore, TableStore, Modal } from '@zswl/components'
import { message } from 'antd'
import bankAccountApi from '@/api/budget/bankAccountApi'
import moment from 'moment'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  list = new TableStore({
    request: async (params) => {
      return await bankAccountApi.postBankaccountList(params)
    },
  })
  editModal = new ModalStore({
    onFinish: async (values, initValues = {}) => {
      await bankAccountApi.postBankaccountSave({
        ...values,
        id: initValues.id,
      })
      message.info('保存成功！')
      this.list.search()
      this.editModal.close()
    },
    onOpen: (values) => {
      if (!values) return
      const { openingDate, ...rest } = values
      return {
        ...rest,
        openingDate: openingDate ? moment(openingDate) : undefined,
      }
    },
  })
  itemEdit = (val) => {
    this.editModal.open(val)
  }
  itemDelete = async (val) => {
    Modal.confirm({
      title: `请确认是否删除？`,
      onOk: async () => {
        await bankAccountApi.postBankaccountDelete({ id: val.id })
        message.success('删除成功！')
        this.list.search()
      },
    })
  }
}
export default Store
