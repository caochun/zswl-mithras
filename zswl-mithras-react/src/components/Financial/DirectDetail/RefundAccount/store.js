import { makeAutoObservable } from '@zswl/admin'
import { TableStore, ModalStore } from '@zswl/components'
import { compareDetail, timeFormat } from '@/utils'
import { message, Modal } from 'antd'
import Api from '../api'
import moment from 'moment'

class Store {
  constructor({ businessVersion, isFormApproval, financingId, detail }) {
    this.businessVersion = businessVersion
    this.isFormApproval = isFormApproval
    this.financingId = financingId
    this.detail = detail
    makeAutoObservable(this)
  }

  $table = new TableStore({
    pagination: false,
    request: async (params) => {
      if (this.detail) return this.detail
      // if (this.isFormApproval) {
      //   return Api.postPayAccountListCompare({
      //     ...params,
      //     financingId: this.financingId,
      //     businessVersion: this.businessVersion,
      //   })
      // }
      return Api.getAccountList({ ...params, financingId: this.financingId })
    },
  })

  remove = (record) => {
    Modal.confirm({
      title: '是否删除？',
      onOk: async () => {
        await Api.delAccount({
          id: record.id?.value || record.id,
        })
        message.success('删除成功')
        this.$table.search({ financingId: this.financingId })
      },
    })
  }

  curItem = null
  $createModal = new ModalStore({
    onOpen: (record) => {
      if (record) {
        const result = this.isFormApproval ? compareDetail(record).newDetail : record
        this.curItem = result
        return {
          ...result,
          accountOpeningDate: result.accountOpeningDate
            ? moment(result.accountOpeningDate)
            : undefined,
          bankAccountId: {
            label: result.accountNumber,
            value: result.bankAccountId,
          },
        }
      }
    },
    onFinish: async (values) => {
      const { id, accountBank, bankAccountId, accountType, accountOpeningDate } = values
      const params = {
        accountBank,
        accountType,
        accountOpeningDate,
        accountOpeningDate: accountOpeningDate && timeFormat(accountOpeningDate),
        bankAccountId: bankAccountId.value,
        accountNumber: bankAccountId.label,
        financingId: this.financingId,
      }
      id
        ? await Api.editAccount({
            id,
            ...params,
          })
        : await Api.addAccount({
            ...params,
          })
      message.success(id ? '修改成功' : '新增成功')
      this.$createModal.close()
      this.$table.search({ financingId: this.financingId })
    },
  })
}
export default Store
