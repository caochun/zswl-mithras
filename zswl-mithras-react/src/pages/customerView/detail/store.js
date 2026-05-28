import { PageStore, TableStore } from '@zswl/components'
import { http, makeAutoObservable } from '@zswl/admin'
import blackListApi from '@/api/blackList/queryExternalDataApi'
import Api from './api'
import { history } from '@zswl/admin'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  detail = {}
  getDetail = async () => {
    const { id, ...params } = this.page.getParams()
    const res = await http.post('/customer/view/detail/headBody', params, { timeout: 0 })
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
          id ? http.post('/client/unified/view/detail', { clientId: id }) : {},
        ])

        return {
          ...(library ?? {}),
          ...(customerInfo ?? {}),
        }
      } catch (error) {
        console.log('error: ', error)
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
