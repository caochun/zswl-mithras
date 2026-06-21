import { useState } from 'react'
import { message } from 'antd'
import { observer } from '@zswl/admin'
import { Descriptions } from '@zswl/components'
import { AmountEditable, DatePickerEditable } from '@/components/Format'
import { amountFormat, formatPercent } from '@/utils'
import Api from '@/api/financial/directFinancingDetail'

function FinancialDirectDetailAsset({ id, disabled }) {
  const [editable, setEditable] = useState(false)
  const store = Descriptions.useStore({
    request: async () => {
      return Api.getAsset({ financingId: +id })
    },
  })
  // 保存
  const save = async () => {
    const values = await store.submit()
    await Api.editAsset({ id, ...values })
    message.success('保存成功')
    await store.init()
    setEditable(false)
  }
  const inputNumberEle = (dataIndex, config) => ({
    editable: (val) =>
      AmountEditable(val, dataIndex, {
        disabled: false,
        ...config,
      }),
    render: (val) => amountFormat(formatPercent(val)),
  })
  return (
    <Descriptions
      title="资产池信息"
      store={store}
      editable={editable}
      labelStyle={{ width: 220 }}
      extra={[
        {
          name: '取消',
          hidden: !editable,
          onClick: () => setEditable(false),
        },
        { name: '保存', hidden: !editable, type: 'primary', onClick: save },
        {
          name: '编辑',
          type: 'primary',
          hidden: editable,
          disabled,
          onClick: () => setEditable(true),
        },
      ]}
      items={[
        {
          title: '封包日',
          dataIndex: 'packageDate',
          requiredMark: true,
          editable: (val) => DatePickerEditable(val, 'packageDate'),
        },
        {
          title: '加权平均贷款年利率(%)',
          dataIndex: 'averageAnnualInterestRate',
          requiredMark: true,
          ...inputNumberEle('averageAnnualInterestRate', { required: true }),
        },
        {
          title: '期末贷款笔数',
          dataIndex: 'numberOfLoans',
          ...inputNumberEle('numberOfLoans'),
        },
        {
          title: '加权平均合同期限(月)',
          dataIndex: 'averageContractTerm',
          ...inputNumberEle('averageContractTerm'),
        },
        {
          title: '借款人户数(户)',
          dataIndex: 'numberOfBorrowers',
          ...inputNumberEle('numberOfBorrowers'),
        },
        {
          title: '加权平均账龄(月)',
          dataIndex: 'averageAging',
          ...inputNumberEle('averageAging'),
        },
        {
          title: '最高贷款利率(%)',
          dataIndex: 'maxLoanInterestRate',
          ...inputNumberEle('maxLoanInterestRate'),
        },
        {
          title: '最低贷款利率(%)',
          dataIndex: 'minLoanInterestRate',
          ...inputNumberEle('minLoanInterestRate'),
        },
        {
          title: '期末租金余额(万元)',
          dataIndex: 'endingRentBalance',
          ...inputNumberEle('endingRentBalance'),
        },
        {
          title: '期末本金余额(万元)',
          dataIndex: 'endingPrincipalBalance',
          ...inputNumberEle('endingPrincipalBalance'),
        },
        {
          title: '加权平均剩余期限(月)',
          dataIndex: 'averageRemainingTerm',
          ...inputNumberEle('averageRemainingTerm'),
        },
        {
          title: '覆盖倍数',
          dataIndex: 'coverageMultiple',
          ...inputNumberEle('coverageMultiple'),
        },
        {
          title: '单笔贷款最高本金余额(万元)',
          dataIndex: 'maxPrincipalBalance',
          ...inputNumberEle('maxPrincipalBalance'),
        },
        {
          title: '单笔贷款平均本金余额(万元)',
          dataIndex: 'averagePrincipalBalance',
          ...inputNumberEle('averagePrincipalBalance'),
        },
        {
          title: '贷款最长剩余期限(月)',
          dataIndex: 'maxRemainingTerm',
          ...inputNumberEle('maxRemainingTerm'),
        },
        {
          title: '贷款最短剩余期限(月)',
          dataIndex: 'minRemainingTerm',
          ...inputNumberEle('minRemainingTerm'),
        },
      ]}
    />
  )
}

export default observer(FinancialDirectDetailAsset)
