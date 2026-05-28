import { makeAutoObservable, toJS } from '@zswl/admin'
import { TableStore, ModalStore } from '@zswl/components'
import DataUpload from '@/components/DataUpload'
import { compareDetail, downFile } from '@/utils'
import { message, Modal } from 'antd'
import Api from './api'

class Store {
  constructor(data) {
    this.contractId = data?.contractId
    this.isFormApproval = data?.isFormApproval
    this.businessVersion = data?.businessVersion
    this.baseStore = data?.baseStore
    makeAutoObservable(this)
  }

  isDetail = false
  isCreate = false

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

  remove = (record) => {
    Modal.confirm({
      title: '是否删除该条质押信息？',
      onOk: async () => {
        await Api.removeItem({
          id: this.isFormApproval ? record.id.value : record.id,
          contractId: this.contractId,
        })
        message.success('删除成功')
        this.$table.search({ contractId: this.contractId })
      },
    })
  }
  downTemp = async ({ filename }) => {
    const res = await Api.postDownloadTemp({ filename })
    downFile(res)
  }

  preview = async ({ id }) => {
    window.open(`/preview/reportPreview/${id}`)
  }

  curItem = {}
  $createModal = new ModalStore({
    onOpen: (record) => {
      this.isDetail = record ? !!record.isDetail : false
      this.isCreate = !record

      if (record) {
        if (this.isFormApproval) {
          const res = compareDetail(record)
          const result = res.newDetail
          this.curItem = result
          this.clientType = result.pledgeType
          const resultClientList = result.pledgeInfo?.map((item) => {
            return {
              clientName: item.clientName,
              id: item.clientId,
            }
          })
          this.setResultClientList(resultClientList)
          this.clientList = resultClientList
          this.clientIds = result.pledgeIds
          this.searchClientList()
          return {
            ...result,
            relatContracts: res.relatContracts ?? [],
            file: result.fileName && {
              fileName: result.fileName,
              fileId: result.fileId,
            },
          }
        } else {
          this.curItem = record
          this.clientType = record.pledgeType
          const resultClientList = record.pledgeInfo?.map((item) => {
            return {
              clientName: item.clientName,
              id: item.clientId,
            }
          })
          this.setResultClientList(resultClientList)
          this.clientList = resultClientList
          this.clientIds = record.pledgeIds
          this.searchClientList()
          this.getContractList()
          return {
            ...record,
            relatContracts: record.relatContracts ?? [],
            file: record.fileName && {
              fileName: record.fileName,
              fileId: record.fileId,
            },
          }
        }
      }
    },
    onFinish: async (values) => {
      if (this.isDetail) {
        this.$createModal.close()
        return
      }
      const {
        id,
        file,
        relatContracts,
        pledgeType,
        pledgeIds,
        pledgeDescribe,
        highest,
        contractPledgeType,
        pledgeContractCode,
        mortgagePledgeObjList: mortgageFile,
      } = values
      const { fileList, originalList } = DataUpload.classify(file)
      const { fileList: mortgagePledgeFileList, originalList: mortgageOriginal } =
        DataUpload.classify(mortgageFile)
      const mortgagePledgeFileIds = mortgageOriginal.map((v) => v.id ?? v.fileId) ?? []

      id
        ? await Api.updateItem({
            id,
            file: fileList,
            fileId: originalList[0]?.id ?? originalList[0]?.fileId,
            mortgagePledgeFileList,
            mortgagePledgeFileIds,
            pledgeType,
            contractPledgeType,
            pledgeContractCode,
            pledgeDescribe,
            pledgeIds,
            highest,
            relatContracts,
            contractId: this.contractId,
          })
        : await Api.addItem({
            file: fileList,
            pledgeType,
            mortgagePledgeFileList,
            contractPledgeType,
            pledgeContractCode,
            pledgeDescribe,
            pledgeIds,
            highest,
            relatContracts,
            contractId: this.contractId,
          })
      message.success(id ? '修改成功' : '新增成功')
      this.$createModal.close()
      this.baseStore.fileTableRef.current.table.search()
      this.$table.search({ contractId: this.contractId })
    },
  })

  generateContractNo = async () => {
    await Api.generateContractNo({
      contractId: this.contractId,
    })
    message.success('操作成功')
    this.$table.search({ contractId: this.contractId })
  }

  clientType = ''
  resultClientList = []
  setResultClientList = (list) => {
    this.resultClientList = list
  }

  clientList = []
  searchClientList = async (val) => {
    const res = await Api.getClientList({
      effected: true,
      clientType: this.clientType,
      clientName: val,
      scene: 'other',
    })
    let list = res?.list || []
    if (val) {
      this.clientList = list
    } else {
      let isSame = this.curItem.mortgageType === this.clientType
      let _list = isSame ? list.concat(toJS(this.resultClientList)) : list
      this.clientList = _list
    }
  }
  clientIds = []
  contractList = []
  getContractList = async (val) => {
    if (this.clientIds.length === 0) {
      return
    }
    const res = await Api.postContractList({
      clientIds: this.clientIds,
      clientCode: val,
      contractId: this.contractId,
    })
    this.contractList = res || []
  }
}
export default Store
