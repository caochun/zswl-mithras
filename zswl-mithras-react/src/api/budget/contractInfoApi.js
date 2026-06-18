import contractApi from '@/api/contract/baseInfo'

export default {
  postContractList: (params) => contractApi.postContractList(params),
  postFtpInterestContractList: (params) =>
    contractApi.postContractList(params, 'ftpInterestContractBaseInfoList'),
}
