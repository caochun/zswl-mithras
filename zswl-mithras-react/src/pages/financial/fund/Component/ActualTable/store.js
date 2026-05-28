import { makeAutoObservable } from '@zswl/admin'
import { message } from 'antd'
import DataUpload from '@/components/DataUpload'
import { amountFormat, downFile, formatPercent, timeFormat } from '@/utils'
import { TableStore } from '@zswl/components'
import Api from '@/pages/financial/fund/api'
import { validateModal } from '@/pages/budget/flowCenter/BankFlow/SpecialPayFlowListModal'

class Store {
  constructor({ isFormApproval, scene, financingId, businessVersion, sourceData, baseStore }) {
    this.isFormApproval = isFormApproval
    this.financingId = financingId
    this.businessVersion = businessVersion
    this.scene = scene
    this.sourceData = sourceData
    this.baseStore = baseStore
    makeAutoObservable(this)
  }

  $table = new TableStore({
    // pagination: false,
    request: async () => {
      if (this.sourceData) return this.sourceData
      if (this.isFormApproval) {
        const res = await Api.getActualListCompare({
          financingId: this.financingId,
          businessVersion: this.businessVersion,
        })
        return res
      } else {
        const res = await Api.getActualList({ financingId: this.financingId })
        return res
      }
    },
  })

  importTable = async (files) => {
    const { fileList } = DataUpload.classify(files)
    const params = { file: fileList[0], financingId: this.financingId, scene: this.scene }
    const { interestDiff } = await Api.postActualImportTable({
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
    await Api.postActualImportTable({
      ...params,
      isCheck: false,
    })
    message.success('导入成功')
    this.$table.search()
    this.baseStore?.page.getParams()?.SchemoRef.current.reload?.()
  }
  postExportTable = async () => {
    const res = await Api.postActualExportTable({
      financingId: this.financingId,
      businessVersion: this.businessVersion,
      scene: this.scene,
    })
    if (res?.code === 200) {
      downFile(res)
      message.success('导出成功')
    } else if (res?.msg) {
      message.error(res.msg)
    }
  }

  modifyActualDate = async (date) => {
    if (date) {
      await Api.postActualLoanDateModify({
        actualLoanDate: timeFormat(date),
        financingId: this.financingId,
      })
      message.success('更新成功')
    }
  }
}
export default Store
