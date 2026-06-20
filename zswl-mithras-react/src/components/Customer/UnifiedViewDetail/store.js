import { PageStore, TableStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import blackListApi from '@/api/customerView/blackGrayApi'
import Api from '@/api/customerView/customerDetailApi'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  detail = {}
  getDetail = async () => {
    const { id, ...params } = this.page.getParams()
    const res = await Api.getHeadBody(params)
    this.detail = res
  }
  page = new PageStore({
    request: async ({ id, ...params }) => {
      try {
        this.getDetail()
        const [library, customerInfo] = await Promise.all([
          blackListApi.getLibrary(
            { ...params, unifiedSocialCreditCode: params?.uscc, clientId: id },
            'riskControlBlackGrayBaseInfoLibrary'
          ),
          id ? Api.getUnifiedViewDetail({ clientId: id }) : {},
        ])

        return {
          ...(library ?? {}),
          ...(customerInfo ?? {}),
        }
      } catch (error) {
        return {}
      }
    },
  })
  table = new TableStore({
    request: (params) => {
      return Api.getDetail({ id: params?.id })
      return []
    },
  })
  checkQcc = async () => {
    return await Api.generateToken();
  }
}
export default new Store()
