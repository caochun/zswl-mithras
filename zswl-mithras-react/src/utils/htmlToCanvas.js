import { message } from 'antd'
import html2canvas from 'html2canvas'

export const downLoadImg = async (node, name) => {
  const dom = document.getElementById(node)
  if (!dom) {
    message.error('未找到要截图的元素')
    return Promise.reject(new Error('未找到要截图的元素'))
  }
  const t = new Date().getTime()
  console.log({ t })
  return html2canvas(dom, {
    width: dom.offsetWidth,
    height: dom.offsetHeight,
    scrollX: 0,
    scrollY: 0,
    x: 0,
    y: 0,
    // aphoto为被截图节点id
    allowTaint: false,
    useCORS: true, // 支持跨域图片的截取，不然图片截取不出来
    // 图片服务器配置 Access-Control-Allow-Origin: *
    scale: 1, // 降低缩放比例可以提高性能
    logging: false, // 禁用日志输出
    backgroundColor: '#ffffff', // 设置背景色，避免透明背景处理
  })
    .then((canvas) => {
      let dataURL = canvas.toDataURL('image/jpg')
      const blob = base64ToBlob(dataURL)
      let evt = document.createEvent('HTMLEvents')
      evt.initEvent('click', true, true)
      let aLink = document.createElement('a')
      aLink.download = `${name}.png`
      aLink.href = URL.createObjectURL(blob)
      aLink.click()
      // 释放URL对象
      URL.revokeObjectURL(aLink.href)
      message.success('下载成功')
      return Promise.resolve()
    })
    .catch(() => {
      message.error('系统问题，请刷新后重试')
      return Promise.reject(new Error('下载失败'))
    })
}

// base64转blob
const base64ToBlob = (base64) => {
  const arr = base64.split(',')
  const mime = arr[0].match(/:(.*?);/)[1]
  const bstr = atob(arr[1])
  let n = bstr.length
  const u8arr = new Uint8Array(n)
  while (n--) {
    u8arr[n] = bstr.charCodeAt(n)
  }
  return new Blob([u8arr], { type: mime })
}
