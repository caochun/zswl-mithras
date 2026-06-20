import { makeAutoObservable, toJS } from '@zswl/admin'
import DataUpload from '@/components/DataUpload'
import { TableStore, ModalStore } from '@zswl/components'
import { message, Modal } from 'antd'
import moment from 'moment'
import { timeFormat, compareDetail, downFile } from '@/utils'
import Api from '@/api/contract/component/ApplicationDetail/DiYa/api'
class Store {
  constructor(data) {
    this.businessVersion = data?.businessVersion
    this.isFormApproval = data?.isFormApproval
    this.contractId = data?.contractId
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
      title: '是否删除该条抵押信息？',
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

  curItem = {}
  $createModal = new ModalStore({
    onOpen: async (record) => {
      this.isDetail = record ? !!record.isDetail : false
      this.isCreate = !record
      if (record) {
        if (this.isFormApproval) {
          const res = compareDetail(record)
          const result = res.newDetail
          this.curItem = result
          const resultClientList = result.mortgageInfo?.map((item) => {
            return {
              clientName: item.clientName,
              id: item.clientId,
            }
          })
          this.setResultClientList(resultClientList || [])
          this.clientIds = result.mortgageIds
          this.clientList = resultClientList
          this.searchClientList()
          this.getContractList()

          const mortgagePledgeFileList = await DataUpload.queryFileData(res?.mortgagePledgeFileIds)
          return {
            ...result,
            file: result.fileName && {
              fileName: result.fileName,
              filePath: result.filePath,
            },
            mortgagePledgeFileList,
            assessDate: result.assessDate ? moment(result.assessDate) : undefined,
            relatContracts: result.relatContracts || [],
          }
        } else {
          this.curItem = record
          this.clientType = record.mortgageType
          // 抵押人名称
          const resultClientList = record.mortgageInfo?.map((item) => {
            return {
              clientName: item.clientName,
              id: item.clientId,
            }
          })
          this.setResultClientList(resultClientList || [])
          this.clientList = resultClientList
          this.clientIds = record.mortgageIds
          this.searchClientList()
          this.getContractList()

          return {
            ...record,
            file: record.fileName && {
              fileName: record.fileName,
              filePath: record.filePath,
              fileId: record.fileId,
            },
            assessDate: record.assessDate ? moment(record.assessDate) : undefined,
            relatContracts: record.relatContracts || [],
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
        mortgageType,
        relatContracts,
        mortgageIds,
        mortgageDescribe,
        appraisalCompany,
        appraisalCode,
        highest,
        assessDate,
        mortgageContractCode,
        mortgageItemType,
        contractMortgageType,
        mortgagePledgeObjList: mortgageFile,
        assess,
      } = values
      const { fileList, originalList } = DataUpload.classify(file)
      const { fileList: mortgagePledgeFileList, originalList: mortgageOriginal } =
        DataUpload.classify(mortgageFile)
      const mortgagePledgeFileIds = mortgageOriginal.map((v) => v.id ?? v.fileId) ?? []
      id
        ? await Api.updateItem({
            id,
            file: fileList[0],
            fileId: originalList[0]?.id ?? originalList[0]?.fileId,
            mortgageType,
            mortgageContractCode,
            mortgagePledgeFileList,
            mortgagePledgeFileIds,
            relatContracts,
            mortgageIds,
            mortgageDescribe,
            appraisalCompany,
            appraisalCode,
            highest,
            assess,
            mortgageItemType,
            contractMortgageType,
            assessDate: assessDate ? timeFormat(assessDate) : undefined,
            contractId: this.contractId,
          })
        : await Api.addItem({
            file: fileList[0],
            mortgageType,
            mortgagePledgeFileList,
            relatContracts,
            mortgageIds,
            mortgageContractCode,
            mortgageDescribe,
            appraisalCompany,
            appraisalCode,
            highest,
            assess,
            mortgageItemType,
            contractMortgageType,
            assessDate: assessDate ? timeFormat(assessDate) : undefined,
            contractId: this.contractId,
          })
      message.success(id ? '修改成功' : '新增成功')
      this.$createModal.close()
      this.baseStore.fileTableRef.current.table.search()
      this.$table.search({ contractId: this.contractId })
    },
  })

  preview = async ({ id }) => {
    window.open(`/preview/reportPreview/${id}`)
  }

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
