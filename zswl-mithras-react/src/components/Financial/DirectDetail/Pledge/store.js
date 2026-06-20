import { makeAutoObservable } from '@zswl/admin'
import { TableStore, ModalStore } from '@zswl/components'
import { timeFormat } from '@/utils'
import moment from 'moment'
import { message, Modal } from 'antd'
import Api from '@/api/financial/directFinancingDetail'
import { debounce as _debounce } from 'lodash'
import ApiFund from '@/api/financial/fundApi'

class Store {
  constructor({ businessVersion, isFormApproval, financingId, detail, baseStore }) {
    this.businessVersion = businessVersion
    this.isFormApproval = isFormApproval
    this.financingId = financingId
    this.detail = detail
    this.baseStore = baseStore
    makeAutoObservable(this)
  }
  baseStore = null
  $table = new TableStore({
    request: async (params) => {
      if (this.detail) return this.detail
      return Api.getPledgeList({ ...params, financingId: this.financingId })
    },
  })

  remove = (record) => {
    Modal.confirm({
      title: '是否删除？',
      onOk: async () => {
        await Api.delPledge({
          id: this.isFormApproval ? record.id.value : record.id,
        })
        message.success('删除成功')
        this.$table.search({ financingId: this.financingId })
        this.baseStore.repayRef?.current?.tableStore?.search()
      },
    })
  }

  getDetail = async (pledgeId) => {
    const res = await Api.getPledgeDetail({ id: pledgeId })
    return res
  }

  contractList = []
  getContractSearch = _debounce(async (val) => {
    const res = await ApiFund.postContractSearch({
      contractCode: val,
      removeLockFlag: 1
    })
    res && (this.contractList = res)
  }, 500)

  curItem = null
  $createModal = new ModalStore({
    onOpen: async (record) => {
      this.getContractSearch()
      if (record) {
        const id = record.id.value ?? record.id
        const res = await this.getDetail(id)
        this.curItem = res
        return {
          ...res,
          contractStartDate: res.contractStartDate && moment(res.contractStartDate),
          contractEndDate: res.contractEndDate && moment(res.contractEndDate),
          bizDeptId: res.bizDeptId,
          bizDeptName: res.bizDeptName,
          clientName: res.clientName,
          projName: res.projName,
          projReviewId: res.projReviewId,
          contractId: {
            label: res.contractCode,
            value: res.contractId,
          },
        }
      }else{
        const res = await Api.getDefaultValues()
        return {
          accountBank:res.accountBank,
          accountNumber:res.accountNumber,
          accountName:res.accountName
        }
      }
    },
    onFinish: async (values) => {
      const { id, projReviewId, projName, contractId, contractStartDate, contractEndDate } = values
      const params = {
        ...values,
        isPledge: !!values.isPledge,
        isSupervise: !!values.isSupervise,
        contractId: contractId.value,
        contractCode: contractId.label,
        projReviewId,
        projName,
        contractStartDate: contractStartDate ? timeFormat(contractStartDate) : undefined,
        contractEndDate: contractEndDate ? timeFormat(contractEndDate) : undefined,
      }
      id
        ? await Api.editPledge({
            id,
            financingId: this.financingId,
            ...params,
          })
        : await Api.addPledge({
            financingId: this.financingId,
            ...params,
          })
      message.success(id ? '修改成功' : '新增成功')
      this.$createModal.close()
      this.$table.search({ financingId: this.financingId })
      this.baseStore.repayRef?.current?.tableStore?.search?.()
    },
  })
}
export default Store
