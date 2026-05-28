import { makeAutoObservable } from '@zswl/admin'
import { ModalStore, TableStore, Modal, PageStore, Access } from '@zswl/components'
import { message } from 'antd'
import weeklyReportDetailApi from '@/api/budgetManagement/weeklyReportDetailApi'
import { monthStaticCard, staticNotMonthCard } from './staticData'
import { debounce, throttle } from 'lodash'
import weeklyReportApi from '@/api/budgetManagement/weeklyReportApi'

class Store {
  budgetPlanWeeklyReportId: number
  constructor({ id } = {}) {
    this.budgetPlanWeeklyReportId = id
    makeAutoObservable(this)
  }
  page = new PageStore({
    request: async (params) => {
      const res = await weeklyReportApi.postReportInfo(params)
      return { ...res }
    },
  })
  departmentList = []
  getDepartmentList = async () => {
    const res = await weeklyReportDetailApi.postDeptConfirmList({
      id: this.budgetPlanWeeklyReportId,
    })
    this.departmentList = res
  }
  addItem = () => {
    this.editModal.open()
  }

  itemDelete = async () => {
    const { keys } = this.projectTable.getSelected()
    await weeklyReportDetailApi.postDetailBatchDelete({ ids: keys })
    message.success('删除成功！')
    this.refresh()
  }

  refresh = () => {
    this.projectTable.search()
    this.getDepartmentList()
    this.getStatistics()
  }
  statistics = []
  getStatistics = async () => {
    const hasAccess = Access.validate(['budgetplanweeklyreportdetailstatistics'])
    if (!hasAccess) return
    const params = this.projectTable.getParams()
    const func = weeklyReportDetailApi.postDetailStatistics

    const res = await func({
      ...params,
      budgetPlanWeeklyReportId: this.budgetPlanWeeklyReportId,
    })
    const staticCard = monthStaticCard
    this.statistics = staticCard.map((item) => ({
      ...item,
      value: res[item.dataIndex],
    }))
  }
  editModal = new ModalStore({
    onFinish: async (values) => {
      const budgetPlanWeeklyReportId = this.page.getParams().id
      const res = await weeklyReportDetailApi.postDetailAdd({ ...values, budgetPlanWeeklyReportId })
      this.editModal.close()
      this.refresh()
    },
  })

  projectTable = new TableStore({
    request: async (params) => {
      const budgetPlanWeeklyReportId = this.budgetPlanWeeklyReportId
      const func = weeklyReportDetailApi.postReportPageList
      const { list, ...rest } = await func({
        ...params,
        budgetPlanWeeklyReportId,
      })
      this.refresh()
      return {
        list: list.map(({ contractCount, ...item }) => ({
          ...item,
          children: !!contractCount ? [] : undefined,
        })),
        ...rest,
      }
    },
  })
  expandRow = throttle(async (expanded, record) => {
    if (!expanded) return
    const list = this.projectTable.getList()
    const findItem = list.find((item) => item.id === record.id)
    if (findItem?.children?.length) return

    const res = await weeklyReportDetailApi.postPageListContract({
      id: record.id,
    })
    const newChildren = res.map((item) => ({
      ...item,
      level: 1,
    }))
    const newTableList = list.map((item) => ({
      ...item,
      children: item.id === record.id ? newChildren : item.children,
    }))
    this.projectTable.setList(newTableList)
  }, 1000)

  // 列表编辑态索引
  editIndex = -1
  editItem = ({ rowIndex }) => {
    this.editIndex = rowIndex
  }
  cancelEdit = () => {
    this.editIndex = -1
  }
  confirmEdit = async ({ record }) => {
    const { values, list } = await this.projectTable.submit()
    const newValues = JSON.parse(JSON.stringify(values))
    const { [record.id]: editData, ...rest } = newValues
    await weeklyReportDetailApi.postDetailModify({
      id: record.id,
      ...editData,
      ...rest,
    })
    message.success('更新成功')
    this.editIndex = -1
    this.projectTable.search()
  }
}

export default Store
