import { ModalStore, PageStore, TableStore } from '@zswl/components'
import { observer, http, history, makeAutoObservable, getQuery } from '@zswl/admin'
import exchangeRateApi from '@/api/budget/exchangeRateApi'
import { message } from 'antd'
import moment from 'moment'

/**
 * 汇率设置页面的数据管理Store
 * 管理表格数据、新增弹窗、编辑弹窗等状态
 */
class Store {
  constructor() {
    makeAutoObservable(this)
  }

  // 页面状态管理
  page = new PageStore({})

  // 表格数据管理
  table = new TableStore({
    request: async (params) => {
      const { year, month } = this.page.getParams()
      const isFormApproval = getQuery('typeId') === 'approval' ? undefined : 0
      const res = await exchangeRateApi.postExchangeRatePageList({
        ...params,
        isDraft: isFormApproval,
        year,
        month,
      })
      return res
    },
  })

  // 新增弹窗管理
  createModal = new ModalStore({
    onOpen: (record) => {
      const { targetYear, targetMonth } = record ?? {}
      const time = `${targetYear}-${targetMonth}`
      return {
        ...record,
        targetYear: targetYear ? moment(time) : undefined,
        targetMonth: targetMonth ? moment(time) : undefined,
      }
    },
    onFinish: async (params) => {
      const isFormApproval = getQuery('typeId') === 'approval' ? 1 : 0
      const api = params.id
        ? exchangeRateApi.postExchangeRateModify
        : exchangeRateApi.postExchangeRateAdd
      await api({ ...params, isDraft: isFormApproval })
      this.table.search()

      message.success('操作成功')
      this.createModal.close()
    },
  })

  /**
   * 删除汇率记录
   * @param {number} id - 汇率记录ID
   */
  deleteRecord = async (id) => {
    await exchangeRateApi.postExchangeRateDelete({ id })
    this.table.search()
    message.success('删除成功')
  }
}

export default Store
