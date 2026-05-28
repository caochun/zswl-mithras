import { TableStore, Modal, ModalStore, App } from '@zswl/components'
import { makeAutoObservable, history } from '@zswl/admin'
import Api from './api'
import dayjs from 'dayjs'
const { getData } = App
class Store {
  constructor() {
    makeAutoObservable(this)
  }
  table = new TableStore({
    request: (searchData) => {
      return Api.getExamineList(searchData)
    },
  })

  /**
   * 撤回
   */
  remove = () => {
    Modal.confirm({
      title: '是否确定删除考核？',
      onOk: async () => {
        const { keys } = this.table.getSelected()
        if (keys === 0) {
          return
        }
        await Api.remove({ ids: keys })
        this.table.search()
      },
    })
  }

  createModal = new ModalStore({
    onFinish: async (values) => {
      console.log('table')
      const day = dayjs(values.yearAndMonth)
      let list = this.table.list.filter((item, i) => {
        return item.examineYear === +day.format('YYYY') && item.examineMonth === +day.format('MM')
      })
      console.log('list', list)
      if (list.length > 0) {
        Modal.confirm({
          title: `系统提示`,
          content: '该考核月份已有考核表，请确认是否覆盖',
          closable: true,
          onOk: async () => {
            const data = await Api.getExamineAdd({
              examineYear: day.format('YYYY'),
              examineMonth: day.format('MM'),
            })
            this.createModal.close()
            this.table.search()
            // history.push(`/budgetManagement/assessment/detail`)
          },
        })
        return
      }
      // 添加预算考核
      const data = await Api.getExamineAdd({
        examineYear: day.format('YYYY'),
        examineMonth: day.format('MM'),
      })
      this.createModal.close()
      this.table.search()
      // history.push(`/budgetManagement/assessment/detail`)
    },
  })
}
export default new Store()
