import { http } from '@zswl/admin'
import { message } from 'antd'
import store from './store'

const getOptions = () => http.get('/select/all')
const getCountryList = () => http.get('/select/country')
// const getBase = () => http.get('/init', { mock: { delay: 1000 } })
const getBase = () =>
  http
    .get('/user/menuTree')
    .then((res) => {
      const { user } = res
      if (user.updatePwdStatus) {
        message.warning('密码已过期，请修改密码')
        setTimeout(() => {
          window.location.href = '/login?isChangePwd=true'
        }, 1000)
      }
      return res
    })
    .catch((err) => {
      store.logout()
    })

const getAllIndustry = (params) => http.get('/select/industry/all', { params })
const getUserList = (params) => http.get('/user/list', { params })
const getAssociationDict = () => http.post('/association/report/getAssociationDict', {})

export default {
  init: () =>
    http.all([
      getBase(),
      getOptions(),
      getCountryList(),
      getAllIndustry(),
      getAssociationDict(),
      // getUserList({ curPage: 1, pageSize: 1000 }),
    ]),
  logout: () => http.post('/user/logout'),
  getUserInfo: (params) => http.get('/userCenter/getUserInfo', { params }),
  saveChangePwd: (params) => http.put('/userCenter/changePwd', params, { type: 'formData' }),
  OCROnline: (params) => http.post('/ocr/detectOnline', params, { type: 'upload', timeout: 0 }),
  ocrDownload: (params) => http.get('/materials/downloadCommon', { params }),
}
