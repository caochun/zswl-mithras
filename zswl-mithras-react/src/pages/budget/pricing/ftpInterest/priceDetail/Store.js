import { TableStore, PageStore, ModalStore, Modal } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import { message } from 'antd'
import Api from './api'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  assessmentPrice = 0
  setAssessmentPrice = (value) => {
    this.assessmentPrice = value
  }
  page = new PageStore({})

  $table = new TableStore({
    request: async (params) => {
      const data = await Api.postFtpPriceList({
        ...params,
        receiptCode: this.page.getParams().receiptCode,
      })
      return data
    },
  })
  $editModal = new ModalStore({
    onOpen: async (values) => {
      return values
    },
    onFinish: async (values) => {
      Modal.confirm({
        title: '后续每一天的数据都将继承该调整，请再次确认！',
        onOk: async () => {
          const { id, recordDate } = values
          const res = await Api.postFtpPriceCheck({ id, recordDate })
          if (res) {
            Modal.confirm({
              title: `${recordDate}的价格已被确认，调整其价格将触发资金成本历史数据的重算。`,
              okText: '调整并重算',
              cancelText: '不调整',
              onOk: async () => {
                await Api.postFtpPriceUpdate({
                  ...values,
                  assessmentPrice: this.assessmentPrice,
                })
                message.success('操作成功')
                this.$editModal.close()
                this.$table.search()
              },
            })
          } else {
            await Api.postFtpPriceUpdate({
              ...values,
            })
            message.success('操作成功')
            this.$editModal.close()
            this.$table.search()
          }
        },
      })
    },
  })
}
export default Store
