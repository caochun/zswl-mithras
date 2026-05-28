import { makeAutoObservable } from '@zswl/admin'
import { TableStore, ModalStore } from '@zswl/components'
import { message, Modal } from 'antd'
import { compareDetail, compareTableData } from '@/utils'
import Api from './api'

class Store {
  constructor(data) {
    this.isFormApproval = data?.isFormApproval
    this.businessVersion = data?.businessVersion
    this.contractId = data?.contractId
    this.bizType = data?.bizType
    makeAutoObservable(this)
  }

  isCreate = true

  AccountUseEnum = {
    BL: 'BLSK',
    ZR: 'ZRSK',
    ZL: 'ZLSK',
    ZZ: 'ZZSK',
  }

  $table = new TableStore({
    pagination: false,
    request: (params) => {
      if (this.isFormApproval) {
        const result = Api.getListCompare({ ...params, businessVersion: this.businessVersion })
        return result
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
      const { clientName, accountName, accountNum, accountAddress, payeeType, bankAccountId, id } =
        values
      id
        ? await Api.updateItem([
            {
              id,
              clientName,
              accountName,
              accountNum,
              accountAddress,
              payeeType,
              bankAccountId,
              accountUse: this.AccountUseEnum[this.bizType],
              contractId: this.contractId,
            },
          ])
        : await Api.addItem({
            clientName,
            accountName,
            accountNum,
            accountAddress,
            payeeType,
            bankAccountId,
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
