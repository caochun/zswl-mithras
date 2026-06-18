import { http } from '@zswl/admin'

const postContractList = (params, functionCode = 'contractbaseinfolist') =>
  http.post('/contract/base/info/list', params, {
    headers: {
      functionCode,
    },
  })

export default {
  postContractList,
  postFtpInterestContractList: (params) =>
    postContractList(params, 'ftpInterestContractBaseInfoList'),
}
