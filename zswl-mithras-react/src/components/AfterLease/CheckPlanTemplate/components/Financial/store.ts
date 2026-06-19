import { TableStore, SearchBarStore } from '@zswl/components'
import { makeAutoObservable, history } from '@zswl/admin'
import Api from '@/api/afterLease/checkPlanFinancialApi'
import reportApi from '@/api/afterLease/rentalInspectionReport'
import moment from 'moment'
import { message } from 'antd'

const BAR_MAP = {
  CAPITAL_BALANCE: 'debtSearchBar',
  PROFIT: 'profitSearchBar',
  CASH_FLOW: 'cashSearchBar',
  GOV_CAPITAL_BALANCE: 'govSearchBar',
  INCOME_EXPEND: 'incomeSearchBar',
}
class Store {
  constructor({ businessVersion }) {
    this.businessVersion = businessVersion
    makeAutoObservable(this)
  }
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

  save = async () => {
    const queryJsonData = this[BAR_MAP[this.active]].getParams()
    const { year, ...rest } = queryJsonData
    let yearFrom = year && moment(year[0]).format('yyyy')
    let yearTo = year && moment(year[1]).format('yyyy')
    const params = {
      checkPlanClientId: this.projectId,
      clientId: this.clientId,
      subjectType: this.active,
      queryJsonData: JSON.stringify({ ...rest, yearFrom, yearTo }),
      clientProjectIdentity: this.activeTag === 4 ? 'LESSEE' : 'GUARANTOR',
      resultJsonData: JSON.stringify(this.financeList),
    }
    await reportApi.postSnapshotSave(params)
  }
  getSnopdata = async () => {
    const { queryData = {}, resultData = [] } =
      (await reportApi.postFinanceSnapshot({
        clientProjectIdentity: this.activeTag === 4 ? 'LESSEE' : 'GUARANTOR',
        checkPlanClientId: this.projectId,
        clientId: this.clientId,
        subjectType: this.active,
        businessVersion: this.businessVersion,
      })) || {}

    const { yearFrom, yearTo, quarter } = queryData
    queryData.year = yearFrom && yearFrom && [moment().year(yearFrom), moment().year(yearTo)]
    queryData.quarter = quarter && `${quarter}`
    const Bar = this[BAR_MAP[this.active]]
    const query = Bar.getParams()
    Bar.setParams({ ...query, ...queryData, clientId: this.clientId, subjectType: this.active })
    if (this.canEdit) {
      Bar.search()
    } else {
      this.financeList = resultData
      this.loading = false
    }
  }
  /**
   * 资产负债表
   */
  debtSearchBar = new SearchBarStore({
    onSearch: async (params) => {
      if (!this.canEdit) return
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
    onSearch: async (params) => {
      if (!this.canEdit) return
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
    onSearch: async (params) => {
      if (!this.canEdit) return
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
    onSearch: async (params) => {
      if (!this.canEdit) return
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
      if (!this.canEdit) return
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
      if (!this.canEdit) return
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
  financeList = {}
  loading = true
  clientId = 1
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
  canEdit = false
  projectId = null
  activeTag = null
  getCommerceDetail = async (data, canEdit = true, activeTag) => {
    this.clientId = data.clientId
    this.projectId = data.projectId
    this.canEdit = canEdit
    this.activeTag = activeTag
    let res
    res = await Api.commerceDetail(data)

    const { orgType } = res
    this.active = orgType != 1 && orgType != null ? 'GOV_CAPITAL_BALANCE' : 'CAPITAL_BALANCE'
    this.orgTypeID = orgType
  }
  //变更日志
  changeLog = async (id) => {
    history.push(`/customer/maintain/detail/log/${id}`)
  }
  //客户生效（或提交审批）
  clientEffect = async (id) => {
    await Api.clientEffect({ id })
    message.success('提交成功！')
  }
}
export default Store
