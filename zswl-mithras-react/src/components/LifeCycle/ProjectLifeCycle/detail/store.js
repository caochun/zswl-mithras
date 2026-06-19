import { timeFormat } from '@/utils'
import { makeAutoObservable } from '@zswl/admin'
import { ModalStore, PageStore, TableStore } from '@zswl/components'
import Api from '@/api/lifeCycle/projectLifeCycleApi'
class Store {
  constructor() {
    makeAutoObservable(this)
  }
  actualDetail = {}
  page = new PageStore({
    request: async (params) => {
      const res = await Promise.all([
        Api.getDetail(params).catch(() => {}),
        Api.getProjEstablishDetail(params).catch(() => {}),
        Api.getProjReviewDetail(params).catch(() => {}),
        Api.getProjContractDetail(params).catch(() => {}),
        Api.getRentCollection(params).catch(() => {}),
        Api.getAfterLeaseCheck(params).catch(() => {}),
      ])
      if (res) {
        return {
          ...res[0],
          projEstablish: res[1],
          projReview: res[2],
          contract: res[3],
          rentCollection: res[4],
          afterLeaseCheck: res[5],
        }
      }
    },
  })
  table = new TableStore({
    request: (searchData) => {
      return Api.getMilestone({
        ...searchData,
        eventTimeFrom: searchData.eventTime ? timeFormat(searchData.eventTime[0]) : undefined,
        eventTimeTo: searchData.eventTime ? timeFormat(searchData.eventTime[1]) : undefined,
        ...this.page.getParams(),
        // dataType: this.page.getParams().dataType,
        // projectId: this.page.getParams().projectId,
      })
    },
  })

  getCommerceData = async () => {}

  $riskStrategy = new ModalStore({
    onOpen: async () => {},
  })
  getGeneralDetail = async (id) => {
    this.clientId = id
    const res = await Api.getGeneralDetail({ clientId: id })
    this.classifyData = res
  }
  clientId
  classifyData = {}
  clientInfo = {}
  addressList = []
  // 获取五级分类、客户信息详情、客户注册地址
  getClientInfo = async (id) => {
    this.clientId = id
    if (id) {
      Promise.all([
        Api.getGeneralDetail({ clientId: id }),
        Api.getCommerceDetail({ clientId: id }),
        Api.getCommerceAddressList({ clientId: id }),
      ]).then((data) => {
        this.classifyData = data[0]
        this.clientInfo = data[1]
        this.addressList =
          data[2]?.list?.filter((item) => item.addressType === 'REGISTRY_ADDRESS') || []
      })
    } else {
      this.classifyData = {}
      this.clientInfo = {}
      this.addressList = []
    }
  }
  forceUpdateId = 1
}
export default new Store()
