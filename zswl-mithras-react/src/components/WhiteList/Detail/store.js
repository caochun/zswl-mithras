import { PageStore } from '@zswl/components'
import { history, makeAutoObservable } from '@zswl/admin'
import assessmentWhitelistApi from '@/api/whiteList/assessmentWhitelistApi'
import { message } from 'antd'

/**
 * 评估机构白名单详情页数据存储类
 * 管理详情页面的数据获取和操作
 */
class Store {
  constructor() {
    makeAutoObservable(this)
  }

  // 页面数据管理
  page = new PageStore({
    request: async (params) => {
      const response = await assessmentWhitelistApi.postWhitelistDetail({
        id: params.id,
        version: params.version,
      })
      return response
    },
  })
  /**
   * 更新评估机构工商信息
   * @param {Object} record - 要更新工商信息的记录
   */
  handleCommerceRefresh = async () => {
    const { id, businessVersion } = this.page.getParams()
    await assessmentWhitelistApi.postCommerceRefresh({ id, businessVersion })
    message.success('更新工商信息成功')
    this.page.init()
  }
  /**
   * 提交评估机构白名单审批
   */
  handleSubmit = async () => {
    const { id } = this.page.getParams()
    await assessmentWhitelistApi.postWhitelistSubmit({ id })
    message.success('提交审批成功')
    this.page.init()
  }
  /**
   * 出库审批
   */
  handleOutSubmit = async () => {
    const { id } = this.page.getParams()
    await assessmentWhitelistApi.postOutSubmit({ id })
    message.success('出库审批成功')
    this.page.init()
  }
  handleCancel = async () => {
    const { id } = this.page.getParams()
    await assessmentWhitelistApi.postWhitelistCancel({ id })
    message.success('取消操作成功')
    history.push('/whiteList')
  }
  saveData = async (values) => {
    const { id } = this.page.getParams()
    await assessmentWhitelistApi.postReasonSave({ id, ...values })
    this.page.init()
  }
}

export default Store
