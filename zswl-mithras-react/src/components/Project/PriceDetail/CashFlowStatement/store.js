import { makeAutoObservable, getQuery } from '@zswl/admin'
import { TableStore } from '@zswl/components'
import { message } from 'antd'
import DataUpload from '@/components/DataUpload'
import Api from '@/api/project/component/PriceDetail/CashFlowStatement/api'
import cashApi from '@/api/project/projectPriceCashflow'
import { downFile } from '@/utils/downFunction'

const format = (data) => {
  return (data ?? []).map((item) => {
    const { pricingDate, compareResult } = item
    const isChange = !!compareResult
    return {
      id: pricingDate.id,
      date: { value: pricingDate.date },
      phase: { value: pricingDate.phase, isChange },
      rent: { value: pricingDate.rent },
      cashFlowAmount: { value: pricingDate.cashFlowAmount },
      principal: { value: pricingDate.principal },
      interest: { value: pricingDate.interest, isChange },
      remainingPrincipal: { value: pricingDate.remainingPrincipal },
    }
  })
}

class Store {
  constructor(props) {
    this.rootStore = props?.rootStore
    makeAutoObservable(this)
  }
  rootStore
  onlyOfficeParams
  projPricingId
  reportLoading = false
  table = new TableStore({
    request: async (params) => {
      if (!this.projPricingId) {
        return []
      }
      const isFormApproval = getQuery('typeId') == 'approval'
      const { processInstanceId, modelKey, businessVersion } = this.rootStore?.page.getParams()
      if (isFormApproval && processInstanceId) {
        const isCreate = modelKey === 'ProjReviewPricingApprovalFlow'
        const api = isCreate ? Api.postMeetMinuteCompare : Api.reviewCashflowCompare

        const res = await api({ id: this.projPricingId, processInstanceId, businessVersion })
        return isCreate ? format(res) : res || []
      }
      const res = await Api.postCashFlowList({
        id: this.projPricingId,
      })
      return res || []
    },
  })
  beforeUpload = (file) => {
    if (!this.rootStore.QSShowValue) {
      message.info('报价方案未保存，请先保存！')
      return false
    }
  }
  onFileChange = async (file, projectId) => {
    const { fileList, config } = DataUpload.classify(file)
    const { code, msg } = await Api.postCashFlowUpload(
      {
        file: fileList[0],
        projPricingId: projectId,
      },
      config
    )
    if (code === 200) {
      message.success('上传成功')
      this.table.search()
    } else {
      message.info(msg)
    }
  }

  generate = () => {
    cashApi.postCashflowGenerate({ id: this.projPricingId }).then((res) => {
      this.table.search()
    })
  }
  download = async (projPricingId, filename) => {
    const res = await Api.postCashFlowUDownload({ projPricingId, filename })
    downFile(res)
  }
  exportRent = async (projPricingId, filename, businessVersion) => {
    await Api.postRentExport({ projPricingId, filename, businessVersion })
  }
  exportCashFlow = async (projPricingId, filename, businessVersion) => {
    await Api.postCashFlowExport({ projPricingId, filename, businessVersion })
  }
}
export default Store
