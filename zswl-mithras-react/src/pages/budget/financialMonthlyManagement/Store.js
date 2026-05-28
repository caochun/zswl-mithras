import { makeAutoObservable } from '@zswl/admin'
import { ModalStore, TableStore, DrawerStore, Modal } from '@zswl/components'
import { message } from 'antd'
import moment from 'moment'
import thirdCanqiongApi from '@/api/budget/flowCenter/thirdCanqiongApi'
import Api from '@/api/budget/monthlyManagement/monthlyManagementApi'

//  "实际利率法":"AIR",  "剩余本金法":"RP","印花税-项目端":"STAMP_DUTY_PROJ",  "印花税-资金端":"STAMP_DUTY_FIN", "成本计提":"COST",
const tableStoreMap = {
  AIR: 'incomeConfirmTable',
  RP: 'incomeProvisionTable',
  STAMP_DUTY_PROJ: 'stampDutyProjTable',
  STAMP_DUTY_FIN: 'stampDutyFinTable',
  COST: 'costRecoveryTable',
}
const sourceTableStoreMap = {
  ASSET_SIDE_AIR_ACCOUNT: 'incomeConfirmTable',
  ASSET_SIDE_PR_ACCOUNT: 'incomeProvisionTable',
  ASSET_SIDE_COST_STAMP_DUTY: 'stampDutyProjTable',
  FINANCE_SIDE_COST_STAMP_DUTY: 'stampDutyFinTable',
  ASSET_SIDE_COST_DK: 'costRecoveryTable',
}
class Store {
  constructor() {
    makeAutoObservable(this)
  }
  monthlyTable = new TableStore({
    request: async (params) => {
      return await Api.postListPage(params)
    },
  })

  // 选择月份
  monthValue = ''
  currentId = null
  chooseMonthModal = new ModalStore({
    onFinish: async (values) => {
      const { month } = values
      this.monthValue = moment(month).format('YYYY-MM')

      const id = await Api.postBaseAdd({
        yearAndMonth: this.monthValue,
      })
      this.monthlyTable.search()
      this.currentId = id
      this.chooseMonthModal.close()
      this.createDrawer.open({ editType: 'create' })
    },
  })
  openDetail = async (record) => {
    const editTypeMap = {
      NOT_CONFIRM: 'create',
      CONFIRMED: 'edit',
      CLOSED_AMOUNT: 'read',
    }
    const editType = editTypeMap[record.status]
    this.currentId = record.id //
    this.monthValue = record.yearAndMonth
    if (editType === 'create') {
      await this.update()
    }
    this.createDrawer.open({ editType })
  }
  // 月结面板
  createDrawer = new DrawerStore({})

  currentStep = 0
  setCurrentStep = (value) => {
    this.currentStep = value
  }

  exportExcel = async (params) => {
    // AIR("实际利率法"),
    // RP("剩余本金法"),
    // COST("计提成本"),
    // STAMP_DUTY_PROJ("印花税-项目端"),
    // STAMP_DUTY_FIN("印花税-资金端"),
    await Api.postMonthlyDownload({
      yearAndMonth: this.monthValue,
      ...params,
      pageSize: 5000,
    })
  }

  // 收入确认
  incomeConfirmTable = new TableStore({
    request: async (params) => {
      return await Api.postAirPage({
        ...params,
        yearAndMonth: this.monthValue,
      })
    },
  })

  // 收入计提
  incomeProvisionTable = new TableStore({
    request: async (params) => {
      return await Api.postRpPage({
        ...params,
        yearAndMonth: this.monthValue,
      })
    },
  })

  // 成本计提
  costRecoveryTable = new TableStore({
    request: async (params) => {
      return await Api.postCostList({
        ...params,
        yearAndMonth: this.monthValue,
      })
    },
  })

  // 印花税计提-项目端
  stampDutyProjTable = new TableStore({
    request: async (params) => {
      return await Api.postProjPage({ ...params, yearAndMonth: this.monthValue })
    },
  })
  // 印花税计提-资金端
  stampDutyFinTable = new TableStore({
    request: async (params) => {
      return await Api.postFinPage({ ...params, yearAndMonth: this.monthValue })
    },
  })
  receiptModal = new ModalStore({})
  // 完成
  confirmSubmit = async () => {
    await Api.postMonthlySubmit({
      yearAndMonth: this.monthValue,
    })
    this.createDrawer.close()
    message.success('创建成功')
    this.setCurrentStep(0)
    this.monthlyTable.search()
  }
  close = async () => {
    const { rows, keys } = this.monthlyTable.getSelected()
    const mainId = keys?.[0]
    const { yearAndMonth } = rows?.[0]
    const res = await Api.postCloseValidate({ mainId })
    Modal.confirm({
      content: `您将对${yearAndMonth}进行关账，请再次确认！`,
      onOk: async () => {
        await Api.postMonthlyClose({ mainId })
        this.monthlyTable.search()
        message.success('关账成功')
      },
    })
  }
  reverse = async (record) => {
    const { id, source } = record
    const res = await thirdCanqiongApi?.postFinancialWithdraw([
      { platform: 'CQ2_ACCOUNT_APPLICATION', source, businessKey: id },
    ])
    const { success, message: msg } = res?.[0] ?? {}
    if (success) {
      this[sourceTableStoreMap[source]].search()
      message.success('操作成功')
    } else {
      message.error(msg)
    }
  }
  changeStatus = async (record) => {
    const { id: recordId, isEffect, tabType } = record
    await Api.postUpdateStatus({
      mainId: this.currentId,
      recordId,
      status: isEffect ? 0 : 1,
      tabType,
    })
    this[tableStoreMap[tabType]]?.search?.()
  }
  update = async (record, source) => {
    await Api.postMonthlyFresh({ mainId: this.currentId })
    this[sourceTableStoreMap[source]]?.search?.()
    message.success('更新成功')
  }
  updateRecord = async (record) => {
    const { id, source, tabType } = record
    await Api.postUpdateSingle({ mainId: this.currentId, singleRecordId: id, tabType })
    this[tableStoreMap[tabType]]?.search?.()
    message.success('更新成功')
  }
  pushSingle = async (record) => {
    const { id, source, tabType } = record
    await Api.postPushSingle({ mainId: this.currentId, tabType, id })
    this[tableStoreMap[tabType]]?.search?.()
    message.success('推送成功')
  }
}
export default Store
