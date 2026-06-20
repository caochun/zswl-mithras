import { Button, Page, Table } from '@zswl/components'
import { observer } from '@zswl/admin'
import store from './store'
import { getTableColumns, getFormColumns } from '@/utils'
import ALL_COLUMNS from './Column'
import { InputEditable, FiledFormat } from '@/components/Format'
import { saveServer } from '@/utils'

const formNameColumns = [
  '数据时点',
  '指标类型',
  '指标类别',
  '指标名称',
  '更新时间',
  { title: '预警监测状态', editable: false },
]
const formColumns = getFormColumns(ALL_COLUMNS, formNameColumns)

function Index() {
  const { date } = store.table.getParams()
  const nameColumns = [
    {
      title: '指标编号',
      width: 200,
      fixed: 'left',
      actions: ({ metricCode: name, id }) => [
        {
          name,
          to: `/risk/riskStrategy/indicatorManage/detail/${id}${date ? '?date=' + date : ''}`,
        },
      ],
      editable: InputEditable,
    },
    '指标类型',
    '指标名称',
    '指标类别',
    '当前值',
    '限额值',
    '预警值',
    '预警监测状态',
    // '单位',
    '更新时间',
  ]
  const columns = getTableColumns(ALL_COLUMNS, nameColumns)

  return (
    <Table
      columnsFilter={'riskStrategy_indicatorManage_1'}
      onFilter={(key, val) => saveServer('riskStrategy_indicatorManage_1', val)}
      store={store.table}
      editable={false}
      autoRequest
      searchbar={{
        items: formColumns,
      }}
      extra={[
        <Button key={'calc'} type="primary" onClick={store.calc}>
          计算
        </Button>,
      ]}
      scroll={{
        x: 1200,
      }}
      columns={columns}
    />
  )
}

export default observer(Index)
