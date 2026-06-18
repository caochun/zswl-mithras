import { makeAutoObservable, getQuery } from '@zswl/admin'
import { TableStore, ModalStore, Modal } from '@zswl/components'
import { getIsClientDetailParams } from '@/utils/domains/customer/CustomerUtils'
import Api from '@/api/customer/maintainApi'
import detailStore from '../../store'
import { message } from 'antd'
import moment from 'moment'
const dateFormat = 'yyyy-MM-DD'
class Store {
  constructor(id, businessVersion, startUserId) {
    this.clientId = id
    this.businessVersion = businessVersion
    this.startUserId = startUserId
    makeAutoObservable(this)
  }
  businessVersion
  //发债及评级
  bondRating = new TableStore({
    request: async (params) => {
      if (getQuery('typeId') == 'approval') {
        return await Api.getApprovalBondList({
          clientId: getQuery('businessKey'),
          businessVersion: this.businessVersion,
          startUserId: this.startUserId,
          ...params,
        })
      }
      return await Api.getBondList({
        clientId: this.clientId,
        ...params,
        ...getIsClientDetailParams(),
      })
    },
  })
  //新增发债及评级
  issueBondsModal = new ModalStore({
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
          obj2.rateDate = obj2.rateDate && moment(obj2.rateDate, dateFormat)
          return obj2
        } else {
          value.rateDate = value.rateDate && moment(value.rateDate, dateFormat)
          return value
        }
      }
    },
    onFinish: async (values, { id } = {}) => {
      if (id) {
        values.rateDate = values.rateDate && moment(values.rateDate).format('yyyy-MM-DD')
        await Api.editBondInfo({
          id,
          clientId: getQuery('typeId') == 'approval' ? getQuery('businessKey') : this.clientId,
          ...values,
        })
      } else {
        values.rateDate = values.rateDate && moment(values.rateDate).format('yyyy-MM-DD')
        await Api.addBondInfo({
          clientId: getQuery('typeId') == 'approval' ? getQuery('businessKey') : this.clientId,
          ...values,
        })
      }
      this.bondRating.search()
      this.issueBondsModal.close()
      message.success('提交成功！')
    },
  })
  //删除发债及评级
  removeBondInfo = ({ id }) => {
    Modal.confirm({
      title: '确认删除吗？',
      onOk: async () => {
        await Api.removeBondInfo({ id: id.value ?? id })
        this.bondRating.search()
        message.success('删除成功！')
      },
    })
  }
  //   //联系人信息
  //   linkManInfo = new TableStore({
  //     request: async (params) => {
  //       return await Api.getContactList({ clientId: this.clientId, ...params })
  //     },
  //   })
  //   //新增联系人
  //   contactModal = new ModalStore({
  //     onFinish: async (values, { id } = {}) => {
  //       if (id) {
  //         await Api.editContact({ clientId: this.clientId, id, ...values })
  //       } else {
  //         await Api.addContact({ clientId: this.clientId, ...values })
  //       }
  //       this.linkManInfo.search()
  //       this.contactModal.close()
  //       message.success('提交成功！')
  //     },
  //   })
  //   //删除联系人
  //   removeContact = ({ id }) => {
  //     Modal.confirm({
  //       title: '确认删除吗？',
  //       onOk: async () => {
  //         await Api.removeContact({ id })
  //         this.linkManInfo.search()
  //         message.success('删除成功！')
  //       },
  //     })
  //   }
}
export default Store
