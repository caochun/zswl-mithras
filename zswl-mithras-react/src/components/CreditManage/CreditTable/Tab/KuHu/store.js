import { TableStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import { message } from 'antd'
import { timeFormat } from '@/utils'
import { getHeaderWithFunctionCode } from '../../../CreditTableConfig'
import Api from './api'

class Store {
  constructor({ baseParams }) {
    this.baseParams = baseParams
    makeAutoObservable(this)
  }
  $table = new TableStore({
    request: async (params) => {
      const res = await Api.getList(
        {
          ...params,
          ...this.baseParams,
        },
        getHeaderWithFunctionCode({
          channel: this.baseParams.channel,
          humpPath: 'crClientList',
        })
      )
      return { ...res, list: res.list.map(({ id, ...rest }) => ({ id: id?.value ?? id, ...rest })) }
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
    await Api.reportChange({
      id: record.id,
      reportFlag: checked ? 1 : 0,
      module: 'CLIENT',
    }).finally(() => {})
    this.$table.search()
    message.success(`操作成功`)
  }
}
export default Store
