import { makeAutoObservable } from '@zswl/admin'
import { Modal, TableStore } from '@zswl/components'
import { message } from 'antd'
import costBudgetApi from '@/api/budgetManagement/costBudgetApi'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  list = new TableStore({
    request: async (params) => {
      const response = await costBudgetApi.postCostList({
        ...params,
        pageSize: params.pageSize || 10,
      })

      return response
    },
  })

  itemDelete = async (record) => {
    Modal.confirm({
      title: '确认删除',
      content: '确定要删除该预算吗？',
      onOk: async () => {
        try {
          await costBudgetApi.postCostRemove({ id: record.id })
          message.success('删除成功！')
          this.list.search()
        } catch (error) {
          message.error('删除失败！')
        }
      },
    })
  }
}

export default Store
