import { TableStore, ModalStore } from '@zswl/components'
import { makeAutoObservable, toJS } from '@zswl/admin'
import Api from '@/api/cpm/marginManagementApi'
import { debounce as _debounce } from 'lodash'
import moment from 'moment'
import { message } from 'antd'
import DetailStore from '../detail/store'
const dateFormat = 'yyyy-MM-DD'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  table = new TableStore({
    request: async ({ rest }) => {
      return await Api.marginRecordList({
        id: DetailStore.getClientId(),
        recordType: 'COLLECTION',
        ...rest,
      })
    },
  })
  updateRecord = async (s, id, f, callback) => {
    if (f == 1) {
      await Api.updateRecord({ marginId: DetailStore.getClientId(), id, writeOff: s.key })
    } else {
      await Api.updateRecord({ marginId: DetailStore.getClientId(), id, review: s.key })
    }
    if (s.key == 'REVIEWED') {
      callback && callback()
    }
    this.table.search()
  }
  addOredit = 1
  marginRecordDetailData = {}
  //弹窗
  collectionModal = new ModalStore({
    onOpen: async (value) => {
      if (value) {
        this.addOredit = 2
        const data = await Api.marginCollectionDetail({ id: value.id })
        console.log(data, 'banknamebanknamebankname')
        data.bankAccountId = data.ourBankInfo?.accountNumber
        data.bankname = data.ourBankInfo?.accountBank
        data.ourBankAccountId = data.ourBankInfo?.accountName
        data.collectionDate = data.collectionDate && moment(data.collectionDate, dateFormat)
        if (data.enclosureName && data.enclosureId) {
          data.file = [{ name: data.enclosureName, id: data.enclosureId }]
        }
        return data
      }
    },
    onFinish: async (values, { id } = {}) => {
      if (id) {
        // await Api.addRecord({
        //   ...values,
        //   marginId: DetailStore.getClientId(),
        //   clientId: this.clientSelectId,
        // })
      } else {
        let formdata = new FormData()
        const {
          bankAccountId,
          collectionAmount,
          collectionDate,
          collectionType,
          file,
          ourBankAccountId,
          postscript,
        } = values

        console.log(collectionAmount, 'collectionAmount111')
        if (collectionAmount == 0) {
          message.info('实收金额不能为零！')
          return
        }
        // if (file) {
        //   const fileObj = new File(file, file[0].name)
        //   file && formdata.append('file', fileObj)
        // }
        formdata.append('ourAccountId', this.currentBankInfo?.id)
        formdata.append('ourAccountName', this.currentBankInfo?.accountName)
        formdata.append('ourAccountNumber', this.currentBankInfo?.accountNumber)
        formdata.append('ourAccountBank	', this.currentBankInfo?.accountBank)
        formdata.append('collectionAmount', collectionAmount)
        formdata.append('collectionDate', moment(collectionDate).format('yyyy-MM-DD'))
        // formdata.append('ourBankAccountId', ourBankAccountId)

        // if (postscript) {
        //   formdata.append('postscript', postscript)
        // }
        formdata.append('collectionType', collectionType)
        formdata.append('marginId', DetailStore.getClientId())
        //formdata.append('clientId', this.clientSelectId)
        await Api.addRecord(formdata)
        this.table.search()
        this.collectionModal.close()
        message.success('提交成功！')
      }
    },
  })
  payModalClose = () => {
    this.addOredit = 1
  }
  clientList = []
  searchClient = async (e) => {
    const data = await Api.getOurClientBankList({ clientName: e })
    return data
  }
  clientSelectId = ''
  currentBankInfo = {}
  bankChange = (e, s) => {
    console.log(e, s, 'sssss')
    this.currentBankInfo = s
    //this.searchBackNum(e, s.clientType)
    this.clientSelectId = s.id
  }
  backList = []
  searchBackNum = _debounce(async (clientId, type) => {
    let data = {}
    if (type == 'CORPORATION') {
      data = await Api.getClientBankList({ clientId })
    } else {
      data = await Api.getNormalBankAccountList({ clientId })
    }
    console.log(toJS(data), 2000)
    this.backList = data.list
  }, 500)
}
export default new Store()
