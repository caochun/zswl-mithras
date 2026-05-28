import { http } from '@zswl/admin'

const mock = false
export default {
  // ftp参数设定表列表
  postSettingList: (data) =>
    http.post('/budget/parameter/config/list', data, { mock }),

  // 修改ftp参数设定表
  postSettingModify: (data) =>
    http.post('/budget/parameter/config/modify', data, { mock }),
}

