import { TableStore, ModalStore, Modal } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import Api from '@/api/kpi/projProfit/projProfit'
import localApi from './api'
import { message } from 'antd'
import setApi from '@/api/kpi/baseSet/parameterConfig'
import kpiParameterConfigApi from '@/api/kpi/projProfit/kpiParameterConfigApi'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  $table = new TableStore({
    request: async (params) => {
      const data = await Api.postProjectprofitPagelist({ params })
      return data
    },
  })
  typeInfo = null
  $editModal = new ModalStore({
    onOpen: async (values) => {
      return values
    },
    onFinish: async (values) => {
      console.log(values)
    },
  })
  editItem = (record) => {
    this.typeInfo = record
    this.$editModal.open(record)
  }
  setModal = new ModalStore({
    onOpen: async (values = {}) => {
      let res = await kpiParameterConfigApi.postContractAssessDeptGet(values)
      if (res.length === 0) res = [{}]
      return { contractAssessDeptlDtoList: res }
    },
    onFinish: async (values) => {
      const initialValues = this.setModal.getInitialValues()
      const ids = initialValues.contractAssessDeptlDtoList
        .filter((item) => !values.contractAssessDeptlDtoList.find((v) => v.id === item.id))
        .map((v) => v.id)
      if (ids.length > 0) {
        await kpiParameterConfigApi.postContractAssessDeptDelete({ ids })
      }
      await kpiParameterConfigApi.postContractAssessDeptSave(values)
      message.success('保存成功')
      this.setModal.close()
    },
  })
  calculationModal = new ModalStore({
    onFinish: async (values) => {
      await Api.postProfitCalculation(values)
      message.success('计算成功')
      this.calculationModal.close()
      this.$table.search()
    },
  })

  paramsTable = new TableStore({
    request: async (params) => {
      const result = await setApi.getList({ ...params, queryFlag: '1' })
      return result
    },
  })

  handleConfirm = () => {
    Modal.confirm({
      title: '是否确认?',
      onOk: async () => {
        const { keys } = this.$table.getSelected()
        await localApi.postFinanceProjectprofitConfirm({
          projectProfitIdList: keys,
        })
        message.success('操作成功')
        this.$table.search()
      },
    })
  }
}
export default Store
