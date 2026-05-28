/* prettier-ignore-start */
import * as Types from './interface/exchangeRateApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 财务管理-汇率设置-分页列表
  postExchangeRatePageList: (
    data: Types.ExchangeRatePageListRequest,
  ): Promise<Types.ExchangeRatePageListResponse> =>
    http.post('/baseData/exchangeRate/pageList', data, { mock }),

  // 财务管理-汇率设置-删除
  postExchangeRateDelete: (
    data: Types.ExchangeRateDeleteRequest,
  ): Promise<Types.ExchangeRateDeleteResponse> =>
    http.post('/baseData/exchangeRate/delete', data, { mock }),

  // 财务管理-汇率设置-新增
  postExchangeRateAdd: (
    data: Types.ExchangeRateAddRequest,
  ): Promise<Types.ExchangeRateAddResponse> =>
    http.post('/baseData/exchangeRate/add', data, { mock }),

  // 财务管理-汇率设置-编辑
  postExchangeRateModify: (
    data: Types.ExchangeRateModifyRequest,
  ): Promise<Types.ExchangeRateModifyResponse> =>
    http.post('/baseData/exchangeRate/modify', data, { mock }),
}

/* prettier-ignore-end */
