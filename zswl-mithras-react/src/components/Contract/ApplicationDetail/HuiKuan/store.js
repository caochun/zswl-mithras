import { makeAutoObservable } from '@zswl/admin'
import { TableStore, ModalStore } from '@zswl/components'
import { message, Modal } from 'antd'
import { compareDetail } from '@/utils'
import Api from './api'

class Store {
  constructor(data) {
    this.isFormApproval = data?.isFormApproval
    this.businessVersion = data?.businessVersion
    this.contractId = data?.contractId
    this.bizType = data?.bizType
    makeAutoObservable(this)
  }

  AccountUseEnum = {
    BL: 'BLHK',
    ZR: 'ZRHK',
  }

  isCreate = true

  $table = new TableStore({
    pagination: false,
    request: (params) => {
      if (this.isFormApproval) {
        return Api.getListCompare({ ...params, businessVersion: this.businessVersion })
      }
      return Api.getList(params)
    },
  })

  remove = (record) => {
    Modal.confirm({
      title: '是否删除该账户？',
      onOk: async () => {
        await Api.removeItem({
          id: this.isFormApproval ? record.id.value : record.id,
          contractId: this.contractId,
        })
        message.success('删除成功')
        this.$table.search({
          contractId: this.contractId,
          accountUse: this.AccountUseEnum[this.bizType],
        })
      },
    })
  }

  $createModal = new ModalStore({
    onOpen: (record) => {
      this.isCreate = !record
      if (record) {
        if (this.isFormApproval) {
          return compareDetail.newDetail
        } else {
          return record
        }
      }
    },

    onFinish: async (values) => {
      const { repayWay, accountName, accountNum, accountAddress, id } = values
      id
        ? await Api.updateItem([
            {
              id,
              repayWay,
              accountName,
              accountNum,
              accountAddress,
              accountUse: this.AccountUseEnum[this.bizType],
              contractId: this.contractId,
            },
          ])
        : await Api.addItem({
            repayWay,
            accountName,
            accountNum,
            accountAddress,
            accountUse: this.AccountUseEnum[this.bizType],
            contractId: this.contractId,
          })
      message.success(id ? '修改成功' : '新增成功')
      this.$createModal.close()
      this.$table.search({
        contractId: this.contractId,
        accountUse: this.AccountUseEnum[this.bizType],
      })
    },
  })
}
export default Store
