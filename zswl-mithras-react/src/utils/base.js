import mathjs from '@/utils/math'
import { getLocalStorage, setLocalStorage } from '@zswl/admin'
import { App } from '@zswl/components'
import { message } from 'antd'
import { isObject } from 'lodash'
import moment from 'moment'
import queryString from 'query-string'
import { blobToJson } from './file'

/**
 * 登陆相关的，之前同盾的登陆逻辑（不合理），之后会改掉
 * @type {string}
 */
const SALT = '_salt_'
const QJT_AC = '_qjt_ac_'
export function setSalt (value) {
  setLocalStorage(SALT, value)
}
export function getSalt () {
  return getLocalStorage(SALT)
}
export function setQjtAc (value) {
  setLocalStorage(QJT_AC, value)
}
export function getQjtAc () {
  return getLocalStorage(QJT_AC)
}

export function clearCookie () {
  const keys = document.cookie.match(/[^ =;]+(?=\=)/g)
  if (keys) {
    keys.forEach((key) => {
      document.cookie = key + '=;expires=Thu, 01 Jan 1970 00:00:00 GMT;path=/'
    })
  }
}

export const baseURL = () => {
  // return 'http://10.158.250.185:7003'
  if (__DATA__.cicd_base_url) {
    return __DATA__.cicd_base_url
  }
  return {
    lvwOps: 'http://10.42.200.13',
    preSvc: 'http://10.158.33.211',
    prod: 'http://api.zsrzzl.com.cn',
    uat: 'http://10.158.33.228:80',
    kl: 'http://10.158.250.241:7003',
    dq: 'http://10.158.250.171:7003',
    local: 'http://10.158.250.103:7003',
    dev: 'http://127.0.0.1:7003',
    sit: 'http://10.158.32.219:8080',
    hhr: 'http://10.158.250.91:7003',
    dk: 'http://10.158.250.174:7003'
  }[__ENV__]
}

export function handleHttpError (res) {
  if (res && (res.msg === '所选客户为空' || res.msg == '本次核销后剩余本金不为0，请核对！' || 'NO_WRITE_OFF_COMPLETED' == res.msg.split('@')[0])) {
    return
  }
  if (Object.prototype.toString.call(res) == '[object Blob]') {
    blobToJson(res)
      .then((jsonData) => {
        message.info(jsonData?.msg || '操作失败')
      })
      .catch((error) => {
        console.error(error)
      })
    return
  }

  const { code, msg } = res
  if ([401000, 401001, 401027, 401002].includes(code)) {
    message.destroy()
    message.error(msg, 1).then(() => {
      if (window.location.pathname !== '/login') {
        App.clearToken()
        window.location.href = '/login'
      }
    })
  } else {
    message.info(msg, 3)
  }
}

export function yearFormat (time) {
  return moment(time).format('yyyy')
}

export function monthFormat (time) {
  return moment(time).format('yyyy-MM')
}

export const mouthFormat = monthFormat

export function timeFormat (time) {
  return moment(time).format('yyyy-MM-DD')
}

export function timeSecondFormat (time) {
  return moment(time).format('yyyy-MM-DD HH:mm:ss')
}

export function formatPercent (val, initFormat = 10000) {
  return hasValue(val) ? mathjs.divide(val, initFormat) : undefined
}

export function numToFixed (num, decimal = 2) {
  return hasValue(num) ? Number(num).toFixed(decimal) : num
}

export const columnsRender = (val, isLog, format) => {
  return isLog ? (
    <span style={{ color: val?.isChange ? 'red' : undefined }}>{hasValue(val?.value) ? (format ? format(val?.value) : val?.value) : '-'}</span>
  ) : hasValue(val) ? (
    format ? (
      format(val)
    ) : (
      val
    )
  ) : (
    '-'
  )
}

export function formatNull (val) {
  return val === null ? undefined : val
}

export const isEmpty = (obj) => {
  if (typeof obj === 'undefined' || obj === null || obj === '') {
    return true
  }
  return false
}
//过滤对象中值为null的对象
export function preProcessData (formData) {
  Object.keys(formData).forEach((item) => {
    if (isEmpty(formData[item])) {
      delete formData[item]
    }
  })
  return formData
}

export function amountStrToNumber (val) {
  if (!val) {
    return val
  }
  return parseFloat(val.toString().replace(/,/g, ''))
}

export const getUrl = (url, query) => {
  return url + '?' + queryString.stringify(query)
}

export const getKeyOptionsLabelMap = (key, options) => {
  const obj = {}
  if (options && options[key]) {
    options[key].forEach((item) => {
      const { label, value } = item
      obj[value] = label
    })
  }
  return obj
}

export const hasValue = (val, ignoreZero = false) => {
  if (val === 0 || val === '0') {
    if (ignoreZero) {
      return false
    }
    return true
  }
  if (!val) {
    return false
  }
  return true
}

export const getCompareValue = (val) => {
  if (isObject(val)) {
    return val.value
  }
  return val
}
export const getKeyOptionsLabelMapPlus = (key) => {
  const { getData } = App
  const options = getData().optionsType
  const obj = {}
  if (options && options[key]) {
    options[key].forEach((item) => {
      const { label, value } = item
      obj[value] = label
    })
  }
  return obj
}

export const getRiskControlIndustryClassifySelectOptions = (currentValue) => {
  const list = App.getData().optionsType?.riskControlIndustryClassify ?? []
  const raw = getCompareValue(currentValue)
  if (raw === 'TRAVEL') {
    return list
  }
  return list.filter((item) => item.value !== 'TRAVEL')
}

export function getAge(identityCard) {
  let len = (identityCard + '').length
  if (len == 0) {
    return ''
  }
  // else {
  //   if (len != 15 && len != 18) {
  //     //身份证号码只能为15位或18位其它不合法
  //     return ''
  //   }
  // }
  let strBirthday = ''
  if (len == 18) {
    //处理18位的身份证号码从号码中得到生日和性别代码
    strBirthday = identityCard.substr(6, 4) + '/' + identityCard.substr(10, 2) + '/' + identityCard.substr(12, 2)
  }
  if (len == 15) {
    strBirthday = '19' + identityCard.substr(6, 2) + '/' + identityCard.substr(8, 2) + '/' + identityCard.substr(10, 2)
  }
  //时间字符串里，必须是“/”
  let birthDate = new Date(strBirthday)
  let nowDateTime = new Date()
  let age = nowDateTime.getFullYear() - birthDate.getFullYear()
  //再考虑月、天的因素;.getMonth()获取的是从0开始的，这里进行比较，不需要加1
  if (nowDateTime.getMonth() < birthDate.getMonth() || (nowDateTime.getMonth() == birthDate.getMonth() && nowDateTime.getDate() < birthDate.getDate())) {
    age--
  }
  return age
}

// 银行卡隔四位空格
export function formateCard (value) {
  if (!value) {
    return value
  }
  if (!Number.isNaN(value.replace(/[ ]/g, ''))) {
    value = value.replace(/\s/g, '').replace(/(\d{4})(?=\d)/g, '$1 ') //四位数字一组，以空格分割
  }
  return value
}

// 获取url指定参数
export const getUrlParam = (param) => {
  const reg = new RegExp('(^|&)' + param + '=([^&]*)(&|$)')
  const r = window.location.search.substr(1).match(reg)
  return r !== null ? decodeURI(r[2]) : null
}

export function getMenuIdByPathname () {
  // 这里是接口带上对应当前菜单的menuCode，为了权限
  const { menuKeys = [], menuMap } = App.getData()
  const match = menuKeys.find((key) => window.location.pathname.indexOf(key) === 0)
  if (match) {
    const menuPath = menuMap[match]
    return menuPath[menuPath.length - 1]?.code
  }
}

export const errorFormScroll = (err, form) => {
  form.scrollToField(err.errorFields[0]?.name, {
    behavior (actions) {
      actions.forEach(({ el, top, left }) => {
        el.scrollTop = top + 200
        el.scrollLeft = left
      })
    },
  })
  return Promise.reject(err)
}

// 获取枚举值下的 对象
export const getEnumFlatObjByType = (key) => {
  const enumValue = App.getData().optionsType?.[key]
  const obj = {}
  enumValue.map((item) => {
    const { label, value } = item
    obj[value] = label
  })
  return obj
}

export function getRandomString (len = 32) {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'
  const maxPos = chars.length
  let tmp = ''
  for (let i = 0; i < len; i++) {
    tmp += chars.charAt(Math.floor(Math.random() * maxPos))
  }
  return tmp
}

/**
 * 高精度字符串乘法：numStr * multiplier，返回字符串结果
 * @param {string} numStr - 数字字符串（整数/小数均可）
 * @param {number|string} multiplier - 倍数（数字/字符串）
 * @returns {string} 乘法结果字符串
 */
export function highPrecisionMultiply (numStr, multiplier = 100000000) {
  // 统一转为字符串处理
  const num1 = String(numStr)
  const num2 = String(multiplier)

  // 处理小数：提取小数点位置，转为整数运算
  const [int1, dec1 = ''] = num1.split('.')
  const [int2, dec2 = ''] = num2.split('.')
  const decLen = dec1.length + dec2.length // 最终小数位数

  // 转为纯整数字符串（去掉小数点）
  const intStr1 = int1 + dec1
  const intStr2 = int2 + dec2

  // 整数字符串乘法核心逻辑
  const len1 = intStr1.length
  const len2 = intStr2.length
  const result = new Array(len1 + len2).fill(0)

  // 从后往前逐位相乘
  for (let i = len1 - 1; i >= 0; i--) {
    for (let j = len2 - 1; j >= 0; j--) {
      const product = (intStr1[i] - 0) * (intStr2[j] - 0)
      const sum = product + result[i + j + 1]
      result[i + j + 1] = sum % 10 // 当前位
      result[i + j] += Math.floor(sum / 10) // 进位
    }
  }

  // 去掉前置零
  let resultStr = result.join('').replace(/^0+/, '')
  if (resultStr === '') resultStr = '0' // 处理0的情况

  // 补回小数点
  if (decLen > 0) {
    const pointPos = resultStr.length - decLen
    resultStr = pointPos > 0 ? `${resultStr.slice(0, pointPos)}.${resultStr.slice(pointPos)}` : `0.${'0'.repeat(-pointPos)}${resultStr}`
  }

  return resultStr
}
