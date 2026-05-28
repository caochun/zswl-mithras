/* prettier-ignore-start */
import { http } from '@zswl/admin'

export default {
  postConcentrationClientList: (params) =>
    http.post('/risk/control/concentration/list/client', params),
  postConcentrationGroupList: (params) =>
    http.post('/risk/control/concentration/list/group', params),
  postConcentrationRelateList: (params) =>
    http.post('/risk/control/concentration/list/relate', params),
  postConcentrationAllRelate: (params) =>
    http.post('/risk/control/concentration/all/relate', params),
  postConcentrationFri: (params) => http.post('/risk/control/concentration/fri', params),
}
