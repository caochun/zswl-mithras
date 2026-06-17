import { makeAutoObservable } from '@zswl/admin'
import { TableStore, Modal, ModalStore } from '@zswl/components'
import { message } from 'antd'
import moment from 'moment'
import Api from './api'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  others
  table = new TableStore({
    request: async (params) => {
      const result = await Api.list({
        ...params,
        dataMonth: params.dataMonth
          ? moment(params.dataMonth).format('yyyy-MM') + '-01'
          : undefined,
      })
      this.others = result.others
      return result
    },
  })

  current = {}
  createModal = new ModalStore({
    onOpen: (record) => {
      this.current = record
      return {
        ...record,
        dataMonth: record?.dataMonth ? moment(record.dataMonth) : undefined,
        bizStartDate: record?.bizStartDate ? moment(record.bizStartDate) : undefined,
        bizEndDate: record?.bizEndDate ? moment(record.bizEndDate) : undefined,
      }
    },
    onFinish: async (values) => {
      const { id, dataMonth, bizStartDate, bizEndDate } = values
      values.dataMonth = dataMonth ? moment(dataMonth).format('yyyy-MM') + '-01' : undefined
      values.bizStartDate = bizStartDate ? moment(bizStartDate).format('yyyy-MM-DD') : undefined
      values.bizEndDate = bizEndDate ? moment(bizEndDate).format('yyyy-MM-DD') : undefined
      id
        ? await Api.modify({
            ...values,
            id,
          })
        : await Api.add(values)
      message.success('操作成功')
      this.createModal.close()
      this.table.search()
    },
  })

  submit = () => {
    const { dataMonth } = this.table.getParams()
    const mouthStr = moment(dataMonth).format('yyyy-MM')
    Modal.confirm({
      title: `请确认是否批量报送${mouthStr}数据?`,
      onOk: async () => {
        await Api.report({
          dataMonth: mouthStr + '-01',
        })
        message.success('报送成功！')
        this.table.search()
      },
    })
  }
  delete = ({ id }) => {
    Modal.confirm({
      title: `请确认是否删除?`,
      onOk: async () => {
        await Api.remove({ id })
        message.success('删除成功！')
        this.table.search()
      },
    })
  }
}
export default new Store()
