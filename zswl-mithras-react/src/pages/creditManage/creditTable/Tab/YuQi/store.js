import { TableStore, Modal, ModalStore } from '@zswl/components'
import { makeAutoObservable, setSessionStorage } from '@zswl/admin'
import { message } from 'antd'
import { timeFormat, hasValue } from '@/utils'
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
          humpPath: 'crOverdueRecordList',
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
  deleteModal = new ModalStore({
    onFinish: async (values) => {
      const data = this.deleteModal.getInitialValues()
      await Api.deleteItem({ ...data, ...values })
      this.deleteModal.close()
      this.$table.search()
    },
  })
  deleteItem = async ({ record }) => {
    const id = record?.id?.value ?? record.id
    const businessKey = record?.businessKey?.value
    const label = record?.label?.value
    const title = label === 'REMOVE' ? '取消删除' : '删除'
    if (label === 'REMOVE') {
      Modal.confirm({
        title: `请确认是否${title}？`,
        onOk: async () => {
          await Api.cancelItem({ id, businessKey })
          message.success(`${title}成功`)
          this.$table.search()
        },
      })
    } else {
      this.deleteModal.open({ businessKey, id })
    }
  }
  onReportFlagChange = async (checked, record) => {
    await Api.reportChange({
      id: record.id,
      reportFlag: checked ? 1 : 0,
      module: 'OVERDUE_RECORD',
    }).finally(() => {})
    this.$table.search()
    message.success(`操作成功`)
  }
}
export default Store
