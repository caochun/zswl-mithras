import AllList from './CustomListDrawer/AllList'
import SettledList from './CustomListDrawer/SettledList'
import SettleInThreeMonthList from './CustomListDrawer/SettleInThreeMonthList'
import OverdueList from './CustomListDrawer/OverdueList'
import SurvivalList from './CustomListDrawer/SurvivalList'

export const initFieldsConfig = [
  {
    group: '所有客户',
    groupCode: 'CLIENT_ALL',
    tipContent: '客户管理模块的所有客户',
    borderColor: '#FF5962',
    component: <AllList />,
  },
  {
    group: '存续客户',
    groupCode: 'CLIENT_SURVIVAL',
    tipContent: '剩余本金大于0 的客户',
    borderColor: '#3377FF',
    component: <SurvivalList />,
  },
  {
    group: '3个月内结清客户',
    groupCode: 'CLIENT_THREE_MONTH_SETTLE',
    tipContent: '客户所有合同中存在最后一期租金还款日在90天内的客户',
    borderColor: '#35D2A2',
    component: <SettleInThreeMonthList />,
  },
  {
    group: '逾期客户',
    groupCode: 'CLIENT_OVERDUE',
    tipContent: '当前该客户存在任意一笔业务逾期',
    borderColor: '#43A8C7',
    component: <OverdueList />,
  },
  {
    group: '已结清客户',
    groupCode: 'CLIENT_SETTLE',
    borderColor: '#985FF7',
    tipContent: '当前该客户有过放款业务，并且所有业务均已结束',
    component: <SettledList />,
  },
]

export const getNameColumns = (groupCode) => {
  const current = initFieldsConfig.find((item) => item.groupCode === groupCode)
  return current?.component ?? <div></div>
}

export const columnsFilterKey = '工作台_客户视图_客户一览'
