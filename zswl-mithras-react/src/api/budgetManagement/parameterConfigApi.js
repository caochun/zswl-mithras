import { http } from '@zswl/admin'

const mock = false

export default {
  postSettingList: (data) => http.post('/budget/parameter/config/list', data, { mock }),
  postSettingModify: (data) => http.post('/budget/parameter/config/modify', data, { mock }),
}
