import { observer, history } from '@zswl/admin'
import { Table, Button } from '@zswl/components'
import { getTableColumns } from '@/utils'
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
      // actions({ divideYear, divideMonth }) {
      //   return [
      //     {
      //       name: moment(`${divideYear}-${divideMonth}`).format('yyyy年MM月'),
      //       onClick: () =>
      //         history.push(`${pathname}/projectManagerProfit/detail/${divideYear}-${divideMonth}`),
      //     },
      //   ]
      // },
      render: (val, { divideYear, divideMonth }) => {
        return (
          <a
            onClick={() =>
              history.push(`${pathname}/projectManagerProfit/detail/${divideYear}-${divideMonth}`)
            }
          >
            {moment(`${divideYear}-${divideMonth}`).format('yyyy年MM月')}
          </a>
        )
      },
    },
    '项目利润-当期值(元)',
    '项目利润-累计值(元)',
    {
      title: '本年存量利润',
      children: [
        {
          title: '本年存量利润-总计主办利润(元)',
          rename: '总计主办利润(元)',
        },
        {
          title: '本年存量利润-总计主办投放(元)',
          rename: '总计主办投放(元)',
        },
        {
          title: '本年存量利润-总计协办利润(元)',
          rename: '总计协办利润(元)',
        },
        {
          title: '本年存量利润-总计协办投放(元)',
          rename: '总计协办投放(元)',
        },
      ],
    },
    {
      title: '本年新增利润',
      children: [
        {
          title: '本年存量利润-总计主办利润(元)',
          rename: '总计主办利润(元)',
        },
        {
          title: '本年存量利润-总计主办投放(元)',
          rename: '总计主办投放(元)',
        },
        {
          title: '本年存量利润-总计协办利润(元)',
          rename: '总计协办利润(元)',
        },
        {
          title: '本年存量利润-总计协办投放(元)',
          rename: '总计协办投放(元)',
        },
      ],
    },
  ]
  const columns = getTableColumns(moduleColumns(), nameColumns)

  return (
    <Table
      bordered
      columnsFilter="项目绩效测算表_项目经理利润完成率"
      onFilter={(key,val) => saveServer('项目绩效测算表_项目经理利润完成率',val)}
      actions={[
        <Button type="primary" onClick={() => store.export({ columns })}>
          导出
        </Button>,
      ]}
      store={store.table}
      editable={false}
      scroll={{
        x: true,
      }}
      columns={columns}
    />
  )
}
export default observer(Index)
