import { observer, history } from '@zswl/admin'
import { Table, Button } from '@zswl/components'
import { getTableColumns } from '@/utils'
import moduleColumns from './Column'
import { DepartMentalItem } from '@/pages/kpi/estimation/Column'
import moment from 'moment'
import store from './store'
import { render } from 'react-dom'
import { saveServer } from '@/utils'

const Index = ({ pathname }) => {
  const nameColumns = [
    {
      title: '最新核算月份',
      dataIndex: 'calculateDate',
      fixed: 'left',
      render: (val, { divideYear, divideMonth }) => {
        return (
          <a
            onClick={() =>
              history.push(`${pathname}/departmentalPool/detail/${divideYear}-${divideMonth}`)
            }
          >
            {moment(`${divideYear}-${divideMonth}`).format('yyyy年MM月')}
          </a>
        )
      },
    },
    '部门池利润(元)',
    '部门池利润奖金(元)',
    '部门池投放奖金(元)',
    '部门池合计奖金(元)',
  ]
  const columns = getTableColumns(moduleColumns(), nameColumns)

  return (
    <Table
      columnsFilter="项目绩效测算表_部门池"
      onFilter={(key,val) => saveServer('项目绩效测算表_部门池',val)}
      actions={[
        <Button type="primary" onClick={() => store.export({ columns })}>
          导出
        </Button>,
      ]}
      store={store.table}
      editable={false}
      // searchbar={{
      //   items: [<DepartMentalItem></DepartMentalItem>],
      // }}
      scroll={{
        x: true,
      }}
      columns={columns}
    />
  )
}
export default observer(Index)
