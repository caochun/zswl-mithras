/* prettier-ignore-start */
import * as Types from './interface/orgManage'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 修改担保机构
  postAgencyModify: (data: Types.AgencyModifyRequest): Promise<Types.AgencyModifyResponse> =>
    http.post('/fund/guarantee/agency/modify', data, { mock }),

  // 删除担保机构
  postAgencyRemove: (data: Types.AgencyRemoveRequest): Promise<Types.AgencyRemoveResponse> =>
    http.post('/fund/guarantee/agency/remove', data, { mock }),

  // 同步天眼查数据
  postAgencySync: (data: Types.AgencySyncRequest): Promise<Types.AgencySyncResponse> =>
    http.post('/fund/guarantee/agency/sync', data, { mock }),

  // 担保机构下拉列表
  postAgencyPulldown: (data: Types.AgencyPulldownRequest): Promise<Types.AgencyPulldownResponse> =>
    http.post('/fund/guarantee/agency/pulldown', data, { mock }),

  // 担保机构列表
  postAgencyList: (data: Types.AgencyListRequest): Promise<Types.AgencyListResponse> =>
    http.post('/fund/guarantee/agency/list', data, { mock }),

  // 担保机构详情
  postAgencyDetail: (data: Types.AgencyDetailRequest): Promise<Types.AgencyDetailResponse> =>
    http.post('/fund/guarantee/agency/detail', data, { mock }),

  // 新增担保机构并同步天眼查信息
  postAgencyAdd: (data: Types.AgencyAddRequest): Promise<Types.AgencyAddResponse> =>
    http.post('/fund/guarantee/agency/add', data, { mock }),

  // 新增用于手动录入的担保机构
  postAgencyAddhalf: (data: Types.AgencyAddhalfRequest): Promise<Types.AgencyAddhalfResponse> =>
    http.post('/fund/guarantee/agency/addhalf', data, { mock }),

  // 额度使用详情
  postInfoLimitDetail: (
    data: Types.InfoLimitDetailRequest,
  ): Promise<Types.InfoLimitDetailResponse> =>
    http.post('/fund/guarantee/info/limitDetail', data, { mock }),
}

/* prettier-ignore-end */
