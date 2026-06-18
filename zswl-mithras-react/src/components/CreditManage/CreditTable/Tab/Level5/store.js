import { TableStore, ModalStore } from '@zswl/components'
import { makeAutoObservable, setSessionStorage } from '@zswl/admin'
import { message, Modal } from 'antd'
import { timeFormat } from '@/utils'
import {
  CREATETABLE_PARAMS,
  getHeaderWithFunctionCode,
} from '../../../CreditTableConfig'
import _ from 'lodash'
import Api from './api'

class Store {
  constructor({ baseParams }) {
    this.baseParams = baseParams
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
        getHeaderWithFunctionCode({
          channel: this.baseParams.channel,
          humpPath: 'crFiveClassList',
        })
      )
      return res
      return res.list.map(({ id, ...rest }) => ({ id: id?.value ?? id, ...rest }))
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

  createModal = new ModalStore({
    onFinish: async (values) => {
      const { identificationDate } = values
      await Api.addItem({
        ...values,
        identificationDate: identificationDate ? timeFormat(identificationDate) : undefined,
      })
      message.success('新增成功')
      this.createModal.close()
      this.editIndex = -1
      this.$table.search()
    },
  })
  deleteModal = new ModalStore({
    onFinish: async (values) => {
      const data = this.deleteModal.getInitialValues()
      await Api.deleteItem({ ...data, ...values })
      this.deleteModal.close()
      this.$table.search()
    },
  })
  deleteItem = async ({ record }) => {
    const id = record?.id?.value
    const businessKey = record?.businessKey?.value
    const label = record?.label?.value
    const title = label === 'REMOVE' ? '取消删除' : '删除'
    if (label === 'REMOVE') {
      Modal.confirm({
        title: `请确认是否${title}？`,
        onOk: async () => {
          await Api.deleteCancelItem({ id, businessKey })
          message.success(`${title}成功`)
          this.$table.search()
        },
      })
    } else {
      this.deleteModal.open({ businessKey, id })
    }
  }
}
export default Store
