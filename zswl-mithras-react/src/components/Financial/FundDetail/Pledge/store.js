import { makeAutoObservable } from '@zswl/admin'
import { TableStore, ModalStore } from '@zswl/components'
import { compareDetail, timeFormat } from '@/utils'
import moment from 'moment'
import { message, Modal } from 'antd'
import Api from '@/api/financial/fundApi'
import { debounce as _debounce } from 'lodash'
import ApiFun from '@/api/financial/fundApi'

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
      if (this.isFormApproval) {
        return Api.postPledgeListCompare({
          ...params,
          financingId: this.financingId,
          businessVersion: this.businessVersion,
        })
      }
      return Api.postPledgeList({ ...params, financingId: this.financingId })
    },
  })

  remove = (record) => {
    Modal.confirm({
      title: '是否删除？',
      onOk: async () => {
        await Api.postPledgeDelete({
          pledgeId: this.isFormApproval ? record.id.value : record.id,
        })
        message.success('删除成功')
        this.$table.search({ financingId: this.financingId })
      },
    })
  }

  getDetail = async (pledgeId) => {
    const res = await Api.postPledgeDetail({ pledgeId })
    return res
  }

  contractList = []
  getContractSearch = _debounce(async (val) => {
    const res = await ApiFun.postContractSearch({
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
      const {
        id,
        projReviewId,
        projName,
        contractId,
        bizDeptId,
        contractStartDate,
        contractEndDate,
      } = values
      const params = {
        ...values,
        isPledge: !!values.isPledge,
        isSupervise: !!values.isSupervise,
        contractId: contractId.value,
        contractCode: contractId.label,
        bizDeptId,
        projReviewId,
        projName,
        contractStartDate: contractStartDate ? timeFormat(contractStartDate) : undefined,
        contractEndDate: contractEndDate ? timeFormat(contractEndDate) : undefined,
      }
      console.log('params: ', params)
      id
        ? await Api.postPledgeModify({
            id,
            financingId: this.financingId,
            ...params,
          })
        : await Api.postPledgeCreate({
            financingId: this.financingId,
            ...params,
          })
      message.success(id ? '修改成功' : '新增成功')
      this.$createModal.close()
      this.$table.search({ financingId: this.financingId })
    },
  })
}
export default Store
