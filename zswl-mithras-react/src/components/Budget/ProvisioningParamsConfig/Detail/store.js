import { PageStore, TableStore } from '@zswl/components'
import { makeAutoObservable, history, getQuery } from '@zswl/admin'
import { message } from 'antd'
import EclBusinessApi from '@/api/budget/provisioning/eclBusinessApi'
import { uniqueId } from 'lodash'
import mathjs from '@/utils/math'

const getEnum = ({ configValue, configEnum }) => {
  let enums = {}
  try {
    const parsed = JSON.parse(configValue)
    const { enums: _enums } = JSON.parse(configEnum) || {}

    const { data } = parsed || {}

    if (_enums) {
      enums = Object.entries(_enums)
        .map(([key, value]) => [key, value.map((item) => ({ label: item, value: item }))])
        .reduce((prev, cur) => ({ ...prev, [cur[0]]: cur[1] }), {})
    }
    return { data, enums, _enums }
  } catch (error) {
    return { data: [], enums }
  }
}
class Store {
  constructor() {
    makeAutoObservable(this)
  }

  page = new PageStore({
    request: async (params) => {
      const { id } = params
      const isVersion = getQuery('view') === 'version'
      const func = isVersion ? EclBusinessApi.postVersionDetail : EclBusinessApi.postConfigDetail
      const { configValue, configEnum, ...rest } = await func({
        id,
      })
      const { data, enums, _enums } = getEnum({ configValue, configEnum })
      setTimeout(() => {
        this.$table.setList(data.map((item) => ({ ...item, id: uniqueId() })))
      }, 100)
      return { ...rest, enums, _enums }
    },
  })

  $table = new TableStore({
    pagination: false,
    request: async () => [],
  })

  save = async () => {
    const { id, configModule, configCode, configName, configValue, configVersion } =
      this.page.getData() || {}
    const { list, values } = await this.$table.submit()
    const columns = this.$table.getOptimizedColumns()
    const data = list.map((item) => {
      const row = {}
      columns.forEach((column) => {
        const { dataIndex, initFormat } = column
        if (initFormat) {
          row[dataIndex] = mathjs.format(mathjs.multiply(item[dataIndex], initFormat))
        } else {
          row[dataIndex] = item[dataIndex]
        }
      })
      return row
    })
    await EclBusinessApi.postConfigModify({
      id,
      configModule,
      configCode,
      configName,
      configValue: JSON.stringify({ data }),
      configVersion,
    })
    message.success('操作成功')
    history.push(`/budget/provisioning/paramsConfig?type=reload`)
  }
}
export default Store
