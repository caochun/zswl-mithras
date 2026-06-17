import { makeAutoObservable, getQuery } from '@zswl/admin'
import { TableStore, ModalStore, Modal } from '@zswl/components'
import Api from './api'
import { getIsClientDetailParams } from '@/utils/customer'
import detailStore from '../../store'
import { message } from 'antd'
// const typeId = getQuery('typeId')
// const businessID = getQuery('businessKey')
class Store {
  constructor(id, businessVersion, startUserId) {
    this.clientId = id
    this.businessVersion = businessVersion
    this.startUserId = startUserId
    makeAutoObservable(this)
  }
  businessVersion
  //联系人信息
  linkManInfo = new TableStore({
    request: async (params) => {
      if (getQuery('typeId') == 'approval') {
        return await Api.getApprovalContactList({
          clientId: getQuery('businessKey'),
          businessVersion: this.businessVersion,
          startUserId: this.startUserId,
          ...params,
        })
      }
      return await Api.getContactList({
        clientId: this.clientId,
        ...params,
        ...getIsClientDetailParams(),
      })
    },
  })
  //新增联系人
  contactModal = new ModalStore({
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
        if (getQuery('typeId') == 'approval') {
          await Api.editContact({ clientId: getQuery('businessKey'), id, ...values })
        } else {
          await Api.editContact({ clientId: this.clientId, id, ...values })
        }
      } else {
        await Api.addContact({
          clientId: getQuery('typeId') == 'approval' ? getQuery('businessKey') : this.clientId,
          ...values,
        })
      }
      this.linkManInfo.search()
      this.contactModal.close()
      message.success('提交成功！')
    },
  })
  //删除联系人
  removeContact = ({ id }) => {
    Modal.confirm({
      title: '确认删除吗？',
      onOk: async () => {
        await Api.removeContact({ id: id?.value ?? id })
        this.linkManInfo.search()
        message.success('删除成功！')
      },
    })
  }
}
export default Store
