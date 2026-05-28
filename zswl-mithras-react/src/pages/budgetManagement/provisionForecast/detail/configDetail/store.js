import { TableStore, PageStore, Modal } from '@zswl/components'
import { makeAutoObservable, history, getQuery } from '@zswl/admin'
import { message } from 'antd'
import { uniqueId } from 'lodash'
import predictConfigApi from '@/api/budget/provisioning/predictConfigApi'

const getEnum = ({ configValue, configEnum }) => {
  let enums = {}
  try {
    const parsed = JSON.parse(configValue)
    const { enums: _enums } = JSON.parse(configEnum) || {}

    const { data } = parsed || {}

    if (_enums) {
      enums = Object.entries(_enums)
        .map(([key, value]) => [
          key,
          value.map((item) => ({
            label: item,
            value: item,
          })),
        ])
        .reduce((prev, cur) => ({ ...prev, [cur[0]]: cur[1] }), {})
    }
    return { enums, data }
  } catch (error) {
    console.log('error: ', error)
    return { enums, data: [] }
  }
}
class Store {
  constructor() {
    makeAutoObservable(this)
  }

  page = new PageStore({
    request: async (params) => {
      const { id } = params
      const func = predictConfigApi.postConfigDetail
      const { configValue, configEnum, ...rest } = await func({
        id,
      })
      const { enums, data } = getEnum({ configValue, configEnum })
      setTimeout(() => {
        this.$table.setList(data.map((item) => ({ ...item, id: uniqueId() })))
      }, 100)
      return { ...rest, enums }
    },
  })

  $table = new TableStore({
    pagination: false,
    request: async (params) => {
      const { id } = this.page.getParams()
      return []
    },
  })

  save = async () => {
    const { id, configModule, configCode, configName, configValue, configVersion, enums } =
      this.page.getData() || {}
    const { list, values } = await this.$table.submit()
    const columns = this.$table.getOptimizedColumns()
    const data = list.map((item) => {
      const row = {}
      columns.forEach((column) => {
        const { dataIndex, initFormat } = column
        if (initFormat) {
          row[dataIndex] = item[dataIndex] * initFormat
        } else {
          row[dataIndex] = item[dataIndex]
        }
      })
      return row
    })
    await predictConfigApi.postConfigModify({
      id,
      configModule,
      configCode,
      configName,
      configValue: JSON.stringify({ data, enums }),
      configVersion,
    })
    message.success('操作成功')
    history.goBack()
  }
}
export default Store
