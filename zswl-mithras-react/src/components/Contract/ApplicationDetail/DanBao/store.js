import { makeAutoObservable, toJS } from '@zswl/admin'
import { TableStore, ModalStore } from '@zswl/components'
import { message, Modal } from 'antd'
import mathjs from '@/utils/math'
import { debounce as _debounce } from 'lodash'
import Api from '@/api/contract/component/ApplicationDetail/DanBao/api'
import { hasValue, compareDetail } from '@/utils'
import DataUpload from '@/components/DataUpload'

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
  contractDataList = []

  getContactDataList = async () => {}

  remove = (record) => {
    Modal.confirm({
      title: '是否删除该条担保信息？',
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

  curItem = {}
  formatFile = (list) => {
    return (list ?? [])
      .filter((item) => item instanceof Object)
      .map((item) => item?.id)
      .filter(Boolean)
  }
  $createModal = new ModalStore({
    onOpen: async (record) => {
      this.isDetail = record ? !!record.isDetail : false
      this.isCreate = !record

      if (record) {
        let result = record
        if (this.isFormApproval) {
          const res = compareDetail(record)
          result = res.newDetail ?? {}
        }
        this.curItem = result
        this.clientType = result.guarantorType
        const resultClientList = result.guarantorInfo?.map((item) => {
          return {
            clientName: item.clientName,
            id: item.clientId,
          }
        })
        this.setResultClientList(resultClientList)
        this.clientList = resultClientList
        this.clientIds = result.guarantorIds
        this.searchClientList()
        this.getContractList()

        const amountMultipleObj = {}
        result?.amountMultiple?.map((item) => {
          amountMultipleObj[`amountMultiple-${item.clientId}`] = hasValue(item.amount)
            ? mathjs.toNonExponentialPlus(mathjs.format(mathjs.divide(item.amount, 10000)))
            : undefined
        })
        const resolutionFileId = await DataUpload.queryFileData(result.resolutionFileId)
        const constitutionFileIds = await DataUpload.queryFileData(result.constitutionFileList)
        return {
          ...result,
          amountSingle: hasValue(result.amountSingle)
            ? mathjs.toNonExponentialPlus(mathjs.format(mathjs.divide(result.amountSingle, 10000)))
            : undefined,
          ...amountMultipleObj,
          contactId: {
            label: result.contactName,
            value: result.contactId,
          },
          relatContracts: result.relatContracts || [],
          resolutionFileId,
          constitutionFileIds,
        }
      } else {
        return {
          guaranteeMethod: 'JOINT_RESPONSIBILITY',
          isReport: 1,
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
        guarantorType,
        relatContracts,
        guarantorIds,
        guaranteeMethod,
        isReport,
        jointGuaranteeMark,
        amountSingle,
        guarantorContractCode,
        resolutionType,
        contactId,
        resolutionFileId,
        constitutionFileIds,
        ...rest
      } = values

      const amountMultiple = []
      Object.keys(rest).forEach((key) => {
        if (key.includes('amountMultiple') > -1) {
          amountMultiple.push({
            clientId: String(key).split('-')[1],
            amount: mathjs.toNonExponentialPlus(mathjs.format(mathjs.multiply(rest[key], 10000))),
          })
        }
      })
      const files = (resolutionFileId ?? []).filter((item) => !(item instanceof File))
      const { fileList } = DataUpload.classify(files)
      const fileListId = (resolutionFileId ?? [])
        .filter((item) => item instanceof Object)
        .map((item) => item?.id)
        .filter(Boolean)

      const constitutionFiles = (constitutionFileIds ?? []).filter(
        (item) => !(item instanceof File)
      )
      const { fileList: multipartFileList } = DataUpload.classify(constitutionFiles)
      const constitutionFileList = this.formatFile(constitutionFileIds)
      const params = {
        resolutionType,
        guarantorType,
        fileListId,
        relatContracts,
        guaranteeMethod,
        isReport,
        guarantorIds,
        guarantorContractCode,
        jointGuaranteeMark,
        amountSingle: ['SINGLE', 'JOINT'].includes(jointGuaranteeMark)
          ? mathjs.toNonExponentialPlus(mathjs.format(mathjs.multiply(amountSingle, 10000)))
          : undefined,
        amountMultiple: !['SINGLE', 'JOINT'].includes(jointGuaranteeMark)
          ? JSON.stringify(amountMultiple)
          : undefined,
        contractId: this.contractId,
        contactId: typeof contactId === 'object' ? contactId.value : contactId,
        fileList,
        multipartFileList,
        constitutionFileList,
      }
      id
        ? await Api.updateItem({
            ...params,
            id,
          })
        : await Api.addItem(params)
      message.success(id ? '修改成功' : '新增成功')
      this.$createModal.close()
      this.$table.search({ contractId: this.contractId })
      this.baseStore?.fileTableRef.current.table.search()
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

  clientIds = []

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
      let isSame = this.curItem.guarantorType === this.clientType
      let _list = isSame ? list.concat(toJS(this.resultClientList)) : list
      this.clientList = _list
    }
  }

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

  contractDataList = []
  getContactDataList = _debounce(async (e, clientId) => {
    const res = await Api.getContactList({
      clientId,
      clientName: typeof e === 'object' ? undefined : e,
    })
    this.contractDataList = res?.list
  }, 500)
}
export default Store
