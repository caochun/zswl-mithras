import { TableStore, App, FormStore } from '@zswl/components'
import { makeAutoObservable, history, http } from '@zswl/admin'
import Api from '../api'
import { message } from 'antd'
import { getQjtAc, getSalt } from '@/utils'

const { getData } = App
class Store {
  constructor() {
    makeAutoObservable(this)
  }
  noticeDetail
  previewUrl

  pageParams = {}
  setPageParams = (params) => {
    this.pageParams = params
  }
  initData = {}
  init = async (params) => {
    if (!params.collectionId) {
      return
    }
    const res = await Promise.all([
      Api.getRentCollectionDetail({ id: params.collectionId }).catch(() => {}),
      Api.getRentOverdueDetail({ id: params.collectionId }).catch(() => {}),
    ])
    this.initData = {
      rentDetail: res[0],
      overdue: res[1],
    }
  }
  // page = new PageStore({
  //   request: async (params) => {
  //     const res = await Promise.all([
  //       Api.getRentCollectionDetail({ id: params.collectionId }).catch(() => {}),
  //       Api.getRentOverdueDetail({ id: params.collectionId }).catch(() => {}),
  //     ])
  //     return {
  //       rentDetail: res[0],
  //       overdue: res[1],
  //     }
  //   },
  // })

  formStore = new FormStore({})
  preview = async () => {
    const values = await this.formStore.submit()
    const { receiverMail, comment, title, ourAccountNumber = [] } = values
    const { collectionId } = this.pageParams
    const res = await Api.getEmailPreview({
      collectionId,
      receiverMail,
      comment,
      title,
      bankId: ourAccountNumber[0],
    })
    if (res) {
      this.getPreviewUrl(res)
    }
  }

  send = async () => {
    const values = await this.formStore.submit()
    const { receiverMail, comment, title, ourAccountNumber = [] } = values
    const { collectionId } = this.pageParams
    const { code, msg } = await Api.postEmailSend({
      collectionId,
      receiverMail,
      comment,
      title,
      bankId: ourAccountNumber[0],
    })
    if (code === 200) {
      message.info('邮件发送成功！')
      this.getNoticeDetail()
    } else {
      msg && message.info(msg)
    }
  }

  getNoticeDetail = async () => {
    const { collectionId } = this.pageParams
    const res = await Api.getEmailDetail({ collectionId })
    this.noticeDetail = res
    this.getPreviewUrl(res.htmlPreviewUrl)
  }

  getPreviewUrl = async (url) => {
    const htmlRes = await fetch(url, {
      headers: {
        token: JSON.stringify({
          _salt_: getSalt(),
          _qjt_ac_: getQjtAc(),
        }),
        'X-Cf-Random': App.getToken(),
      },
    })
    const blob = await htmlRes.blob()
    this.previewUrl = URL.createObjectURL(blob)
  }

  penaltyInterestModalTable = new TableStore({
    request: async (params) => {
      const { collectionId } = this.pageParams
      const res = await Api.postPenaltyInterestRecordHistory({
        ...params,
        id: collectionId,
      })
      return res || []
    },
  })
}
export default Store
