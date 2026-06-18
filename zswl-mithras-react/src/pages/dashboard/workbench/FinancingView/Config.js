import { DashboardIconBellRed as BellRed, DashboardIconBellYellow as BellYellow } from '@/components/Dashboard/DashboardEntries'
import RepayInterest from './ListDrawer/RepayInterest'
import FundCase from './ListDrawer/FundCase'
import CreditCase from './ListDrawer/CreditCase'
import FundCost from './ListDrawer/FundCost'

export const initFieldsConfig = [
  {
    group: '还本付息',
    groupCode: 'FUND_FINANCE_REPAY',
    borderColor: '#ff4b59',
    component: <RepayInterest />,
    // tipContent: '统计还款日期在本月的数据',
    fields: [
      { name: '本月计划应还', dataIndex: 'repayTotalAmount' },
      { name: '本月未还', dataIndex: 'repayBalanceAmount' },
      { name: '3天内到期', dataIndex: 'repayInThreeDays', symbolIcon: <BellRed /> },
      { name: '7天内到期', dataIndex: 'repayInSevenDays', symbolIcon: <BellYellow /> },
    ],
  },
  {
    group: '融资情况(存量)',
    groupCode: 'FUND_FINANCE_LOAN',
    borderColor: '#256ef9',
    component: <FundCase />,
    // tipContent: '统计当前日期仍在起息的直融、间融合同数据；加权利率为借款合同利率的加权',
    fields: [
      { name: '合计数', dataIndex: 'quantity' },
      { name: '融资余额', dataIndex: 'balanceAmount' },
      { name: '存量融资加权综合成本', dataIndex: 'averageCostFunds' },
      { name: '存量融资加权合同利率', dataIndex: 'averageInterestRate' },
    ],
  },
  {
    group: '融资情况（本年新增）',
    groupCode: 'FUND_FINANCE_LOAN_THIS_YEAR',
    borderColor: '#1dcd9a',
    component: <FundCase />,
    // tipContent:
    //   '统计起息日为当年且当前日期仍在起息的直融、间融合同数据；加权利率为借款合同利率的加权',
    fields: [
      // 第一行：只有合计数
      { name: '合计数', dataIndex: 'quantity', rowIndex: 0 },
      // 第二行：两个数据
      { name: '融资余额', dataIndex: 'balanceAmount', rowIndex: 1 },
      {
        name: '融资金额',
        dataIndex: 'totalAmount',
        rowIndex: 1,
        amountParam: { amountType: 'amount' },
      },
      // 第三行：两个数据
      { name: '本年新增融资加权综合成本', dataIndex: 'averageCostFunds', rowIndex: 2 },
      { name: '本年新增融资加权合同利率', dataIndex: 'averageInterestRate', rowIndex: 2 },
    ],
  },
  {
    group: '融资情况（本月新增）',
    groupCode: 'FUND_FINANCE_LOAN_THIS_MONTH',
    borderColor: '#9f2521',
    component: <FundCase />,
    // tipContent:
    //   '统计起息日为当月且当前日期仍在起息的直融、间融合同数据；加权利率为借款合同利率的加权',
    fields: [
      // 第一行：只有合计数
      { name: '合计数', dataIndex: 'quantity', rowIndex: 0 },
      // 第二行：两个数据
      { name: '融资余额', dataIndex: 'balanceAmount', rowIndex: 1 },
      {
        name: '融资金额',
        dataIndex: 'totalAmount',
        rowIndex: 1,
        amountParam: { amountType: 'amount' },
      },
      // 第三行：两个数据
      { name: '本月新增融资加权综合成本', dataIndex: 'averageCostFunds', rowIndex: 2 },
      { name: '本月新增融资加权合同利率', dataIndex: 'averageInterestRate', rowIndex: 2 },
    ],
  },
  {
    group: '授信情况',
    groupCode: 'FUND_FINANCE_CREDIT',
    borderColor: '#ff8b45',
    component: <CreditCase />,
    // tipContent: '统计当前所有授信数据',
    fields: [
      { name: '合计数', dataIndex: 'quantity' },
      { name: '授信金额', dataIndex: 'totalAmount' },
      { name: '已用额度', dataIndex: 'usedAmount' },
    ],
  },
  {
    group: '资金成本',
    groupCode: 'FOND_FINANCE_COST_FOUNDS',
    borderColor: '#339fbe',
    component: <FundCost />,
    // tipContent: (
    //   <div>
    //     <div>综合资金成本：综合资金成本的加权平均成本</div>
    //     <div> 综合资金成本(本年新增)：本年新增借款的加权平均成本</div>
    //     <div> 综合资金成本(本月新增)：本月新增借款的加权平均成本</div>
    //   </div>
    // ),
    fields: [
      {
        name: '存量直接融资加权成本',
        dataIndex: 'costFundsZR',
        searchParam: { type: 'DIRECT' },
      },
      { name: '存量间接融资加权成本', dataIndex: 'costFundsJR', searchParam: { type: 'INDIRECT' } },
      {
        name: '本年新增直接融资加权成本',
        dataIndex: 'costFundsZRThisYear',
        searchParam: { type: 'DIRECT', isThisYear: 1 },
      },
      {
        name: '本年新增间接融资加权成本',
        dataIndex: 'costFundsJRThisYear',
        searchParam: { type: 'INDIRECT', isThisYear: 1 },
      },
    ],
  },
]

export const getItemConfigByGroupCode = (groupCode) => {
  const current = initFieldsConfig.find((item) => item.groupCode === groupCode)
  return current
}

export const columnsFilterKey = '工作台_融资视图'
