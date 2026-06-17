import { TableStore } from '@zswl/components'
import { makeAutoObservable, setSessionStorage } from '@zswl/admin'
import { message } from 'antd'
import { timeFormat } from '@/utils'
import {
  CREATETABLE_PARAMS,
  getHeaderWithFunctionCode,
} from '@/components/CreditManage/CreditTableConfig'
import Api from './api'

class Store {
  constructor({ baseParams, baseStore }) {
    this.baseParams = baseParams
    this.baseStore = baseStore
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
        getHeaderWithFunctionCode({ channel: this.baseParams.channel, humpPath: 'crRepayList' })
      )
      return {
        ...res,
        list: res.list.map(({ idKey, ...rest }) => ({ idKey: idKey?.value ?? idKey, ...rest })),
      }
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
    this.editIndex = -1
    this.$table.search()
  }
}
export default Store
