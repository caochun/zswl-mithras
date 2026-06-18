import { makeAutoObservable, getQuery } from '@zswl/admin'
import { TableStore, ModalStore, Modal } from '@zswl/components'
import { getIsClientDetailParams } from '@/customer/CustomerUtils'
import Api from '@/api/customer/maintainApi'
import { message } from 'antd'
// const typeId = getQuery('typeId')
// const businessID = getQuery('businessKey')
class Store {
  constructor(id, businessVersion, startUserId) {
    // this.clientId = id
    // this.businessVersion = businessVersion
    // this.startUserId = startUserId
    makeAutoObservable(this)
  }
  businessVersion
  //股东信息
  shareholder = new TableStore({
    request: async (params, { sorter }) => {
      if (getQuery('typeId') == 'approval') {
        return await Api.getApprovalShareholderList({
          clientId: getQuery('businessKey'),
          orderField: sorter.columnKey,
          orderType: sorter.order,
          businessVersion: this.businessVersion,
          startUserId: this.startUserId,
          ...params,
        })
      }
      return await Api.getShareholderList({
        clientId: this.clientId,
        orderField: sorter.columnKey,
        orderType: sorter.order,
        ...params,
        ...getIsClientDetailParams(),
      })
    },
  })
  //新增股东信息
  shareholdersModal = new ModalStore({
    onOpen(value) {
      if (value) {
        if (getQuery('typeId') == 'approval') {
          let obj2 = {}
          let obj1 = Object.keys(value).map((key) => {
            return { [key]: value[key]?.value }
          })
          obj1.forEach((v) => {
            obj2 = Object.assign(obj2, v)
          })
          return obj2
        } else {
          return value
        }
      }
    },
    onFinish: async (values, { id } = {}) => {
      if (id) {
        await Api.editShareholder({
          clientId: getQuery('typeId') == 'approval' ? getQuery('businessKey') : this.clientId,
          id,
          ...values,
        })
        this.shareholdersModal.close()
      } else {
        await Api.addShareholder({
          clientId: getQuery('typeId') == 'approval' ? getQuery('businessKey') : this.clientId,
          ...values,
        })
      }
      this.shareholder.search()
      this.shareholdersModal.close()
      message.success('提交成功！')
    },
  })
  //删除股东信息
  removeShareholder = ({ id }) => {
    Modal.confirm({
      title: '确认删除吗？',
      onOk: async () => {
        await Api.removeShareholder({ id: id?.value ?? id })
        this.shareholder.search()
        message.success('删除成功！')
      },
    })
  }
}
export default new Store()
