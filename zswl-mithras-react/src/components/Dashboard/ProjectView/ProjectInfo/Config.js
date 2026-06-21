import SettleInThreeMonthTable from './InfoDrawer/SettleInThreeMonthTable/ProjectInfoSettleInThreeMonthTable'
import SettledTable from './InfoDrawer/SettledTable/ProjectInfoRentThisMonthTable'
import OverdueTable from './InfoDrawer/OverdueTable/ProjectInfoOverdueTable'

export const initFieldsConfig = [
  {
    group: '3个月内结清项目',
    groupCode: 'PROJECT_VIEW_INFO_SETTLE_IN_THREE_MONTH',
    iconType: 'icon-jieqing',
    component: <SettleInThreeMonthTable />,
  },
  {
    group: '存在逾期项目',
    groupCode: 'PROJECT_VIEW_INFO_OVERDUE',
    iconType: 'icon-yuqixiangmu',
    component: <OverdueTable />,
  },
  {
    group: '本月应收租金',
    groupCode: 'PROJECT_VIEW_INFO_RENT_IN_MONTH',
    iconType: 'icon-shengyubenjinbobei',
    component: <SettledTable />,
  },
]

export const getNameColumns = (groupCode) => {
  const current = initFieldsConfig.find((item) => item.groupCode === groupCode)
  return current?.component ?? <div></div>
}

export const columnsFilterKey = '工作台_项目视图_项目信息'
