import { makeAutoObservable } from '@zswl/admin'
import { TableStore, ModalStore } from '@zswl/components'
import { compareDetail, timeFormat } from '@/utils'
import moment from 'moment'
import { message, Modal } from 'antd'
import Api from '@/api/financial/fundApi'
import fundCostDetailApi from '@/api/financial/fund/fundCostDetailApi'

class Store {
  constructor({ businessVersion, isFormApproval, financingId, detail, baseStore }) {
    this.businessVersion = businessVersion
    this.isFormApproval = isFormApproval
    this.financingId = financingId
    this.detail = detail
    this.baseStore = baseStore
    makeAutoObservable(this)
  }

  $table = new TableStore({
    pagination: false,
    request: async (params) => {
      // if (this.isFormApproval) {
      //   return Api.postPledgeListCompare({
      //     ...params,
      //     financingId: this.financingId,
      //     businessVersion: this.businessVersion,
      //   })
      // }
      return fundCostDetailApi.postFeeList({ ...params, financingId: this.financingId })
    },
  })

  remove = (record) => {
    Modal.confirm({
      title: '是否删除？',
      onOk: async () => {
        await fundCostDetailApi.postFeeRemove({
          id: record.id,
        })
        message.success('删除成功')
        this.$table.search({})
        this.baseStore.page.getParams()?.SchemoRef.current.reload()
      },
    })
  }

  getDetail = async (pledgeId) => {
    const res = await Api.postPledgeDetail({ pledgeId })
    return res
  }

  curItem = null
  $createModal = new ModalStore({
    onOpen: async (record) => {
      if (record) {
        const { organizationName, organizationId, payDate } = record
        return {
          ...record,
          payDate: payDate && moment(payDate),
          organization: {
            value: organizationId,
            label: organizationName,
          },
        }
      } else {
        return {
          organization: {
            value: this.detail?.organizationId?.[0],
            label: this.detail?.organizationName?.[0],
          },
        }
      }
    },
    onFinish: async (values) => {
      const { id, organization, ...params } = values
      params.organizationId = organization?.value
      params.organizationName = organization?.label
      id
        ? await fundCostDetailApi.postFeeModify({
            id,
            financingId: this.financingId,
            ...params,
          })
        : await fundCostDetailApi.postFeeAdd({
            financingId: this.financingId,
            ...params,
          })
      message.success(id ? '修改成功' : '新增成功')
      this.$createModal.close()
      this.$table.search({})
      this.baseStore.page.getParams()?.SchemoRef.current.reload()
    },
  })
}
export default Store
