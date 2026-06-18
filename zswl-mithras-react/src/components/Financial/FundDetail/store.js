import { makeAutoObservable, history } from '@zswl/admin'
import { PageStore, Modal } from '@zswl/components'
import Api from '@/api/financial/fundApi'
import { compareDetail, hasValue } from '@/utils'
import { message } from 'antd'
import approvalRemarkApi from '@/api/financial/approvalRemarkApi'

class Store {
  constructor(data) {
    this.financingId = data?.id
    makeAutoObservable(this)
  }

  page = new PageStore({
    request: async (params) => {
      let approvalDetail
      if (params?.approvalParams) {
        approvalDetail = await approvalRemarkApi
          .postFundFinancingRemarkAll(params.approvalParams)
          .then((res) => res)
          .catch((e) => {
            console.log(e)
          })
      }
      const { financingId, isFormApproval, businessVersion } = params
      if (!isFormApproval) {
        const res = await Api.postBaseDetail({
          financingId,
        })
        return { detail: res, approvalDetail }
      } else {
        const res = await Api.postBaseDetailCompare({
          financingId,
          businessVersion,
        })
        return { ...compareDetail(res), approvalDetail }
      }
    },
  })

  postProjectBaseInfoModify = async (params) => {
    const isYT = params.businessType === 'SYNDICATIONS'
    if (isYT) {
      const organizationInfoList = params?.organizationInfoList?.filter(Boolean) || []
      const idList = new Set([...organizationInfoList.map((v) => v.organizationId)])

      if (idList.size !== organizationInfoList.length) {
        message.error('融资机构不能重复')
        return Promise.reject('融资机构不能重复')
      }
      if (!idList.size) {
        message.error('融资机构不能为空')
        return Promise.reject('融资机构不能为空')
      }
    }

    await Api.postBaseDetailModify({ ...params, id: this.financingId })
    this.page.init()
    const { SchemoRef } = this.page.getParams()
    // 更新 融资方案数据
    SchemoRef?.current.reload()
  }

  changeLog = async (id) => {
    history.push(`/financial/fund/detail/log/${id}`)
  }

  submitApproval = async (id, version, onlyCheck, extParams = {}) => {
    if (onlyCheck) return await Api.postFlowEffect({ id, onlyCheck, version, ...extParams })
    await Api.postFlowEffect({ id, onlyCheck, version, ...extParams })
    await this.page.init()
    message.success('提交审批成功')
  }

  onSubmit = async () => {
    if (this.changeType === 'CHANGE_LPR') {
      await Api.postFlowLpr({
        financingId: this.financingId,
      })
      message.success('提交成功')
    } else if (this.changeType === 'CHANGE_EARLY_SETTLE') {
      await Api.postFlowEarlysettle({
        financingId: this.financingId,
      })
      message.success('提交成功')
    }
  }

  onCancel = async () => {
    Modal.confirm({
      title: `是否取消操作？`,
      onOk: async () => {
        await Api.postFlowCancel({
          financingId: this.financingId,
        })
        message.success('操作成功')
        setTimeout(() => {
          history.push(`/financial/fund`)
        }, 500)
      },
    })
  }
}
export default Store
