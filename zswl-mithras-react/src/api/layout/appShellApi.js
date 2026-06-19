import { http } from '@zswl/admin'

const getOptions = () => http.get('/select/all')
const getCountryList = () => http.get('/select/country')
const getBase = () => http.get('/user/menuTree')
const getAllIndustry = (params) => http.get('/select/industry/all', { params })
const getAssociationDict = () => http.post('/association/report/getAssociationDict', {})

export default {
  init: () =>
    http.all([
      getBase(),
      getOptions(),
      getCountryList(),
      getAllIndustry(),
      getAssociationDict(),
    ]),
  logout: () => http.post('/user/logout'),
  getUserInfo: (params) => http.get('/userCenter/getUserInfo', { params }),
  saveChangePwd: (params) => http.put('/userCenter/changePwd', params, { type: 'formData' }),
  OCROnline: (params) => http.post('/ocr/detectOnline', params, { type: 'upload', timeout: 0 }),
  ocrDownload: (params) => http.get('/materials/downloadCommon', { params }),
}
