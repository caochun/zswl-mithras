import { makeAutoObservable, getQuery } from '@zswl/admin'
import { TableStore, ModalStore, Modal } from '@zswl/components'
import Api from './api'
import detailStore from '../../store'
import { message } from 'antd'
import { getIsClientDetailParams } from '@/utils/customer'
class Store {
  constructor(id, businessVersion, startUserId) {
    this.clientId = id
    this.startUserId = startUserId
    this.businessVersion = businessVersion
    makeAutoObservable(this)
  }
  businessVersion
  bankAccount = new TableStore({
    request: async (params) => {
      let clientType = getQuery('clientType')
      if (clientType == 'NORMAL') {
        if (getQuery('typeId') == 'approval') {
          return await Api.getApprovalNormalBankAccountList({
            clientId: getQuery('businessKey'),
            businessVersion: this.businessVersion,
            startUserId: this.startUserId,
            ...params,
          })
        }
        return await Api.getNormalBankAccountList({
          clientId: this.clientId,
          ...params,
          ...getIsClientDetailParams(),
        })
      }
      if (getQuery('typeId') == 'approval') {
        return await Api.getApprovalCorpBankAccount({
          clientId: getQuery('businessKey'),
          businessVersion: this.businessVersion,
          startUserId: this.startUserId,
          ...params,
        })
      }
      return await Api.getBackAccountList({
        clientId: this.clientId,
        ...params,
        ...getIsClientDetailParams(),
      })
    },
  })
  //新增银行账户
  bankAccountModal = new ModalStore({
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
    onFinish: async (values, { id }) => {
      let clientType = getQuery('clientType')
      if (clientType == 'NORMAL') {
        if (id) {
          await Api.editNormalBankAccount({
            id,
            clientId: getQuery('typeId') == 'approval' ? getQuery('businessKey') : this.clientId,
            ...values,
          })
        } else {
          await Api.addNormalBankAccount({
            ...values,
            clientId: getQuery('typeId') == 'approval' ? getQuery('businessKey') : this.clientId,
          })
        }
      }
      if (clientType == 'CORPORATION') {
        if (id) {
          await Api.editCorpBankAccount({
            id,
            clientId: getQuery('typeId') == 'approval' ? getQuery('businessKey') : this.clientId,
            ...values,
          })
        } else {
          await Api.addCorpBankAccount({
            ...values,
            clientId: getQuery('typeId') == 'approval' ? getQuery('businessKey') : this.clientId,
          })
        }
      }

      this.bankAccount.search()
      this.bankAccountModal.close()
      message.success('提交成功！')
    },
  })
  //删除银行卡号
  removeBankAccount = ({ id }) => {
    let clientType = getQuery('clientType')
    Modal.confirm({
      title: '确认删除吗？',
      onOk: async () => {
        if (clientType == 'NORMAL') {
          await Api.removeNormalBankAccount({ id: id?.value ?? id })
        } else {
          await Api.removeCorpBankAccount({ id: id?.value ?? id })
        }
        this.bankAccount.search()
        message.success('删除成功！')
      },
    })
  }
}
export default Store
