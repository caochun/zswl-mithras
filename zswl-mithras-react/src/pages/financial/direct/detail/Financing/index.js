import { useRef, useState } from 'react'
import { message, InputNumber } from 'antd'
import { Descriptions, Table } from '@zswl/components'
import { observer } from '@zswl/admin'
import { amountFormat, formatPercent, rules } from '@/utils'
import Api from '../api'
import Cost from './Cost'
import { AmountColumn, AmountEditable, DateColumn, MatchOptionColumn } from '@/components/Format'
import CalcDay from './CalcDay'

function Index({ id, disabled, store }) {
  const [editable, setEditable] = useState(false)
  const dataRef = useRef({})
  const descStore = Descriptions.useStore({
    request: () => {
      return dataRef.current
    },
  })
  const tableStore = Table.useStore({
    pagination: false,
    request: async (params) => {
      const data = await Api.getFinance({ financingId: +id, ...params })
      const { page, ...other } = data
      dataRef.current = other
      descStore.init()
      return page
    },
  })
  store.financeTable = tableStore

  // 保存
  const save = async () => {
    const { carryInterestTime, ...rest } = await descStore.submit()
    await Api.editFinancing({
      financingId: +id,
      ...rest,
      carryInterestTime: carryInterestTime && moment(carryInterestTime).format('YYYY-MM-DD'),
    })
    message.success('保存成功')
    await tableStore.search()
    setEditable(false)
  }
  return (
    <div>
      <Descriptions
        title="融资方案"
        store={descStore}
        editable={editable}
        labelStyle={{ width: 180 }}
        contentStyle={{
          minWidth: 230,
          maxWidth: 320,
        }}
        autoRequest={false}
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
            disabled,
            hidden: editable,
            onClick: () => setEditable(true),
          },
        ]}
        items={[
          {
            title: '融资金额(万元)',
            dataIndex: 'financingAmount',
            requiredMark: true,
            editable: (val) =>
              AmountEditable(val, 'financingAmount', { required: true, disabled: false }),
            render: (val) => amountFormat(formatPercent(val)),
          },

          {
            title: '票面加权平均利率 (%)',
            dataIndex: 'averageCouponRate',
            requiredMark: true,
            editable: false,
            render: (val) => amountFormat(formatPercent(val)),
          },
          AmountColumn({
            title: '费用合计(元)',
            dataIndex: 'totalFee',
            initFormat: 10000,
            span: 1,
            editable: false,
          }),
          AmountColumn({
            title: '融资期限(月)',
            dataIndex: 'financingMonth',
            suffix: '月',
            initFormat: 1,
            editable: true,
            wrapItemProps: {
              required: true,
            },
            requiredMark: true,
          }),
          {
            title: '计算日',
            dataIndex: 'calculateDay',
            requiredMark: true,
            editable: {
              element: <CalcDay />,
              rules: [rules.required('请输入')],
              required: true,
            },
            render: (val) => <CalcDay.Detail value={val} />,
          },
          MatchOptionColumn({
            title: '还款方式',
            dataIndex: 'repayWay',
            matchOption: 'repayCalcType',
            editable: true,
            requiredMark: true,
          }),
          MatchOptionColumn({
            title: '还款频率',
            dataIndex: 'repayFrequency',
            matchOption: 'repayRateEnum',
            requiredMark: true,
            editable: true,
          }),
          DateColumn({
            title: '起息日',
            dataIndex: 'carryInterestTime',
            editable: true,
            requiredMark: true,
          }),
          AmountColumn({
            title: '综合融资成本（%）',
            dataIndex: 'comprehensiveFinancingCost',
            editable: false,
          }),
          AmountColumn({
            title: 'FTP收益率（%）',
            dataIndex: 'ftpYieldRate',
            editable: false,
          }),
        ]}
      />
      <Cost tableStore={tableStore} id={id} disabled={disabled} />
    </div>
  )
}

export default observer(Index)
