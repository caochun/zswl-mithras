import { makeAutoObservable } from '@zswl/admin'
import { TableStore, ModalStore, Upload } from '@zswl/components'
import { message } from 'antd'
import { debounce as _debounce } from 'lodash'
import { compareDetail, compare } from '@/utils'
import Api from '@/api/contract/component/ApplicationDetail/ChengZuRen/api'
import DataUpload from '@/components/DataUpload'

class Store {
  constructor(data) {
    this.isFormApproval = data?.isFormApproval
    this.contractId = data?.contractId
    this.businessVersion = data?.businessVersion
    this.baseStore = data?.baseStore
    makeAutoObservable(this)
  }
  baseStore
  contractList = []
  setContractList = (data) => {
    this.contractList = data
  }

  getContactList = _debounce(async (e) => {
    if (this.curRecord.lesseeId) {
      const res = await Api.getContactList({ clientId: this.curRecord.lesseeId, clientName: e })
      this.setContractList(res?.list || [])
    }
  }, 500)

  $table = new TableStore({
    pagination: false,
    request: (params) => {
      if (this.isFormApproval) {
        return Api.getListCompare({
          contractId: this.contractId,
          ...params,
          businessVersion: this.businessVersion,
        })
      }
      return Api.getList({ contractId: this.contractId, ...params })
    },
  })

  curRecord = {}

  formatFile = (list) => {
    return (list ?? [])
      .filter((item) => item instanceof Object)
      .map((item) => item?.id)
      .filter(Boolean)
  }
  $createModal = new ModalStore({
    onOpen: async (record) => {
      let result = record
      if (this.isFormApproval) {
        const res = compareDetail(record)
        result = res.newDetail
      }
      this.curRecord = result
      this.setContractList([])
      const resolutionFileId = await DataUpload.queryFileData(result.resolutionFileId)
      const constitutionFileIds = await DataUpload.queryFileData(result.constitutionFileList)
      const contactId = result.contactId
        ? { key: result.contactId, label: result.contactName }
        : undefined
      return {
        ...result,
        contactId,
        stockRiskExposure: result.stockRiskExposure / 10000,
        resolutionFileId: resolutionFileId,
        constitutionFileIds,
        rentConcatAccountId: result.rentConcatAccountId
          ? {
              label: result.rentConcatAccountName,
              value: result.rentConcatAccountId,
            }
          : undefined,
      }
    },
    onFinish: async (values) => {
      const {
        id,
        lesseeType,
        contactId,
        isReport,
        resolutionType,
        leaseItemFileType,
        resolutionFileId,
        constitutionFileIds,
        rentConcatAccountId,
      } = values
      const files = (resolutionFileId ?? []).filter((item) => !(item instanceof File))
      const { fileList } = DataUpload.classify(files)
      const fileListId = this.formatFile(resolutionFileId)

      const constitutionFiles = (constitutionFileIds ?? []).filter(
        (item) => !(item instanceof File)
      )
      const { fileList: multipartFileList } = DataUpload.classify(constitutionFiles)
      const constitutionFileList = this.formatFile(constitutionFileIds)

      await Api.updateItem({
        id,
        stockRiskExposure: this.curRecord.stockRiskExposure,
        contactId: contactId?.key || contactId,
        lesseeType: lesseeType?.key || lesseeType,
        fileList,
        fileListId,
        isReport,
        contractId: this.contractId,
        resolutionType,
        leaseItemFileType,
        constitutionFileIds: constitutionFileList,
        multipartFileList,
        rentConcatAccountId: rentConcatAccountId?.value,
      })
      message.success('编辑成功')
      this.$createModal.close()
      this.$table.search({ contractId: this.contractId })
      this.baseStore?.fileTableRef.current.table.search()
    },
  })
}
export default Store
