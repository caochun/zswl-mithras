import { makeAutoObservable } from '@zswl/admin'
import { message } from 'antd'
import mathjs from '@/utils/math'
import { compareDetail, formatPercent } from '@/utils'
import { bizTypePriceDetailMap, bizTypePriceModifyMap } from '../../bizTypeConfig'
import Api from '@/api/contract/priceApi'
import baseInfoApi from '@/api/contract/contractDetail'

class Store {
  constructor(data) {
    this.baseStore = data.baseStore
    this.businessVersion = data?.businessVersion
    this.contractId = data?.contractId
    this.isFormApproval = data?.isFormApproval
    this.baseForm = data?.baseForm
    this.setIrrIsChange = data?.setIrrIsChange
    makeAutoObservable(this)
  }

  bizType
  leaseTypes
  getBaseInfo = async (contractId) => {
    const res = await baseInfoApi.getBaseInfo({ id: contractId })
    this.bizType = res.bizType
    this.leaseTypes = res.leaseType
    Promise.resolve(res)
  }

  contractQSDetail = {}
  setContractQSDetail = (val) => {
    this.contractQSDetail = val
  }
  getContractQSDetail = async (contractId) => {
    if (this.isFormApproval) {
      const res = await Api.getContractQSDetailCompare({
        contractId,
        businessVersion: this.businessVersion,
      })
      const bizTypeData = res[bizTypePriceDetailMap[this.bizType]]
      const newData = compareDetail(bizTypeData)
      this.setContractQSDetail(newData)
      const { newDetail, isLog } = newData
      this.baseForm?.setFieldValue('irr', formatPercent(newDetail.irrPercent))
      this.baseForm?.setFieldValue('pricingIrrPercent', formatPercent(newDetail.pricingIrrPercent))
      this.setIrrIsChange?.(isLog.irrPercent)
      this.baseStore?.setPriceData?.(newDetail)
    } else {
      const res = await Api.getContractQSDetail({ contractId })
      const bizTypeData = res[bizTypePriceDetailMap[this.bizType]]
      this.setContractQSDetail({ detail: bizTypeData })
      this.baseForm?.setFieldValue('irr', formatPercent(bizTypeData.irrPercent))
      this.baseForm?.setFieldValue(
        'pricingIrrPercent',
        formatPercent(bizTypeData.pricingIrrPercent)
      )
      this.baseStore?.setPriceData?.(bizTypeData)
    }
  }
  getDetail = () => {
    return this.isFormApproval ? this.contractQSDetail.newDetail : this.contractQSDetail.detail
  }
  postContractQSModify = async (values) => {
    await Api.postContractQSModify({
      [bizTypePriceModifyMap[this.bizType]]: {
        ...values,
        id: this.getDetail().id,
        contractId: this.contractId,
      },
    })
    const { applyCreditAmount, earnestMoney, downPayment, consultingFee } = values
    const { projCreditAmount, projEarnestMoney, projDownPayment, projConsultingFee } =
      this.getDetail()

    const RATE = mathjs.format(mathjs.divide(applyCreditAmount, projCreditAmount / 10000))

    this.legal_earnestMoney = earnestMoney - (RATE * projEarnestMoney) / 10000 < 0
    this.legal_downPayment = downPayment - (RATE * projDownPayment) / 10000 < 0
    this.legal_consultingFee = consultingFee - (RATE * projConsultingFee) / 10000 < 0

    this.getContractQSDetail(this.contractId)
    this.baseStore.danBaoStore.$table.search()
  }

  parseNum = (val) => {
    return Number(val.toFixed(3))
  }

  handleCalc = (form) => {
    const { setFieldsValue, getFieldValue } = form
    const { projCreditAmount, projEarnestMoney, projDownPayment, projConsultingFee } =
      this.getDetail()
    const RATE = mathjs.format(
      mathjs.divide(getFieldValue('applyCreditAmount'), projCreditAmount / 10000)
    )
    setFieldsValue({
      earnestMoney: this.parseNum((RATE * projEarnestMoney) / 10000),
      downPayment: this.parseNum((RATE * projDownPayment) / 10000),
      consultingFee: this.parseNum((RATE * projConsultingFee) / 10000),
    })
  }

  lprLast = {}
  getLprLast = async () => {
    const res = await Api.getLprLast()
    this.lprLast = res
  }

  // 保证金
  legal_earnestMoney = false
  // 首期租金
  legal_downPayment = false
  // 手续费
  legal_consultingFee = false
}
export default Store
