import { observer } from '@zswl/admin'
import { Table } from '@zswl/components'
import { getTableColumns } from '@/utils'
import { ALL_COLUMNS } from './Column'
import { saveServer } from '@/utils'

const KpiBusinessGoalPersonGoal = ({ store }) => {
  const columns = getTableColumns(ALL_COLUMNS, [
    '年度',
    '部门',
    '登陆账户名称',
    '业务人员名称',
    '业务类型',
    '资产余额(万元)',
    '投放额目标(万元)',
    '营业收入目标(万元)',
    '利润目标(万元)',
    // '1月投放目标(万元)',
    // '2月投放目标(万元)',
    // '3月投放目标(万元)',
    // '4月投放目标(万元)',
    // '5月投放目标(万元)',
    // '6月投放目标(万元)',
    // '7月投放目标(万元)',
    // '8月投放目标(万元)',
    // '9月投放目标(万元)',
    // '10月投放目标(万元)',
    // '11月投放目标(万元)',
    // '12月投放目标(万元)',
  ])

  return (
    <Table
      columnsFilter={'businessGoal_detail_PersonGoal'}
      onFilter={(key, val) => saveServer('businessGoal_detail_PersonGoal', val)}
      editable={false}
      scroll={{ x: true }}
      store={store.personGoalTable}
      columns={[...columns]}
    ></Table>
  )
}

export default observer(KpiBusinessGoalPersonGoal)
