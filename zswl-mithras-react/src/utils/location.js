/* eslint-disable */
// 替换地址栏参数
export function changeURLArg(name, value) {
  let url = document.URL,
    resultUrl = ''
  let reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)', 'i')
  let r = window.location.search.substr(1).match(reg)
  let replaceText = name + '=' + value
  if (r != null) {
    let tmp = url.replace(unescape(name + '=' + r[2]), replaceText)
    resultUrl = tmp
  } else {
    if (url.match('[?]')) {
      resultUrl = url + '&' + replaceText
    } else {
      resultUrl = url + '?' + replaceText
    }
  }
  window.history.replaceState(null, null, resultUrl)
}
