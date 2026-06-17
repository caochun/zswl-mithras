import { TableStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import DataUpload from '@/components/DataUpload'
import { message } from 'antd'
import { timeFormat, downFile, amountFormat, formatPercent } from '@/utils'
import Api from '@/api/financial/fundApi'
import { validateModal } from '@/utils/modal'

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
    request: async () => {
      if (this.detail) return this.detail
      if (this.isFormApproval) {
        const res = await Api.getEstimateListCompare({
          financingId: this.financingId,
          businessVersion: this.businessVersion,
        })
        return res
      }
      const res = await Api.getEstimateList({ financingId: this.financingId })
      return res
    },
  })

  importTable = async (files) => {
    console.log('  this.baseStore: ', this.baseStore)
    const { fileList } = DataUpload.classify(files)
    const params = { file: fileList[0], financingId: this.financingId }
    const { interestDiff } = await Api.postEstimateImportTable({
      ...params,
      isCheck: true,
    })
    const isMore = interestDiff > 10 * 10000
    await validateModal(
      {
        content: `导入的报价利息与合同利率计算结果差额为${amountFormat(
          formatPercent(interestDiff)
        )}元，请确认是否导入，点击【确认】继续导入，点击【取消】则关闭弹窗。`,
      },
      isMore
    )
    await Api.postEstimateImportTable({
      ...params,
      isCheck: false,
    })
    message.success('导入成功')
    this.baseStore?.page.getParams()?.SchemoRef.current.reload()
    this.$table.search()
  }

  postExportTable = async () => {
    const res = await Api.postEstimateExportTable({
      financingId: this.financingId,
      businessVersion: this.businessVersion,
    })
    if (res?.code === 200) {
      downFile(res)
      message.success('导出成功')
    } else if (res?.msg) {
      message.error(res.msg)
    }
  }

  modifyPlanDate = async (date) => {
    if (date) {
      await Api.postEstimatePlanDateModify({
        planLoanDate: timeFormat(date),
        financingId: this.financingId,
      })
      message.success('更新成功')
    }
  }
}
export default Store
