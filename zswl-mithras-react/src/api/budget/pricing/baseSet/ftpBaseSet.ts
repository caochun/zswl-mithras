/* prettier-ignore-start */
import * as Types from './interface/ftpBaseSet'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // ftp参数设定表列表
  postSettingList: (data: Types.SettingListRequest): Promise<Types.SettingListResponse> =>
    http.post('/new/ftp/parameter/setting/list', data, { mock }),

  // 修改ftp参数设定表
  postSettingModify: (data: Types.SettingModifyRequest): Promise<Types.SettingModifyResponse> =>
    http.post('/new/ftp/parameter/setting/modify', data, { mock }),
}

/* prettier-ignore-end */
