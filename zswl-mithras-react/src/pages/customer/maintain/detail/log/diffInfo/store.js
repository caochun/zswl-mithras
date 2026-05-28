import { makeAutoObservable } from '@zswl/admin'
import { TableStore, PageStore } from '@zswl/components'
import Api from './api'

class Store {
  constructor() {
    makeAutoObservable(this, { clientId: false })
  }
  initListData = {}
  loading = true
  initList = async (id, clientId) => {
    this.initListData = await Api.compareVersionList({ id, clientId })
    this.loading = false
    //变更之前
    this.addressInfo.search()
    this.linkManInfo.search()
    this.bondRating.search()
    this.shareholder.search()
    this.affiliated.search()
    this.bankAccount.search()
    //变更之后数据
    this.addressInfoAfter.search()
    this.linkManInfoAfter.search()
    this.bondRatingAfter.search()
    this.shareholderAfter.search()
    this.affiliatedAfter.search()
    this.bankAccountAfter.search()
  }
  page = new PageStore({})
  //地址信息
  addressInfo = new TableStore({
    pagination: false,
    request: () => {
      return this.initListData.oldData.CORP_ADDRESS
    },
  })
  //联系人信息
  linkManInfo = new TableStore({
    pagination: false,
    request: () => {
      return this.initListData.oldData.CORP_CONTACT
    },
  })
  //发债及评级
  bondRating = new TableStore({
    pagination: false,
    request: () => {
      return this.initListData.oldData.CORP_BOND
    },
  })
  //股东信息
  shareholder = new TableStore({
    pagination: false,
    request: () => {
      return this.initListData.oldData.CORP_SHAREHOLDER
    },
  })
  //关联企业
  affiliated = new TableStore({
    pagination: false,
    request: () => {
      return this.initListData.oldData.CORP_RELATED_ENTERPRISE
    },
  })
  //银行账户
  bankAccount = new TableStore({
    pagination: false,
    request: () => {
      return this.initListData.oldData.CORP_BANK_ACCOUNT
    },
  })

  /*****************变更之后******************/
  //地址信息
  addressInfoAfter = new TableStore({
    pagination: false,
    request: () => {
      return this.initListData.newData.CORP_ADDRESS
    },
  })
  //联系人信息
  linkManInfoAfter = new TableStore({
    pagination: false,
    request: () => {
      return this.initListData.newData.CORP_CONTACT
    },
  })
  //发债及评级
  bondRatingAfter = new TableStore({
    pagination: false,
    request: () => {
      return this.initListData.newData.CORP_BOND
    },
  })
  //股东信息
  shareholderAfter = new TableStore({
    pagination: false,
    request: () => {
      return this.initListData.newData.CORP_SHAREHOLDER
    },
  })
  //关联企业
  affiliatedAfter = new TableStore({
    pagination: false,
    request: () => {
      return this.initListData.newData.CORP_RELATED_ENTERPRISE
    },
  })
  //银行账户
  bankAccountAfter = new TableStore({
    pagination: false,
    request: () => {
      return this.initListData.newData.CORP_BANK_ACCOUNT
    },
  })
}
export default Store
