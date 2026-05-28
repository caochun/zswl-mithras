import { ModalStore, PageStore, TableStore,Modal } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import collectionManagementApi from '@/api/overdue/collectionManagementApi'
import { message } from 'antd'
import { noEnumDownloadAll } from '@/components/Table/NoEnumFileTable'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  collectionActionList = []
  page = new PageStore({
    request: async (params) => {
      const { collectionActionList, ...rest } =
        await collectionManagementApi.postOverduecollectionDetail({ id: params.id })
      this.collectionActionList = collectionActionList
      return rest
    },
  })

  table = new TableStore({
    request: async (params) => {
      const res = await collectionManagementApi.postOverduecollectionList({ id: params?.id })
      return res
    },
  })
  contractTable = new TableStore({
    request: async (params) => {
      const { clientId } = this.page.getData()
      const res = await collectionManagementApi.getContractList({ clientId })
      return res
    },
  })
  collectionRecordTable = new TableStore({
    request: async (params) => {
      return this.collectionActionList
    },
  })

  collectionModal = new ModalStore({
    onOpen: async (record) => {
      if (record.id) {
        const { contractIds, ...rest } = record
        return { contractIds: contractIds ?? [], ...rest }
      }
      const { id: ocId } = this.page.getData()
      const id = await collectionManagementApi.postActionAdd({ ...record, ocId })
      const { contractIds, ...rest } = await collectionManagementApi.postActionDetail({ id })
      await this.page.init()
      return { id, contractIds: contractIds ?? [], ...rest }
    },
    onFinish: async (data) => {
      const { id } = this.page.getData()

      const isSendLetter = data.type === 'SEND_LETTER'
      if (isSendLetter) {
        await collectionManagementApi.postActionSubmit({ id: data?.id, ocId: id })
      }
      await this.page.init()
      this.collectionRecordTable.search()
      message.success('提交成功')
      this.collectionModal.close()
    },
  })

  exportRecord = async () => {
    const { id } = this.page.getData()
    const res = await collectionManagementApi.postActionDownload({ id })
    return res
  }
  exportContract = async () => {
    const { clientId } = this.page.getData()
    const res = await collectionManagementApi.postContractExport({ clientId })
    return res
  }
  deleteRow = async (record) => {
    const { id } = record
    Modal.confirm({
      title: '确认删除吗？',
      onOk: async () => {
        await collectionManagementApi.postActionDelete({
          id
        })
        await this.page.init()
        this.collectionRecordTable.search()
        message.success('删除成功')
        this.collectionModal.close()
      },
    })
  }
  download = async (record) => {
    const { id: mainId } = record
    await noEnumDownloadAll({
      mainId,
      moduleType: 'OVERDUE_COLLECTION_ACTION',
      materialsType: 'COLLECTION',
    })
    message.success('下载成功')
  }
}
export default Store
