import { ModalStore, PageStore, TableStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import sealForDocumentsApi from '@/api/overdue/sealForDocumentsApi'
import { message } from 'antd'
import { noEnumDownloadAll } from '@/components/Table'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  page = new PageStore({})
  table = new TableStore({
    request: async (params) => {
      return await sealForDocumentsApi.postPrintingPageList(params)
    },
  })

  createModal = new ModalStore({
    onOpen: async (record) => {
      if (record?.id) return record
      // const id = await sealForDocumentsApi.postPrintingAdd({})
      // return { id }
      return {}
    },
    onFinish: async (params) => {
      await sealForDocumentsApi.postPrintingSubmit(params)
      this.table.search()
      this.createModal.close()
      message.success('提交成功')
    },
  })
  download = async (record) => {
    const { id: mainId } = record
    await noEnumDownloadAll({ mainId, moduleType: 'DOC_PRINTING' })
    message.success('下载成功')
  }
  delete = async (record) => {
    await sealForDocumentsApi.postPrintingRemove(record)
    this.table.search()
  }
}
export default new Store()
