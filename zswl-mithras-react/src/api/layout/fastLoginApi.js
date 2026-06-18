import loginApi from '@/api/permission/login'

export default {
  getAuthCode: loginApi.getAuthCode,
  login: loginApi.login,
}
