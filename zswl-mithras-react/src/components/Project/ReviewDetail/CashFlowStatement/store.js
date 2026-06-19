import { makeAutoObservable, getQuery } from '@zswl/admin'
import { TableStore } from '@zswl/components'
import { message } from 'antd'
import DataUpload from '@/components/DataUpload'
import Api from '@/api/project/component/ReviewDetail/CashFlowStatement/api'
import cashApi from '@/api/project/projectCashflow'
import { downFile } from '@/utils/downFunction'
class Store {
  constructor(props) {
    this.rootStore = props?.rootStore
    makeAutoObservable(this)
  }
  rootStore
  onlyOfficeParams
  projReviewId
  reportLoading = false
  table = new TableStore({
    request: async (params) => {
      if (!this.projReviewId) {
        return []
      }
      const isFormApproval = getQuery('typeId') == 'approval'

      const { processInstanceId, businessVersion, modelKey } = this.rootStore?.page.getParams()
      if (isFormApproval && processInstanceId) {
        const api =
          modelKey === 'ProjReviewCreateFlow'
            ? Api.reviewPricingCashflowCompare
            : Api.reviewCashflowCompare
        const res = await api({
          id: this.projReviewId,
          processInstanceId,
          businessVersion,
        })
        return res || []
      }
      const res = await Api.postCashFlowList({
        id: this.projReviewId,
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
        projReviewId: projectId,
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
    // if (!this.rootStore.QSShowValue) {
    //   message.info('报价方案未保存，请先保存！')
    //   return
    // }
    cashApi.postCashflowGenerate({ id: this.projReviewId }).then((res) => {
      this.table.search()
    })
  }
  download = async (projReviewId, filename) => {
    const res = await Api.postCashFlowUDownload({ projReviewId, filename })
    downFile(res)
  }
  exportRent = async (projReviewId, filename, businessVersion) => {
    await Api.postRentExport({ projReviewId, filename, businessVersion })
  }
  exportCashFlow = async (projReviewId, filename, businessVersion) => {
    await Api.postCashFlowExport({ projReviewId, filename, businessVersion })
  }
}
export default Store
