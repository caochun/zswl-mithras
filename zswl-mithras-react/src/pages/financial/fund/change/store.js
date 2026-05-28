import { TableStore, PageStore } from '@zswl/components'
import { makeAutoObservable, history } from '@zswl/admin'
import { message, Modal } from 'antd'
import Api from '@/pages/financial/fund/api'

class Store {
  constructor(data) {
    this.financingId = data?.id
    this.businessVersion = data?.businessVersion
    this.isFormApproval = data?.isFormApproval
    this.changeType = data?.changeType
    makeAutoObservable(this)
  }

  page = new PageStore({
    request: async ({ changeType }) => {
      if (changeType === 'CHANGE_LPR') {
        const res = await this.getLprDetail()
        return { detail: res }
      } else if (changeType === 'CHANGE_EARLY_SETTLE') {
        const [detail, baseInfoDetail] = await Promise.all([
          this.getSettleDetail(),
          Api.postBaseDetail({
            financingId: this.financingId,
            businessVersion: this.businessVersion,
          }),
        ])
        return { detail, baseInfoDetail }
      }
      return {}
    },
  })
  saveBaseInfo = async (params) => {
    const { baseInfoDetail } = this.page.getData()
    await Api.postBaseDetailModify({ ...baseInfoDetail, ...params, id: this.financingId })
    this.page.init()
  }
  baseData = {}
  getBaseData = async () => {
    const res = await Api.postBaseDetail({
      financingId: this.financingId,
      businessVersion: this.businessVersion,
    })
    this.baseData = res
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

  detail = {}
  setDetail = (data) => {
    this.detail = data
  }
  getLprDetail = async () => {
    const res = await Api.postLprDetail({
      financingId: this.financingId,
      businessVersion: this.businessVersion,
    })
    const { oldData, newData } = res
    const formatOldData = {}
    Object.keys(oldData).map((key, index) => {
      formatOldData[`old_${key}`] = oldData[key]
    })
    const result = {
      ...newData,
      ...formatOldData,
    }
    this.setDetail(result)
    return result
  }
  getSettleDetail = async () => {
    const res = await Api.postSettleDetail({
      financingId: this.financingId,
      businessVersion: this.businessVersion,
    })
    this.setDetail(res)
    return res
  }
  saveLprData = async (values) => {
    const { lprAddPercent, lprRatePercent, lprType, interestRateType } = values
    await Api.postLprSave({
      financingId: this.financingId,
      lprAddPercent,
      lprRatePercent,
      lprType,
      interestRateType,
    })
    this.page.init()
  }
  saveSettleData = async (values) => {
    await Api.postSettleSave({
      financingId: this.financingId,
      id: this.detail.id,
      lastPrincipal: this.detail.lastPrincipal,
      ...values,
    })
    this.page.init()
  }

  saveData = async (values) => {
    if (this.changeType === 'CHANGE_LPR') {
      await this.saveLprData(values)
    } else if (this.changeType === 'CHANGE_EARLY_SETTLE') {
      await this.saveSettleData(values)
    }
  }

  $table = new TableStore({
    request: (params) => {
      return Api.postDataList({
        ...params,
        changeType: this.changeType,
        businessVersion: this.businessVersion,
      })
    },
  })
}
export default Store
