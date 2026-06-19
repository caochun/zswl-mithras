import { makeAutoObservable } from '@zswl/admin'
import { hasValue, compareDetail } from '@/utils'
import { PageStore } from '@zswl/components'
import Api from '@/api/project/component/ReviewDetail/Log/diffInfo/api'
import mathjs from '@/utils/math'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  page = new PageStore({})

  compareData = {}
  setCompareData = (data) => {
    this.compareData = data
  }
  init = async (id) => {
    const res = await Api.compareVersionList({ id })
    this.setCompareData(res ?? {})
  }

  formatPercent = (val) => {
    return hasValue(val) ? val / 10000 : undefined
  }

  formatNewData = (values, key, formatFn = (v) => v) => {
    return {
      beforeValue: values[key]?.beforeValue,
      isChange: values[key]?.isChange,
      value: formatFn(values[key]?.value),
    }
  }
  formatFormListItemValue = (values) => {
    const data = values?.map((item) => {
      return {
        ...item,
        clientId: {
          label: item.clientName,
          value: item.clientId,
        },
        stockRiskExposure: mathjs.toNonExponentialPlus(
          mathjs.format(mathjs.divide(item.stockRiskExposure, 10000))
        ),
      }
    })
    return data && data.length > 0 ? data : [{}]
  }

  getBaseInfoData = (values, type) => {
    if (type === 'old') {
      const {
        lesseeInfo,
        guaranteeInfo,
        pledgorInfo,
        mortgagorInfo,
        debtorInfo,
        creditorInfo,
        ...rest
      } = values
      return {
        detail: {
          ...rest,
          lesseeInfo: this.formatFormListItemValue(lesseeInfo),
          guaranteeInfo: this.formatFormListItemValue(guaranteeInfo),
          pledgorInfo: this.formatFormListItemValue(pledgorInfo),
          mortgagorInfo: this.formatFormListItemValue(mortgagorInfo),
          debtorInfo: this.formatFormListItemValue(debtorInfo),
          creditorInfo: this.formatFormListItemValue(creditorInfo),
        },
      }
    } else {
      const { newDetail, ...rest } = compareDetail(values)
      const {
        lesseeInfo,
        guaranteeInfo,
        pledgorInfo,
        mortgagorInfo,
        debtorInfo,
        creditorInfo,
        ...newDetailRest
      } = newDetail

      return {
        ...rest,
        newDetail: {
          ...newDetailRest,
          lesseeInfo: this.formatFormListItemValue(lesseeInfo),
          guaranteeInfo: this.formatFormListItemValue(guaranteeInfo),
          pledgorInfo: this.formatFormListItemValue(pledgorInfo),
          mortgagorInfo: this.formatFormListItemValue(mortgagorInfo),
          debtorInfo: this.formatFormListItemValue(debtorInfo),
          creditorInfo: this.formatFormListItemValue(creditorInfo),
        },
      }
    }
  }

  getBoajiaData = (values, type) => {
    if (type === 'old') {
      return {
        ...values,
        leaseRatePercent: this.formatPercent(values.leaseRatePercent),
        irrPercent: this.formatPercent(values.irrPercent),
        factoringRatePercent: this.formatPercent(values.factoringRatePercent),
        aocRatePercent: this.formatPercent(values.aocRatePercent),
        aocFinancingProportion: this.formatPercent(values.aocFinancingProportion),
        factoringFinancingProportion: this.formatPercent(values.factoringFinancingProportion),
        applyCreditAmount: this.formatPercent(values.applyCreditAmount),
        earnestMoney: this.formatPercent(values.earnestMoney),
        downPayment: this.formatPercent(values.downPayment),
        consultingFee: this.formatPercent(values.consultingFee),
        nominalPrice: this.formatPercent(values.nominalPrice),
      }
    } else {
      const { newDetail, ...rest } = compareDetail(values)
      return {
        ...rest,
        newDetail: {
          ...newDetail,
          leaseRatePercent: this.formatPercent(newDetail.leaseRatePercent),
          irrPercent: this.formatPercent(newDetail.irrPercent),
          factoringRatePercent: this.formatPercent(newDetail.factoringRatePercent),
          aocRatePercent: this.formatPercent(newDetail.aocRatePercent),
          aocFinancingProportion: this.formatPercent(newDetail.aocFinancingProportion),
          factoringFinancingProportion: this.formatPercent(newDetail.factoringFinancingProportion),
          applyCreditAmount: this.formatPercent(newDetail.applyCreditAmount),
          earnestMoney: this.formatPercent(newDetail.earnestMoney),
          downPayment: this.formatPercent(newDetail.downPayment),
          consultingFee: this.formatPercent(newDetail.consultingFee),
          nominalPrice: this.formatPercent(newDetail.nominalPrice),
        },
      }
    }
  }
}
export default new Store()
