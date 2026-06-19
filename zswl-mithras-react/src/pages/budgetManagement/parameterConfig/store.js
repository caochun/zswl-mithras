import { TableStore, ModalStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import { message } from 'antd'
import Api from '@/api/budgetManagement/parameterConfigApi'
import { uniqueId } from 'lodash'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  $table = new TableStore({
    request: async (params) => {
      const result = await Api.postSettingList({
        ...params,
      })
      const list = []
      Object.keys(result).map((key) => {
        let configValue =
          result[key].ftpConfigValue ||
          result[key].riskConfigValue ||
          result[key].expenseRatioConfigValue

        configValue = configValue.map((item, i) => {
          return { ...item, rowId: uniqueId() }
        })

        list.push({
          title: result[key].configName,
          createTime: result[key].updateTime,
          createByName: result[key].updateByName,
          list: configValue ?? [],
          id: result[key].id,
          rowId: uniqueId(),
        })
      })
      console.log('result', list)

      return list
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
}
export default Store
