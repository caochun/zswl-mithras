import { TableStore, SearchBarStore } from '@zswl/components'
import { makeAutoObservable, history } from '@zswl/admin'
import Api from '@/api/customer/financialReportApi'
import moment from 'moment'
import { message } from 'antd'

const sheetType = {
  CAPITAL_BALANCE: '资产负债表',
  PROFIT: '利润表',
  CASH_FLOW: '现金流量表',
  BIZ_INDEX: '业务指标表',
  GOV_CAPITAL_BALANCE: '资产负债表',
  INCOME_EXPEND: '收入支出表',
}
class Store {
  constructor({ id, canEditFlag }) {
    this.clientId = id
    this.canEditFlag = canEditFlag
    makeAutoObservable(this)
  }
  canEditFlag = true
  active = 'CAPITAL_BALANCE'
  setActive = (active) => {
    this.loading = true
    this.active = active
  }
  /**
   * 模版下载
   */
  download = ({ key }) => {
    if (key === '1') {
      // 企业法人模版下载
      Api.downTemplate({ type: key, fileName: '财报导入模板_企业法人' })
    }
    if (key === '2') {
      // 事业单位模版下载
      Api.downTemplate({ type: key, fileName: '财报导入模板_事业单位' })
    }
  }

  /**
   * 资产负债表
   */
  debtSearchBar = new SearchBarStore({
    onSearch: (params) => {
      const { year, ...rest } = params
      let yearFrom = year && moment(year[0]).format('yyyy')
      let yearTo = year && moment(year[1]).format('yyyy')
      this.getFinanceList({
        unit: this.unitValue || '10000',
        decimalCount: this.precisionValue || '2',
        subjectType: this.active,
        yearFrom,
        yearTo,
        ...rest,
      })
    },
  })
  bizSearchBar = new SearchBarStore({
    onSearch: (params) => {
      const { year, ...rest } = params
      let yearFrom = year && moment(year[0]).format('yyyy')
      let yearTo = year && moment(year[1]).format('yyyy')
      this.getFinanceList({
        unit: this.unitValue || '10000',
        decimalCount: this.precisionValue || '2',
        subjectType: this.active,
        yearFrom,
        yearTo,
        ...rest,
      })
    },
  })
  cashSearchBar = new SearchBarStore({
    onSearch: (params) => {
      const { year, ...rest } = params
      let yearFrom = year && moment(year[0]).format('yyyy')
      let yearTo = year && moment(year[1]).format('yyyy')
      this.getFinanceList({
        unit: this.unitValue || '10000',
        decimalCount: this.precisionValue || '2',
        subjectType: this.active,
        yearFrom,
        yearTo,
        ...rest,
      })
    },
  })
  govSearchBar = new SearchBarStore({
    onSearch: (params) => {
      const { year, ...rest } = params
      let yearFrom = year && moment(year[0]).format('yyyy')
      let yearTo = year && moment(year[1]).format('yyyy')
      this.getFinanceList({
        unit: this.unitValue || '10000',
        decimalCount: this.precisionValue || '2',
        subjectType: this.active,
        yearFrom,
        yearTo,
        ...rest,
      })
    },
  })
  incomeSearchBar = new SearchBarStore({
    onSearch: (params) => {
      const { year, ...rest } = params
      let yearFrom = year && moment(year[0]).format('yyyy')
      let yearTo = year && moment(year[1]).format('yyyy')
      this.getFinanceList({
        unit: this.unitValue || '10000',
        decimalCount: this.precisionValue || '2',
        subjectType: this.active,
        yearFrom,
        yearTo,
        ...rest,
      })
    },
  })
  profitSearchBar = new SearchBarStore({
    onSearch: (params) => {
      const { year, ...rest } = params
      let yearFrom = year && moment(year[0]).format('yyyy')
      let yearTo = year && moment(year[1]).format('yyyy')
      this.getFinanceList({
        unit: this.unitValue || '10000',
        decimalCount: this.precisionValue || '2',
        subjectType: this.active,
        yearFrom,
        yearTo,
        ...rest,
      })
    },
  })
  financeList = []
  loading = true
  getFinanceList = async ({ subjectType, ...rest }) => {
    this.financeList = await Api.getFinanceList({
      clientId: this.clientId,
      subjectType,
      //displayDimensions: ['BASE'],
      displayDimensions: [],
      ...rest,
    })
    this.loading = false
  }

  uploadExcel = async (params, active) => {
    this.loading = true
    await Api.uploadExcel(params).finally(() => {
      this.loading = false
    })
    this.getFinanceList({ subjectType: active })
    message.success('导入成功！')
  }

  //金额单位
  unitValue = null
  unitChange = ({ target: { value } }, subjectType) => {
    this.unitValue = value
    let current = this.debtSearchBar.getParams()
    const { year } = current
    let yearFrom = year && moment(year[0]).format('yyyy')
    let yearTo = year && moment(year[1]).format('yyyy')
    delete current.year
    this.getFinanceList({
      ...current,
      yearFrom,
      yearTo,
      unit: value,
      subjectType,
      decimalCount: this.precisionValue,
    })
  }
  //小数点
  precisionValue = null
  precisionChange = ({ target: { value } }, subjectType) => {
    this.precisionValue = value
    let current = this.debtSearchBar.getParams()
    const { year } = current
    let yearFrom = year && moment(year[0]).format('yyyy')
    let yearTo = year && moment(year[1]).format('yyyy')
    delete current.year
    this.getFinanceList({
      ...current,
      yearFrom,
      yearTo,
      decimalCount: value,
      subjectType,
      unit: this.unitValue,
    })
  }
  orgTypeID = null
  getCommerceDetail = async (data) => {
    const { orgType } = await Api.commerceDetail(data)
    this.orgTypeID = orgType
    if (orgType == 1 || orgType == null) {
      this.active = 'CAPITAL_BALANCE'
    } else {
      this.active = 'GOV_CAPITAL_BALANCE'
    }
  }
}
export default Store
