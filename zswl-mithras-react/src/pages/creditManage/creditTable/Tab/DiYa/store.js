import { TableStore } from '@zswl/components'
import { makeAutoObservable, setSessionStorage } from '@zswl/admin'
import { message } from 'antd'
import { timeFormat } from '@/utils'
import {
  CREATETABLE_PARAMS,
  getHeaderWithFunctionCode,
} from '@/pages/creditManage/creditTable/Tab/config'
import Api from './api'
import _ from 'lodash'

class Store {
  constructor({ baseParams }) {
    this.baseParams = baseParams
    makeAutoObservable(this)
  }
  $table = new TableStore({
    request: (params) => {
      const { page, pageSize, ...rest } = params
      setSessionStorage(CREATETABLE_PARAMS, rest)

      return Api.getList(
        {
          ...params,
          ...this.baseParams,
        },
        getHeaderWithFunctionCode({
          channel: this.baseParams.channel,
          humpPath: 'crMortgageList',
        })
      )
    },
  })
  // 列表编辑态索引
  editIndex = -1
  editItem = ({ rowIndex }) => {
    this.editIndex = rowIndex
  }
  cancelEdit = () => {
    this.editIndex = -1
  }
  confirmEdit = async ({ record }) => {
    const { values } = await this.$table.submit()
    const editData = values[record.id]
    await Api.modifyItem({
      id: record.id,
      ...editData,
      isSend: editData.isSend === true ? 1 : 0,
      date: timeFormat(editData.date),
    })
    message.success('操作成功')
    this.editIndex = -1
    this.$table.search()
  }
  onReportFlagChange = async (checked, record) => {
    const id = record.id
    await Api.reportChange({
      id: _.isObject(id) ? id.value : id,
      reportFlag: checked ? 1 : 0,
      module: 'MORTGAGE',
    }).finally(() => {})
    this.$table.search()
    message.success(`操作成功`)
  }
}
export default Store
