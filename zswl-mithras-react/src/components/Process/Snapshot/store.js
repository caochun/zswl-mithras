import { makeAutoObservable } from '@zswl/admin'
import html2canvas from 'html2canvas'
import moment from 'moment'
import Api from '@/api/process/detail/flowDetailApi'
import { message } from 'antd'

class Store {
  constructor() {
    makeAutoObservable(this, { clientId: false })
  }
  detailData = {}
  queryDetail = async (id, diff) => {
    if (diff == 'processInstanceId') {
      this.detailData = await Api.processDetail({ processInstanceId: id })
    }
  }
  toPrint = (node) => {
    const header = `<html><head><title></title></header><body>`
    const footer = `</body></html>`
    const content = document.getElementById(node).innerHTML //把需要打印的指定内容赋给body.innerHTML
    document.body.innerHTML = header + content + footer
    window.print()
    window.location.reload()
  }

  base64ToBlob(img) {
    let parts = img.split(';base64,')
    let contentType = parts[0].split(':')[1]
    let raw = decodeURIComponent(encodeURIComponent(window.atob(parts[1])))
    let rawLength = raw.length
    let uInt8Array = new Uint8Array(rawLength)
    for (let i = 0; i < rawLength; ++i) {
      uInt8Array[i] = raw.charCodeAt(i)
    }
    return new Blob([uInt8Array], { type: contentType })
  }

  downLoadImg = async (node, processId) => {
    // setShow(false)
    // 绑定在某个点击事件
    const dom = document.getElementById(node)
    const t = new Date().getTime()
    html2canvas(dom, {
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
    })
      .then((canvas) => {
        let dataURL = canvas.toDataURL('image/jpg')
        const blob = this.base64ToBlob(dataURL)
        let evt = document.createEvent('HTMLEvents')
        evt.initEvent('click', true, true)
        let aLink = document.createElement('a')
        aLink.download = `${this.detailData?.modelName}审批快照流程-${processId}.png` // 设置要下载的图片的名称
        aLink.href = URL.createObjectURL(blob)
        aLink.click()
        message.success('下载成功')

        // const link = document.createElement('a') // 建立一个超连接对象实例
        // const event = new MouseEvent('click') // 建立一个鼠标事件的实例
        // link.download = `审批快照流程-${processId}.png` // 设置要下载的图片的名称
        // link.href = canvas.toDataURL() // 将图片的URL设置到超连接的href中
        // link.dispatchEvent(event) // 触发超连接的点击事件
        // message.success('下载成功')
      })
      .catch(() => {
        message.error('系统问题，请刷新后重试')
      })
  }
}
export default Store
