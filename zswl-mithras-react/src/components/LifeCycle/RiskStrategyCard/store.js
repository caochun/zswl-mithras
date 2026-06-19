import { makeAutoObservable } from '@zswl/admin'
import { ModalStore } from '@zswl/components'
import { message } from 'antd'
import Api from '@/api/lifeCycle/riskStrategyCardApi'

class Store {
  constructor(data) {
    makeAutoObservable(this)
    this.$CalcModalRef = data.$CalcModalRef
    this.clientId = data.clientId
    this.baseStore = data.baseStore
  }
  $cardModal = new ModalStore({
    onFinish: async (value) => {
      const tableData = this.$CalcModalRef?.current.getTableData()
      const targetScoreBodies = tableData.map(({ targetId, score, data }) => {
        return { targetId, score, data }
      })
      const hasEmptyInput = targetScoreBodies.some((item) => item.score === null)
      if (hasEmptyInput) {
        message.info('存在未选择项,请先选择')
        return
      }
      const { area, province, city, executiveLevel, regionalLevel } = this.tryData
      const params = {
        area,
        province,
        city,
        executiveLevel,
        regionalLevel,
        targetScoreBodies,
      }
      await Api.saveCardData(params)
      message.success('保存成功')
      this.$cardModal.close()
      this.getCardData()
      this.baseStore.forceUpdateId = +this.baseStore.forceUpdateId + 1
    },
  })

  // 获取计算结果
  cardData
  getCardData = async () => {
    const res = await Api.getCardData({ clientId: this.clientId })
    this.cardData = res
  }

  modalStatus
  // 获取card id
  cardId
  cardInfo = {}
  caclRiskvalue = async ({ clientId, clientInfo, modalStatus }) => {
    if (!clientId) {
      return message.info('暂无信息')
    }
    this.modalStatus = modalStatus
    const res = await Api.getCardId({
      clientId,
      suitTrade: clientInfo?.riskControlIndustryClassify,
    })
    const tryData = await this.tryCalcData(res)
    this.cardInfo = tryData
    this.cardId = tryData?.cardId
    this.$cardModal.open()
  }
  // 试计算
  tryData = {}
  tryCalcData = async (data) => {
    const res = await Api.postTryCalc(data)
    this.tryData = res
    return res
  }
}
export default Store
