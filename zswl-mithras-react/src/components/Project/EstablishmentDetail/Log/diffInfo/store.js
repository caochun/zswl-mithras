import { makeAutoObservable } from '@zswl/admin'
import { hasValue } from '@/utils'
import { PageStore } from '@zswl/components'
import mathjs from '@/utils/math'
import Api from './api'

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
      return {
        ...values,
        projSponsorUserId: values.projSponsorUserId
          ? {
              value: values.projSponsorUserId,
              label: values.projSponsorUserName,
            }
          : undefined,
        projCosponsorUserIds: values.projCosponsorUserIds?.map((item, index) => {
          return {
            value: item,
            label: values.projCosponsorUserNames[index],
          }
        }),
        remainAvailableQuota: this.formatPercent(values.remainAvailableQuota),
        riskControlManagerId: values.riskControlManagerId?.map((item, index) => {
          return {
            value: item,
            label: values.riskControlManagerName?.[index],
          }
        }),
        transferorClientId: values.transferorClientId
          ? {
              value: values.transferorClientId,
              label: values.transferorClientName,
            }
          : undefined,
        legalManagerUserId: values.legalManagerUserId
          ? {
              value: values.legalManagerUserId,
              label: values.legalManagerName,
            }
          : undefined,
        debtorInfo: this.formatFormListItemValue(values.debtorInfo),
        lesseeInfo: this.formatFormListItemValue(values.lesseeInfo),
        guaranteeInfo: this.formatFormListItemValue(values.guaranteeInfo),
        pledgorInfo: this.formatFormListItemValue(values.pledgorInfo),
        mortgagorInfo: this.formatFormListItemValue(values.mortgagorInfo),
        creditorInfo: this.formatFormListItemValue(values.creditorInfo),
      }
    } else {
      return {
        ...values,
        projSponsorUserId: {
          isChange: values.projSponsorUserId.isChange,
          value: values.projSponsorUserId.value
            ? {
                value: values.projSponsorUserId.value,
                label: values.projSponsorUserName.value,
              }
            : undefined,
        },
        projCosponsorUserIds: {
          isChange: values.projCosponsorUserIds.isChange,
          value: values.projCosponsorUserIds.value?.map((item, index) => {
            return {
              value: item,
              label: values.projCosponsorUserNames.value[index],
            }
          }),
        },
        remainAvailableQuota: this.formatNewData(
          values,
          'remainAvailableQuota',
          this.formatPercent
        ),
        riskControlManagerId: {
          isChange: values.riskControlManagerId?.isChange,
          value: values.riskControlManagerId?.value?.map((item, index) => {
            return {
              value: item,
              label: values.riskControlManagerName?.value?.[index],
            }
          }),
        },
        transferorClientId: {
          isChange: values.transferorClientId?.isChange,
          value: values.transferorClientId
            ? {
                value: values.transferorClientId.value,
                label: values.transferorClientName.value,
              }
            : undefined,
        },
        legalManagerUserId: {
          isChange: values.legalManagerUserId?.isChange,
          value: values.legalManagerUserId
            ? {
                value: values.legalManagerUserId.value,
                label: values.legalManagerName.value,
              }
            : undefined,
        },
        debtorInfo: {
          isChange: values.debtorInfo?.isChange,
          value: this.formatFormListItemValue(values.debtorInfo?.value),
        },

        lesseeInfo: {
          isChange: values.lesseeInfo?.isChange,
          value: this.formatFormListItemValue(values.lesseeInfo?.value),
        },
        guaranteeInfo: {
          isChange: values.guaranteeInfo?.isChange,
          value: this.formatFormListItemValue(values.guaranteeInfo?.value),
        },
        pledgorInfo: {
          isChange: values.pledgorInfo?.isChange,
          value: this.formatFormListItemValue(values.pledgorInfo?.value),
        },

        mortgagorInfo: {
          isChange: values.mortgagorInfo?.isChange,
          value: this.formatFormListItemValue(values.mortgagorInfo?.value),
        },
        creditorInfo: {
          isChange: values.creditorInfo?.isChange,
          value: this.formatFormListItemValue(values.creditorInfo?.value),
        },
      }
    }
  }
  getBoajiaData = (values, type) => {
    console.log({ values })
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
      return {
        ...values,
        leaseRatePercent: this.formatNewData(values, 'leaseRatePercent', this.formatPercent),
        irrPercent: this.formatNewData(values, 'irrPercent', this.formatPercent),
        factoringRatePercent: this.formatNewData(
          values,
          'factoringRatePercent',
          this.formatPercent
        ),
        aocRatePercent: this.formatNewData(values, 'aocRatePercent', this.formatPercent),
        aocFinancingProportion: this.formatNewData(
          values,
          'aocFinancingProportion',
          this.formatPercent
        ),
        factoringFinancingProportion: this.formatNewData(
          values,
          'factoringFinancingProportion',
          this.formatPercent
        ),
        applyCreditAmount: this.formatNewData(values, 'applyCreditAmount', this.formatPercent),
        earnestMoney: this.formatNewData(values, 'earnestMoney', this.formatPercent),
        downPayment: this.formatNewData(values, 'downPayment', this.formatPercent),
        consultingFee: this.formatNewData(values, 'consultingFee', this.formatPercent),
        nominalPrice: this.formatNewData(values, 'nominalPrice', this.formatPercent),
      }
    }
  }
}
export default new Store()
