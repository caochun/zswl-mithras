import { TableStore, Modal, ModalStore, App, SearchBarStore, PageStore } from '@zswl/components'
import { makeAutoObservable, history } from '@zswl/admin'
import Api from '../api'
import fileList from '@/api/common/fileList'
import { message } from 'antd'
import { downFile, toHump } from '@/utils'

const { getData } = App
class Store {
  constructor({ businessVersion, reduceId, isFormApproval, contractId, isProcess }) {
    this.businessVersion = businessVersion
    this.isFormApproval = isFormApproval
    this.reduceId = reduceId
    this.contractId = contractId
    this.isProcess = isProcess
    makeAutoObservable(this)
  }
  list
  reductionList
  receiptList
  removeFileIds = []
  interest
  // page = new PageStore({
  //   request: async (params) => {
  //     this.getReductionList(params)
  //     this.getReceiptList(params)
  //     this.getRentDetail(params)
  //     this.getInterest(params)
  //     // return res
  //   },
  // })
  page = {
    getParams: () => {
      return {
        contractId: this.contractId,
        isProcess: this.isProcess,
        businessVersion: this.businessVersion,
      }
    },
  }
  init = (params) => {
    if (!params.contractId) return
    this.getReductionList(params)
    this.getReceiptList(params)
    this.getRentDetail(params)
    this.getInterest(params)
  }
  pushRemoveFileIds = (id) => {
    this.removeFileIds = [...this.removeFileIds, id]
  }
  getRentDetail = async (params) => {
    const res = await Api.getRentDetail({ ...params })
    this.list = res
  }
  getReductionList = async (params) => {
    if (this.isFormApproval) {
      const res = await Api.postReductionList({
        id: this.reduceId,
        contractId: this.page.getParams().contractId,
      })
      this.reductionList = res
    } else {
      const { isProcess } = this.page.getParams()
      let currentParams = params
      if (isProcess) {
        currentParams = {
          ...params,
          collectionStatus: ['NEW', 'TAKE_EFFECT'],
        }
      }
      const res = await Api.postReductionList(currentParams)
      this.reductionList = res
    }
  }
  getInterest = async (params) => {
    const res = await Api.postDeductionInterest({
      ...params,
    })
    if (res) {
      this.interest = res.penaltyInterestSurplusAmount / 10000
    }
  }
  getReceiptList = async (params) => {
    const res = await Api.postReceiptList({ ...params })
    this.receiptList = res
  }
  materialsTable = new TableStore({
    request: () => {
      return {}
    },
  })
  deductionMaterialsTable = new TableStore({
    request: () => {
      return {}
    },
  })
  deductionMaterialsAddModal = new ModalStore({
    onFinish: async (values, initValues) => {
      const formData = new FormData()
      values.files
        ?.filter((item) => !item.id)
        .forEach((item) => {
          formData.append('files', item)
        })
      formData.append(
        'penaltyInterestDeductionAmount',
        parseInt(values.penaltyInterestDeductionAmount * 10000)
      )
      formData.append('reasonExplain', values.reasonExplain)
      formData.append('contractId', this.page.getParams().contractId)
      let currentFetch
      if (initValues) {
        currentFetch = Api.modifyDeduction
        this.removeFileIds.forEach((item) => {
          formData.append('removeFileIds', item || '')
        })
        formData.append('id', initValues.id)
      } else {
        currentFetch = Api.postCollectionEffect
      }
      const { code, msg } = await currentFetch(formData)
      this.removeFileIds = []
      if (code === 200) {
        this.getReductionList(this.page.getParams())
        this.deductionMaterialsAddModal.close()
      } else {
        msg && message.info(msg)
      }
    },
  })
  upload = async (file) => {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('mainId', this.page.getParams().contractId)
    formData.append('moduleType', 'OVERDUE_COLLECTION_REDUCTION')
    formData.append('materialsType', 'OVERDUE_COLLECTION')
    const res = await Api.postFileSave(formData)
    const { code, msg } = res
    if (code === 200) {
      message.info('文件上传成功！')
      this.getReceiptList(this.page.getParams())
      return res
    } else {
      msg && message.info(msg)
    }
  }
  remove = async (fileId) => {
    const functionCode = `${toHump('OVERDUE_COLLECTION_REDUCTION')}FileRemove`
    const { code, msg } = await Api.postFileRemove(
      {
        fileId,
        mainId: this.page.getParams().contractId,
        moduleType: 'OVERDUE_COLLECTION_REDUCTION',
      },
      functionCode
    )
    if (code === 200) {
      message.info('文件删除成功！')
      this.getReceiptList(this.page.getParams())
    } else {
      msg && message.info(msg)
    }
  }
  download = async (params) => {
    const functionCode = `${toHump(params.moduleType)}FileDownload`
    const res = await Api.postFileDownload(
      {
        ...params,
        mainId: this.page.getParams().contractId,
      },
      functionCode
    )
    downFile(res)
  }
  notice = async (params) => {
    const { code, msg } = await Api.postNotice({
      contractId: this.page.getParams().contractId,
    })
    if (code === 200) {
      message.info('通知成功！')
      this.getRentDetail(this.page.getParams())
    } else {
      msg && message.info(msg)
    }
  }
  modify = async () => {}
}
export default Store
