import { TableStore, ModalStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import Api from '@/api/cpm/marginManagementApi'
import { debounce as _debounce } from 'lodash'
import { message } from 'antd'
import DetailStore from '../detail/store'
import moment from 'moment'

const dateFormat = 'yyyy-MM-DD'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  table = new TableStore({
    request: async ({ rest }) => {
      return Api.backList({ id: DetailStore.getClientId(), recordType: 'REFUND', ...rest })
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
  addOreditRefund = 1
  //保证金退款
  refundModal = new ModalStore({
    onOpen: async (value) => {
      if (value) {
        this.addOreditRefund = 2
        let data = await Api.marginRecordDetail({ id: value.id })
        data.collectionDate = data.collectionDate && moment(data.collectionDate, dateFormat)
        // data.collectionAmount = data.collectionAmount
        data.ourBankAccountIdName = data.ourBankInfo?.accountNumber
        data.name1 = data.ourBankInfo?.accountBank
        data.ourBankAccountId = data.ourBankInfo?.accountName
        data.name2 = data.otherBankInfo?.accountBank
        data.otherBankAccountIdName = data.otherBankInfo?.accountNumber
        data.otherBankAccountId = data.otherBankInfo?.accountName
        if (data.enclosureName && data.enclosureId) {
          data.file = [{ name: data.enclosureName, id: data.enclosureId }]
        }
        return data
      }
    },
    onFinish: async (values, { id } = {}) => {
      if (id) {
        return
      }
      const {
        collectionAmount,
        collectionDate,
        collectionType,
        otherBankAccountId,
        file,
        ourBankAccountIdName,
        otherBankAccountIdName,
        ourBankAccountId,
        postscript,
        name2,
      } = values
      if (!(Number(collectionAmount) <= DetailStore?.marginDetailData?.canBackAmount)) {
        message.info('实付金额需小于等于保证金可退金额！')
        return
      }
      let formdata = new FormData()
      if (file) {
        const fileObj = new File(file, file[0].name)
        file && formdata.append('file', fileObj)
      }
      formdata.append('ourAccountId', this.ourBankInfoData?.id)
      formdata.append('ourAccountName', this.ourBankInfoData?.accountName)
      formdata.append('ourAccountNumber', this.ourBankInfoData?.accountNumber)
      formdata.append('ourAccountBank', this.ourBankInfoData?.accountBank)
      formdata.append('otherAccountName', otherBankAccountId)
      formdata.append('otherAccountNumber', otherBankAccountIdName)
      formdata.append('otherAccountBank', name2)
      formdata.append('collectionType', 'REFUND_MARGIN')
      formdata.append('collectionAmount', collectionAmount)
      formdata.append('collectionDate', moment(collectionDate).format('yyyy-MM-DD'))
      if (postscript) {
        formdata.append('postscript', postscript)
      }
      formdata.append('marginId', DetailStore.getClientId())
      await Api.addBackRecord(formdata)
      this.table.search()
      this.refundModal.close()
      message.success('提交成功！')
    },
  })

  refundModalClose = () => {
    this.addOreditRefund = 1
  }
  //保证金抵扣
  deductionModal = new ModalStore({
    onOpen: async (value) => {
      if (value) {
        const result = await Api.marginRecordDetail({ id: value.id })
        return {
          ...result,
          collectionDate: result.collectionDate ? moment(result.collectionDate) : undefined,
        }
      }
    },
  })

  clientList = []
  clientListOther = []
  searchClient = async (e, f) => {
    if (f == 1) {
      const data = await Api.getOurClientBankList({ clientName: e, pageSize: 30 })
      //this.clientList = list
      return data
    } else {
      const { list } = await Api.getListClient({ clientName: e, pageSize: 30 })
      return list
    }
  }
  clientSelectId = ''
  clientSelectIdOther = ''
  ourBankInfoData = {}
  bankChanges = (e, s, f) => {
    if (f == 1) {
      this.ourBankInfoData = s
      // this.searchBackNum(e, s.clientType, f)
      this.clientSelectId = s.id
    } else {
      this.searchBackNum(e, s.clientType, f)
      this.clientSelectIdOther = s.id
    }
  }
  backList = []
  backListOther = []
  searchBackNum = _debounce(async (clientId, type, f) => {
    let data = {}
    if (f == 1) {
      if (type == 'CORPORATION') {
        data = await Api.getClientBankList({ clientId })
      } else {
        data = await Api.getNormalBankAccountList({ clientId })
      }
      this.backList = data.list
    }
    if (f == 2) {
      if (type == 'CORPORATION') {
        data = await Api.getClientBankList({ clientId })
      } else {
        data = await Api.getNormalBankAccountList({ clientId })
      }
      this.backListOther = data.list
    }
  }, 500)
}
export default new Store()
