import { observer, history } from '@zswl/admin'
import { Table, Button } from '@zswl/components'
import { getTableColumns } from '@/utils'
import { CalculateDateItem } from '@/components/Kpi/EstimationFields'
import moduleColumns from './Column'
import moment from 'moment'
import store from './store'
import { saveServer } from '@/utils'

const Index = ({ pathname }) => {
  const nameColumns = [
    {
      title: '最新核算月份',
      dataIndex: 'calculateDate',
      fixed: 'left',
      // actions({ calculateDateYear, calculateDateMonth }) {
      //   return [
      //     {
      //       name: moment(`${calculateDateYear}-${calculateDateMonth}`).format('yyyy年MM月'),
      //       onClick: () =>
      //         history.push(
      //           `${pathname}/projectManagerPrize/detail/${calculateDateYear}-${calculateDateMonth}`
      //         ),
      //     },
      //   ]
      // },
      render: (val, { calculateDateYear, calculateDateMonth }) => {
        return (
          <a
            onClick={() =>
              history.push(
                `${pathname}/projectManagerPrize/detail/${calculateDateYear}-${calculateDateMonth}`
              )
            }
          >
            {moment(`${calculateDateYear}-${calculateDateMonth}`).format('yyyy年MM月')}
          </a>
        )
      },
    },
    '项目利润-当期值(元)',
    '项目利润-累计值(元)',
    '总计主办利润奖金(元)',
    '总计主办投放奖金(元)',
    '总计协办利润奖金(元)',
    '总计协办投放奖金(元)',
  ]
  const columns = getTableColumns(moduleColumns(), nameColumns)

  return (
    <Table
      columnsFilter="项目绩效测算表_项目经理奖金"
      onFilter={(key,val) => saveServer('项目绩效测算表_项目经理奖金',val)}
      actions={[
        <Button type="primary" onClick={() => store.export({ columns })}>
          导出
        </Button>,
      ]}
      store={store.table}
      editable={false}
      searchbar={{
        items: [<CalculateDateItem></CalculateDateItem>],
      }}
      scroll={{
        x: true,
      }}
      columns={columns}
    />
  )
}
export default observer(Index)
