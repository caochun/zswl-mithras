import ThisMonthRent from './ListDrawer/ThisMonthRent'
import PledgeList from './ListDrawer/PledgeList'
import PayNoSettleList from './ListDrawer/PayNoSettleList'
import ProvisionList from './ListDrawer/ProvisionList'
import OverdueList from './ListDrawer/OverdueList'

export const initFieldsConfig = [
  {
    group: '本月应收租金',
    groupCode: 'PROJECT_VIEW_FINANCE_RENT_IN_MONTH',
    iconType: 'icon-yingshoushishou',
    component: <ThisMonthRent />,
    tipContent: '统计应收日期在本月的数据',
    fields: [
      { name: '合计数', dataIndex: 'quantity' },
      { name: '本月应收金额', dataIndex: 'planRentAmountThisMonth' },
      { name: '本月未收金额', dataIndex: 'uncollectionRentAmountThisMonth' },
    ],
  },
  {
    group: '项目质押/监管情况',
    groupCode: 'PROJECT_VIEW_FINANCE_PLEDGE',
    iconType: 'icon-yingshoushishou',
    component: <PledgeList />,
    tipContent: '统计尚未结清合同的质押/监管情况',
    fields: [
      { name: '未结清合同数', dataIndex: 'quantity' },
      { name: '存在质押合同数', dataIndex: 'pledgeQuantity' },
      { name: '存在监管合同数', dataIndex: 'superviseQuantity' },
    ],
  },
  {
    group: '存在逾期项目',
    groupCode: 'PROJECT_VIEW_FINANCE_OVERDUE',
    iconType: 'icon-yuqixiangmu',
    component: <OverdueList />,
    tipContent: '展示存在逾期的合同情况',
    fields: [
      { name: '合计数', dataIndex: 'quantity' },
      { name: '金额', dataIndex: 'amount' },
    ],
  },
  {
    group: '剩余本金与拨备',
    groupCode: 'PROJECT_VIEW_FINANCE_PROVISION',
    iconType: 'icon-shengyubenjinbobei',
    component: <ProvisionList />,
    tipContent: '统计最新生效的拨备计提数据',
    fields: [
      { name: '剩余敞口', dataIndex: 'totalExposure' },
      { name: '拨备金额', dataIndex: 'provisionBalanceAmount' },
    ],
  },
  {
    group: '已投放未结清项目',
    groupCode: 'PROJECT_VIEW_FINANCE_NO_SETTLE',
    iconType: 'icon-shengyubenjinbobei',
    component: <PayNoSettleList />,
    tipContent: '有付款核销且合同状态为「生效」和「起租」的数据',
    fields: [
      { name: '未结清合同数', dataIndex: 'quantity' },
      { name: '剩余租金总额', dataIndex: 'rentBalanceAmount' },
    ],
  },
]

export const getNameColumns = (groupCode) => {
  const current = initFieldsConfig.find((item) => item.groupCode === groupCode)
  return current?.component ?? <div></div>
}

export const getItemConfigByGroupCode = (groupCode) => {
  const current = initFieldsConfig.find((item) => item.groupCode === groupCode)
  return current
}

export const columnsFilterKey = '工作台_项目视图_项目情况'
