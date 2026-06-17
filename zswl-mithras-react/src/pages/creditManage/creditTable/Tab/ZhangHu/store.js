import { TableStore } from '@zswl/components'
import { makeAutoObservable, setSessionStorage } from '@zswl/admin'
import { message } from 'antd'
import { timeFormat, hasValue } from '@/utils'
import {
  CREATETABLE_PARAMS,
  getHeaderWithFunctionCode,
} from '@/components/CreditManage/CreditTableConfig'
import Api from './api'
import _ from 'lodash'

class Store {
  constructor({ baseParams, baseStore }) {
    this.baseParams = baseParams
    this.baseStore = baseStore
    makeAutoObservable(this)
  }
  $table = new TableStore({
    request: async (params) => {
      const { page, pageSize, ...rest } = params
      setSessionStorage(CREATETABLE_PARAMS, _.isEmpty(rest) ? null : rest)
      const res = await Api.getList(
        {
          ...params,
          ...this.baseParams,
        },
        getHeaderWithFunctionCode({ channel: this.baseParams.channel, humpPath: 'crAccountList' })
      )

      return { ...res, list: res.list.map(({ id, ...rest }) => ({ id: id?.value ?? id, ...rest })) }
    },
  })
  // 列表编辑态索引
  editIndex = -1
  editable = false
  editItem = ({ rowIndex }) => {
    this.editIndex = rowIndex
    // setTimeout(() => {}, 0)
  }
  cancelEdit = () => {
    this.editIndex = -1
  }
  confirmEdit = async (record) => {
    await Api.modifyItem({
      id: record.id,
      ...record,
    })
    message.success('操作成功')
    this.$table.search()
  }
  onReportFlagChange = async (checked, record) => {
    await Api.reportChange({
      id: record.id,
      reportFlag: checked ? 1 : 0,
      module: 'ACCOUNT',
    }).finally(() => {})
    this.$table.search()
    message.success(`操作成功`)
  }
}
export default Store
