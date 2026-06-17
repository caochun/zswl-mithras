import { makeAutoObservable, getQuery } from '@zswl/admin'
import { TableStore, ModalStore, Modal } from '@zswl/components'
import { getIsClientDetailParams } from '@/utils/customer'
import Api from './api'
import { message } from 'antd'
class Store {
  constructor(id, businessVersion, startUserId) {
    this.clientId = id
    this.businessVersion = businessVersion
    this.startUserId = startUserId
    makeAutoObservable(this)
  }
  businessVersion
  //配偶信息
  spouseInfo = new TableStore({
    request: async (params) => {
      if (getQuery('typeId') == 'approval') {
        return await Api.getApprovalSpouseList({
          clientId: getQuery('businessKey'),
          businessVersion: this.businessVersion,
          startUserId: this.startUserId,
          ...params,
        })
      }
      return await Api.getSpouseList({
        clientId: getQuery('typeId') == 'approval' ? getQuery('businessKey') : this.clientId,
        ...params,
        ...getIsClientDetailParams(),
      })
    },
  })
  spouseList = []
  spouseData = {}
  spouseSelect = async (e) => {
    this.spouseList = await Api.spouseSelect({
      clientId: getQuery('typeId') == 'approval' ? getQuery('businessKey') : this.clientId,
      fuzzyName: e,
    })
  }
  changeSpouse = async (e) => {
    this.spouseData = await Api.naturalDetail({ clientId: e })
  }
  //新增配偶信息
  spouseModal = new ModalStore({
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
          obj2.spouseName = `${obj2.spouseName}+${obj2.certNumber}`
          return obj2
        } else {
          value.spouseName = `${value.spouseName}+${value.certNumber}`
          return value
        }
      }
    },
    onFinish: async (values, { id } = {}) => {
      // this.spouseModal.getFormStore
      if (id) {
        await Api.editSpouse({
          clientId: getQuery('typeId') == 'approval' ? getQuery('businessKey') : this.clientId,
          id,
          ...values,
          spouseName: this.spouseData.clientName,
        })
      } else {
        await Api.addSpouse({
          clientId: getQuery('typeId') == 'approval' ? getQuery('businessKey') : this.clientId,
          ...values,
          spouseName: this.spouseData.clientName,
        })
      }
      this.spouseInfo.search()
      this.spouseModal.close()
      message.success('提交成功！')
    },
  })
  //删除配偶
  deleteSpouse = ({ id }) => {
    Modal.confirm({
      title: '确认删除吗？',
      onOk: async () => {
        await Api.deleteSpouse({ id: id?.value || id })
        this.spouseInfo.search()
        message.success('删除成功！')
      },
    })
  }
}
export default Store
