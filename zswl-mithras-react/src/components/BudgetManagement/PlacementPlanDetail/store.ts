import deliveryPlanDetailApi from '@/api/budgetManagement/deliveryPlanDetailApi'
import deliveryPlanListApi from '@/api/budgetManagement/deliveryPlanListApi'
import processDetailApi from '@/api/budgetManagement/processDetailApi'
import { formatPercent, highPrecisionMultiply } from '@/utils/base'
import { makeAutoObservable } from '@zswl/admin'
import { Access, ModalStore, PageStore, TableStore } from '@zswl/components'
import { message } from 'antd'
import { throttle } from 'lodash'
import moment from 'moment'
import { monthStaticCard, staticNotMonthCard } from './staticData'

class Store {
  budgetPlanPayId: number
  taskActivityId: string
  constructor({ id, taskActivityId } = {}) {
    this.budgetPlanPayId = id
    this.taskActivityId = taskActivityId
    makeAutoObservable(this)
  }
  page = new PageStore({
    request: async (params) => {
      const res = await deliveryPlanListApi.postPayInfo(params)
      let processDetail = {}
      if (res.processInstanceId) {
        try {
          processDetail = await processDetailApi.getProcessDetail({
            processInstanceId: res.processInstanceId,
          })
        } catch (error) {
          console.log(error)
        }
      }
      return {
        ...processDetail,
        ...res,
        isMonth: ['MONTH', 'MONTH_ADJUST'].includes(res.budgetType),
      }
    },
  })
  departmentList = []
  getDepartmentList = async () => {
    const belongDeptId = this.page.getParams().deptId
    const res = await deliveryPlanDetailApi.postDeptConfirmList({
      belongDeptId,
      budgetPlanPayId: this.budgetPlanPayId,
    })
    this.departmentList = res
  }
  addItem = () => {
    const isMonth = this.page.getData()?.isMonth
    if (isMonth) {
      this.editModal.open()
    } else {
      this.detailModal.open()
    }
  }
  copyItem = async () => {
    const { keys } = this.projectTable.getSelected()
    await deliveryPlanDetailApi.postDetailCopy({ id: keys[0] })
    message.success('复制成功！')
    this.projectTable.search()

    this.refresh()
  }
  itemDelete = async () => {
    const { keys } = this.projectTable.getSelected()
    await deliveryPlanDetailApi.postDetailBatchDelete({ ids: keys })
    message.success('删除成功！')
    this.refresh()
  }

  refresh = async () => {
    await this.getDepartmentList()
    this.projectTable.search()
    this.getStatistics()
  }
  statistics = []
  getStatistics = async () => {
    const params = this.projectTable.getParams()
    const hasAccess = Access.validate(['budgetplanpaynotmonthdetailstatistics', 'budgetplanpaymonthdetailstatistics'])
    if (!hasAccess) return
    const { isMonth } = this.page.getData()

    if ([undefined, null].includes(isMonth)) return
    const func = isMonth ? deliveryPlanDetailApi.postDetailStatistics : deliveryPlanDetailApi.postNotMonthDetailStatistics
    const res = await func({
      ...params,
      budgetPlanPayId: this.budgetPlanPayId,
    })
    const staticCard = isMonth ? monthStaticCard : staticNotMonthCard
    this.statistics = staticCard.map((item) => ({
      ...item,
      value: res[item.dataIndex],
    }))
  }
  editModal = new ModalStore({
    onFinish: async (values) => {
      const budgetPlanPayId = this.page.getParams().id
      const res = await deliveryPlanDetailApi.postDetailAdd({ ...values, budgetPlanPayId })
      this.editModal.close()
      this.projectTable.search()
    },
  })
  detailModal = new ModalStore({
    onOpen: async (record = {}) => {
      const detail = { budgetPlanPayId: this.budgetPlanPayId, id: record.id }

      if (!record.id) return detail
      const baseInfo = await deliveryPlanDetailApi.postDetailBaseInfo({ id: record.id })
      const priceDetail = await deliveryPlanDetailApi.postDetailPrice({ id: record.id })

      return {
        ...detail,
        ...baseInfo,
        ...priceDetail,
        payDate: priceDetail.payDate && moment(priceDetail.payDate),
      }
    },
    onFinish: async (values) => {
      const res = await deliveryPlanDetailApi.postDetailAdd(values)
    },
  })
  projectTable = new TableStore({
    request: async (params) => {
      const budgetPlanPayId = this.budgetPlanPayId
      const { isMonth } = this.page.getData()
      if ([undefined, null].includes(isMonth)) return []
      const func = isMonth ? deliveryPlanDetailApi.postDetailPageList : deliveryPlanDetailApi.postNotMonthDetailPageList
      const { list, ...rest } = await func({
        ...params,
        budgetPlanPayId,
      })
      const isZiJinManager = ['userTask_headofzj', 'userTask_fundManager'].includes(this.taskActivityId)
      // const _list = list.map(item => ({...item,fundPlanPayAmount:formatPercent(item.planPayAmount, 100000000)}))
      return {
        list: isMonth
          ? list.map(({ contractCount, ...item }) => ({
              ...item,
              children: !!contractCount ? [] : undefined,
              fundPlanPayAmount: formatPercent(item.fundPlanPayAmount, isZiJinManager ? 10000 : 100000000),
            }))
          : list,
        ...rest,
      }
    },
  })
  expandRow = throttle(async (expanded, record) => {
    if (!expanded) return
    const list = this.projectTable.getList()
    const findItem = list.find((item) => item.id === record.id)
    if (findItem?.children?.length) return

    const res = await deliveryPlanDetailApi.postPageListContract({
      id: record.id,
    })
    const newChildren = res.map((item) => ({
      ...item,
      //子表格不展示
      fundPlanPayAmount: '-',
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

  // 校验拟投放金额
  checkPlanPayAmount = (id, val) => {
    if (val && typeof val === 'string') {
      const nVal = highPrecisionMultiply(val).split('.')[0]
      return deliveryPlanDetailApi.checkPlanPayAmount({ id, planPayAmount: nVal })
    }
  }

  // 2
  saveYunyingData = async (value) => {
    try {
      const res = await deliveryPlanDetailApi.postDetailModify(value)
    } catch (error) {}
  }

  confirmEdit = async ({ record, rowIndex }) => {
    const { values, list } = await this.projectTable.submit()
    const newValues = JSON.parse(JSON.stringify(values))
    const { [record.id]: editData, ...rest } = newValues
    try {
      const res = await deliveryPlanDetailApi.postDetailModify({
        id: record.id,
        ...editData,
        ...rest,
        fundPlanPayAmount: record.fundPlanPayAmount ? highPrecisionMultiply(String(record.fundPlanPayAmount).replace(/,/g, ''), 100000000).split('.')[0] : '',
      })
    } catch (error) {
      console.log(error)
      return
    }
    message.success('更新成功')
    this.editIndex = -1
    this.projectTable.search()
    this.getStatistics()
  }
}

export default Store
