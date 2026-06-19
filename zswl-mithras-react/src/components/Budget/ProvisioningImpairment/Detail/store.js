import { TableStore, PageStore, Modal } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import { message } from 'antd'
import Api from '@/api/budget/provisioning/provisioning'

class Store {
  constructor({ id }) {
    this.id = id
    makeAutoObservable(this)
  }
  page = new PageStore({})

  provisionDate
  $table = new TableStore({
    request: async (params) => {
      const { provisionDate, provisionBaseInfoList } = await Api.postInfoDetail({
        ...params,
        id: this.id,
      })
      this.provisionDate = provisionDate
      return provisionBaseInfoList
    },
  })

  confirm = async () => {
    await Api.postInfoEffect({ provisionDate: this.provisionDate, id: this.id })
    message.success('操作成功')
    this.$table.search()
  }

  refresh = () => {
    Modal.confirm({
      title: `是否重新载入截止到${this.provisionDate}的数据？刷新后将重置为“未生效”状态，需重新确认后数据才能生效。`,
      onOk: async () => {
        const { id } = this.page.getParams()
        await Api.postInfoRefresh({ provisionDate: this.provisionDate, id })
        message.success('操作成功')
        this.$table.search()
      },
    })
  }

  // 列表编辑态索引
  editIndex = -1
  editItem = ({ rowIndex }) => {
    this.editIndex = rowIndex
  }
  cancelEdit = () => {
    this.editIndex = -1
  }
  confirmEdit = async ({ record }) => {
    const { values } = await this.$table.submit()
    const { profitCurrent, profitTotal } = values
    await Api.postInfoModify({
      id: record.id,
      profitCurrent,
      profitTotal,
    })
    message.success('操作成功')
    this.editIndex = -1
    this.$table.search()
  }
}
export default Store
