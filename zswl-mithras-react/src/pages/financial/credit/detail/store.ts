import { makeAutoObservable, history } from '@zswl/admin'
import { PageStore } from '@zswl/components'
import Api from '@/api/financial/creditManage'
import { compareDetail, formatPercent, hasValue, setDefaultFilter } from '@/utils'
import { message } from 'antd'
import { columnsFilterKey } from './UserDetail'

class Store {
  id: any
  constructor({ id }) {
    this.id = id
    makeAutoObservable(this)
  }

  page = new PageStore({
    request: async (params) => {
      console.log("112",params,'columnsFilterKey')
      const { id, isFormApproval } = params
      setDefaultFilter(columnsFilterKey, {
        usedTotalCreditAmount: false,
        usedGuaranteeAmount: false,
        usedCreditAmount: false,
      })
      if (!isFormApproval) {
        const res = await Api.postCreditDetail({
          id,
        })
        res.enhanceCreditMethod = res.enhanceCreditMethod ?? undefined

        return { detail: res }
      } else {
        const res = await Api.postInfoDetailCompare({
          groupCreditEstablishId: id,
        })
        return compareDetail(res)
      }

      return {}
    },
  })

  postProjectBaseInfoModify = async (params, callback) => {
    const guaranteeDetail = params?.guaranteeDetail || []
    const idList = new Set([...guaranteeDetail.map((v) => v.guaranteeAgencyId)])

    if (idList.size !== guaranteeDetail.length) {
      message.error('担保方不能重复')
      return Promise.reject('担保方不能重复')
    }
    const required = guaranteeDetail.some(
      (v) => !v.guaranteeAgencyId || !hasValue(v.guaranteeAmount)
    )
    if (required) {
      message.error('担保方和担保金额不能为空')
      return Promise.reject('担保方和担保金额不能为空')
    }
    // params.guaranteeDetail = guaranteeDetail.map((v) => {
    //   return {
    //     ...v,
    //     guaranteeAmount: math.toNonExponentialPlus(
    //       math.format(math.multiply(v.guaranteeAmount, 10000))
    //     ),
    //   }
    // })
    await Api.postCreditModify({ id: this.id, ...params })
    const detail = await Api.postCreditDetail({
      id: this.id,
    })
    this.page.setData({ detail })
  }
  //变更日志
  changeLog = async (bizType, id) => {
    history.push(`/credit/establish/detail/log/${id}`)
  }
  approvalLoading = false
  submitApproval = async (id) => {
    try {
      this.approvalLoading = true
      // await versionApi.postEstablishEffect({ id })
      // history.push('/credit/establish')
      message.success('提交审批成功')
      this.approvalLoading = false
    } catch (e) {
      this.approvalLoading = false
    }
  }
}
export default Store
