import { makeAutoObservable, history } from '@zswl/admin'
import { PageStore, Modal } from '@zswl/components'
import { message } from 'antd'
import localApi from './api'

const loop = (data, parent = []) => {
  return data?.map((item) => {
    let { hasRefresh, key, href, title, children } = item
    const hasChildren = (children || []).length > 0

    const res = {
      key,
      // key: title ? parent.concat(title).join('-') : null,
      href,
      title,
      hasRefresh,
      isLeaf: !hasChildren,
    }
    const parentTitle = parent.concat(title)
    if (hasChildren) {
      res.children = loop(children, parentTitle)
    }

    return res
  })
}

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  pageStore = new PageStore({
    request: async () => {
      const list = await localApi.getReportList()
      return {
        list: loop(list),
      }
    },
  })

  isFold = false
  setIsFold = (flag) => {
    this.isFold = flag
  }

  currentReport = {}
  setCurrentReport = (data) => {
    this.currentReport = data
  }

  handleRefresh = async () => {
    if (this.currentReport) {
      await localApi.getRefresh({
        reportName: this.currentReport.title,
      })
      message.success('更新成功')
      setTimeout(() => {
        const frameWindow = document.querySelector('#newReportIframe')
        frameWindow.src = this.currentReport.href
      }, 3000)
    } else {
      message.info('请选择管报')
    }
  }

  handleSelect = (selectedId, selectedRow) => {
    this.setCurrentReport(selectedRow.node)
  }
}
export default new Store()
