import { makeAutoObservable } from '@zswl/admin'
import { PageStore } from '@zswl/components'
import { message } from 'antd'
import Api from './api'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  activeTag = '-1'
  setActiveTag = (v) => {
    this.activeTag = v
  }
  clientGroup = {}
  setClientGroup = (clientInfos) => {
    const group = {}
    clientInfos.forEach((element) => {
      if (group[element.clientRole]) {
        group[element.clientRole].push(element)
      } else {
        group[element.clientRole] = [{ ...element }]
      }
    })
    const newClientGroup = {}
    Object.keys(group).map((key) => {
      newClientGroup[key] = []
      group[key].map((item, index) => {
        if (item?.clientType === 'NORMAL') {
          return
        }
        newClientGroup[key].push(item)
      })
    })
    this.clientGroup = newClientGroup
  }
  curClientId
  setCurClientId = (v) => {
    this.curClientId = v
  }

  onTabChange = (e) => {
    const value = e.target.value
    if (value !== '-1') {
      this.setCurClientId(value.split('_')[1])
    } else {
      this.setCurClientId()
    }
    this.setActiveTag(value)
  }

  onSaveBaseInfo = async (values) => {
    const { inspectionDate } = values
    await Api.modifyQuery({
      id: this.page.getData().id,
      inspectionDate,
    })
    this.page.init()
  }

  // 报告查询
  showChaXunValue = true
  setShowChaXun = (val) => {
    this.showChaXunValue = val
  }
  onSaveChaXun = async (values) => {
    const { preventiveMeasures, queryConclusion } = values
    await Api.modifyConclusion({
      id: this.page.getData().id,
      preventiveMeasures,
      queryConclusion,
    })
    this.page.init()
  }
  // 承租人
  showChengZuRen = {}
  setShowChengZuRen = (key, val) => {
    this.showChengZuRen[key] = val
  }
  onSaveChengZuRen = async (values, autoClientId) => {
    await Api.modifyClientInfo({
      id: autoClientId,
      ...values,
    })
    this.page.init()
  }
  // 提交审批
  submit = async () => {
    await Api.submitExternal({
      id: this.page.getData().id,
    })
    message.success('提交成功')
  }

  // 下载报告
  downReport = async () => {
    await Api.downReport({
      id: this.page.getData().id,
    })
  }

  page = new PageStore({
    request: async ({ id, businessVersion }) => {
      const res = await Api.getExternalDetail({ id, businessVersion })
      return res ?? {}
    },
  })
}
export default Store
