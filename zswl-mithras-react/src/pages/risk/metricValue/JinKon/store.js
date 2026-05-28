import { makeAutoObservable } from '@zswl/admin'
import { TableStore, Modal, ModalStore } from '@zswl/components'
import { message } from 'antd'
import moment from 'moment'
import Api from '@/api/risk/metricValue/controlGliy'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  others
  table = new TableStore({
    request: async (params) => {
      const result = await Api.postReportList({
        ...params,
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
        tradeDate: record?.tradeDate ? moment(record.tradeDate) : undefined,
      }
    },
    onFinish: async (values) => {
      const { id, tradeDate } = values
      values.tradeDate = tradeDate ? moment(tradeDate).format('yyyy-MM-DD') : undefined

      id
        ? await Api.postReportModify({
            ...values,
            id,
          })
        : await Api.postReportAdd(values)
      message.success('操作成功')
      this.createModal.close()
      this.table.search()
    },
  })

  submit = () => {
    const { rows } = this.table.getSelected()
    if (rows.length === 0) {
      message.info('请先在列表中选中')
      return
    }
    Modal.confirm({
      title: `请确认是否批量报送数据?`,
      onOk: async () => {
        await Api.postReportSubmit({ idList: rows.map((item) => item.id) })
        message.success('报送成功！')
        this.table.search()
      },
    })
  }
  delete = ({ id }) => {
    Modal.confirm({
      title: `请确认是否删除?`,
      onOk: async () => {
        await Api.postReportRemove({ id })
        message.success('删除成功！')
        this.table.search()
      },
    })
  }
}
export default new Store()
