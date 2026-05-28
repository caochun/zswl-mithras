export const rules = {
  required(msg = '请输入') {
    return { required: true, message: msg }
  },
  maxLen(len) {
    return { type: 'string', max: len, message: `长度不得超过${len}` }
  },
  minLen(len = 0) {
    return { type: 'string', min: len, message: `长度不得小于${len}` }
  },
  maxNum(num) {
    return { type: 'number', max: num, message: `最大限制${num}` }
  },
  minNum(num = 0) {
    return { type: 'number', min: num, message: `最小限制${num}` }
  },
  arrayRequired(message = '请选择') {
    return { type: 'array', required: true, message }
  },
  objectRequired(message = '请选择') {
    return { type: 'object', required: true, message }
  },
  creditCode(message = '请输入正确的统一社会信用代码') {
    return { pattern: /^[0-9A-Z]{18}$/, message }
  },
  bankCode(message = '请输入正确的银行卡号') {
    return { pattern: /^[0-9]{16,19}$/, message }
  },
  phoneCode(message = '请输入正确的手机号码') {
    return { pattern: /^1[0-9]{10}$/, message }
  },
  startHttpOrHttp2(message = '请输入合法的http(https)地址') {
    return { pattern: /http(s)?:\/\/([\w-]+\.)+[\w-]+(\/[\w- .\/?%&=]*)?/, message }
  },
}
