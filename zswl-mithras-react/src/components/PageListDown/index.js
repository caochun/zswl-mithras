import { Button } from '@zswl/components'
import { isValidElement, useState } from 'react'
import { toHump, timeFormat, yearFormat } from '@/utils'
import Api from '@/api/common/indexDownloadApi'
import { message } from 'antd'
import _ from 'lodash'

const IndexType = {
  customer: 'CLIENT',
  establishment: 'PROJ_ESTABLISH',
  review: 'PROJ_REVIEW',
  contract: 'CONTRACT',
  org: 'FUND_ORGANIZATION',
  credit: 'FUND_CREDIT',
  fund: 'FUND_FINANCING',
  payment: 'FUND_RECEIPT_REPAY',
  provisioning: 'PROVISION_DETAIL',
  ftpInterest: 'FTP_INTEREST',
  receiveApproval: 'MY_PROCESS_RECEIVED_AUDITED',
  processQuery: 'MY_PROCESS_PROCESS_QUERY',
  checkPlanStrategy: 'AFTER_LEASE_CHECK_PLAN',
  checkList: 'NEW_AFTER_LEASE_CHECK_PLAN',
  price: 'PROJ_PRICING',
  financialDirect: 'FUND_DIRECT_FINANCING',
  businessAging: 'FINANCE_ACCOUNT_AGE_ITEM',
  overdueCollection: 'OVERDUE_COLLECTION',
  litigationRegistration: 'LITIGATION_REGISTRATION',
  litigationDoc: 'DOC_PRINTING',
  fundCreditLimit: 'FUND_CREDIT_LIMIT',
  fundGuaranteeLimit: 'FUND_GUARANTEE_AGENCY_LIMIT',
  projPay: 'BUSINESS_FLOW_PROJ_PAY',
  projCollect: 'BUSINESS_FLOW_PROJ_COLLECT',
  flowFinancialPay: 'BUSINESS_FLOW_FINANCIAL_PAY',
  flowFinancialCollect: 'BUSINESS_FLOW_FINANCIAL_COLLECT',
  accountBalance: 'ACCOUNT_BALANCE',
  liquidityBoard: 'LIQUIDITY_BOARD',
  liquidityMismatch: 'LIQUIDITY_MISMATCH',
  liquidityRepay: 'LIQUIDITY_REPAY',
  liquidityRepayIncome: 'LIQUIDITY_RENT_INCOME',
  accountSetting: 'ACCOUNT_SETTING',
  fundTransfer: 'FUND_TRANSFER',
  manageLedger:"NEW_AFTER_LEASE_CHECK_PLAN_LEDGER",
}

const items = [
  { key: '1', name: '下载当前页数据' },
  { key: '2', name: '下载全量数据' },
]

const Index = ({ table, module, api, extraParams, children, access }) => {
  const [loading, setLoading] = useState(false)
  const functionCode = IndexType[module] ? toHump(IndexType[module]) + 'IndexDownload' : ''

  const handleParams = (params) => {
    // 注意看table 的 params 有没处理
    if (['establishment', 'review', 'contract'].includes(module)) {
      params = {
        ...params,
        createFrom: params.createDate ? timeFormat(params.createDate[0]) : undefined,
        createTo: params.createDate ? timeFormat(params.createDate[1]) : undefined,
        updateFrom: params.updateDate ? timeFormat(params.updateDate[0]) : undefined,
        updateTo: params.updateDate ? timeFormat(params.updateDate[1]) : undefined,
        createDate: undefined,
        updateDate: undefined,
      }
    } else if (['customer'].includes(module)) {
      const { industryType } = params
      const industryTypes = industryType ? industryType[industryType.length - 1] : undefined
      params = {
        ...params,
        createDateFrom: params.createDate ? timeFormat(params.createDate[0]) : undefined,
        createDateTo: params.createDate ? timeFormat(params.createDate[1]) : undefined,
        updateDateFrom: params.updateDate ? timeFormat(params.updateDate[0]) : undefined,
        updateDateTo: params.updateDate ? timeFormat(params.updateDate[1]) : undefined,
        createDate: undefined,
        updateDate: undefined,
        showApprovalFlag: true,
        industryType: industryTypes,
      }
    } else if (['receiveApproval', 'processQuery'].includes(module)) {
      const { projName, projCode, contractCode, ...restParams } = params
      return {
        ...restParams,
        extra: { projName, projCode, contractCode },
      }
    } else if (['checkList'].includes(module)) {
      params = {
        ...params,
        planType: !!params.planType ? params.planType : undefined,
        year: params.year ? yearFormat(params.year) : undefined,
        deadLineForm: params.deadLineForm ? timeFormat(params.deadLineForm[0]) : undefined,
        deadLineTo: params.deadLineForm ? timeFormat(params.deadLineForm[1]) : undefined,
      }
    }
    return params
  }
  const handleDownload = async ({ key }) => {
    if (key) {
      setLoading(true)
      let tableParams = table?.getParams()

      const finallyParams = handleParams(tableParams)
      const bodyParams = {
        originJson: JSON.stringify({ ...finallyParams, ...extraParams }),
        indexType: IndexType[module],
        downloadType: key,
      }

      if (_.isFunction(api)) {
        const res = await api(bodyParams).finally(() => {
          setLoading(false)
        })
        if (res && res?.code && res.code !== 200) {
          message.warn(res?.msg || '下载错误')
        }
      } else {
        const res = await Api.getFileDownload(bodyParams, {
          functionCode,
        }).finally(() => {
          setLoading(false)
        })
        if (res && res?.code && res.code !== 200) {
          message.warn(res?.msg || '下载错误')
        }
      }
    }
  }
  if (isValidElement(children)) {
    return React.cloneElement(children, {
      onClick: () => handleDownload({ key: '2' }),
    })
  }
  return (
    <Button
      type="primary"
      onClick={handleDownload}
      items={items}
      access={functionCode}
      loading={loading}
    >
      下载
    </Button>
  )
}

export default Index
