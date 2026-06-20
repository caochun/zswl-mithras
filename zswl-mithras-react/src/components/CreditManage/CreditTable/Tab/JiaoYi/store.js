import { TableStore } from '@zswl/components'
import { makeAutoObservable, setSessionStorage } from '@zswl/admin'
import { message } from 'antd'
import {
  CREATETABLE_PARAMS,
  getHeaderWithFunctionCode,
} from '../../../CreditTableConfig/CreditTableConfig'
import { specialTradeApi as Api } from '@/api/credit/creditTableTabApi'

class Store {
  constructor({ baseParams }) {
    this.baseParams = baseParams
    makeAutoObservable(this)
  }
  $table = new TableStore({
    request: async (params) => {
      const { page, pageSize, ...rest } = params
      setSessionStorage(CREATETABLE_PARAMS, rest)
      const res = await Api.getList(
        {
          ...params,
          ...this.baseParams,
        },
        getHeaderWithFunctionCode({
          channel: this.baseParams.channel,
          humpPath: 'crSpecialTradeList',
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
  confirmEdit = async (record) => {
    await Api.modifyItem(record)
    message.success('操作成功')
    this.$table.search()
  }
  onReportFlagChange = async (value, record) => {
    await Api.reportChange({
      id: record.id,
      reportFlag: value ? 1 : 0,
      module: 'SPECIAL_TRADE',
    })
    this.$table.search()
    message.success(`操作成功`)
  }
}
export default Store
