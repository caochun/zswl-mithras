import { observer } from '@zswl/admin'
import { Table } from '@zswl/components'
import { getTableColumns } from '@/utils'
import { ALL_COLUMNS } from './Column'
import { saveServer } from '@/utils'

const Index = ({ store }) => {
  const columns = getTableColumns(ALL_COLUMNS, [
    '年度',
    '部门',
    'FTP行业分类',
    '投放额目标(万元)',
    '利润目标(万元)',
    '利润目标（拨备前万元）',
    '营业收入目标(万元)',
    '营业收入-咨询服务费收入(万元)',
    '营业收入-利息收入(万元)',
    '业务规模(万元)',
    '经营费用(万元)',
    '经营费用-业务招待费(万元)',
    '经营费用-差旅费用(万元)',
    '1月投放目标(万元)',
    '2月投放目标(万元)',
    '3月投放目标(万元)',
    '4月投放目标(万元)',
    '5月投放目标(万元)',
    '6月投放目标(万元)',
    '7月投放目标(万元)',
    '8月投放目标(万元)',
    '9月投放目标(万元)',
    '10月投放目标(万元)',
    '11月投放目标(万元)',
    '12月投放目标(万元)',
  ])

  return (
    <Table
      columnsFilter={'businessGoal_detail_DepartGoal'}
      onFilter={(key, val) => saveServer('businessGoal_detail_DepartGoal', val)}
      editable={false}
      scroll={{ x: true }}
      store={store.departGoalTable}
      columns={[...columns]}
    ></Table>
  )
}

export default observer(Index)
