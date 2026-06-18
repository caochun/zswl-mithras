import { makeAutoObservable } from '@zswl/admin'
import { ModalStore, TableStore, Modal } from '@zswl/components'
import { message } from 'antd'
import profitMeasurementApi from '@/api/budgetManagement/profitMeasurementApi'
import moment from 'moment'
import { refreshProvisionForecast } from '@/utils/domains/budgetManagement/BudgetManagementUtils'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  list = new TableStore({
    request: async (params) => {
      return await profitMeasurementApi.postProfitPageList(params)
    },
  })

  editModal = new ModalStore({
    onFinish: async (values) => {
      const res = await profitMeasurementApi.postProfitCreate({
        ...values,
        originBudgetPlanId: values.budgetPlanId,
      })
      message.success('保存成功！')
      this.list.search()
      refreshProvisionForecast()
      this.editModal.close()
    },
    onOpen: (values) => {
      if (!values) return {}
      return {
        editType: values.budgetPlanId ? 'EDIT' : 'CREATE',
        ...values,
        budgetType: 'MONTH_ADJUST',
        collectDateTo: values.collectDateTo && moment(values.collectDateTo),
        budgetDate:
          values.budgetDateFrom && values.budgetDateTo
            ? [moment(values.budgetDateFrom), moment(values.budgetDateTo)]
            : undefined,
        writeDate:
          values.writeDateFrom && values.writeDateTo
            ? [moment(values.writeDateFrom), moment(values.writeDateTo)]
            : undefined,
      }
    },
  })

  itemDelete = async (val) => {
    Modal.confirm({
      title: '请确认是否删除？',
      onOk: async () => {
        await profitMeasurementApi.postProfitRemove({ id: val.budgetPlanId })
        message.success('删除成功！')
        this.list.search()
      },
    })
  }
}

export default Store
