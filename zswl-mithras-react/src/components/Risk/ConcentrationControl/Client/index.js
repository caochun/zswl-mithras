import { useMemo } from 'react'
import { Table, TableStore } from '@zswl/components'
import { observer } from '@zswl/admin'
import { getTableColumns, getFormColumns } from '@/utils'
import ALL_COLUMNS from '../../ConcentrationControlColumns'
import Api from '@/api/risk/concentrationControl'
import { TIME_POINT } from '@/utils/domains/risk/RiskUtils'
import { saveServer } from '@/utils'

const nameColumns = [
  '客户名称',
  '所属集团',
  '浙江省内/集团协同业务',
  '剩余未还本金(元)',
  '保证金余额(元)',
  '存量敞口(元)',
  '集中度占比',
  '不良余额(元)',
  '不良余额占比',
  '预警状态',
]
const formNameColumns = [
  '客户名称',
  {
    title: '浙江省内/集团协同业务',
    rename: (
      <div
        style={{
          paddingBottom: 10,
          fontSize: 10,
        }}
      >
        <div>浙江省内/</div>
        <div>集团协同业务</div>
      </div>
    ),
  },
  '数据时点',
  '所属集团',
  '预警状态',
]
const columns = getTableColumns(ALL_COLUMNS, nameColumns)
const formColumns = getFormColumns(ALL_COLUMNS, formNameColumns)

function RiskConcentrationControlClient() {
  const $table = useMemo(() => {
    return new TableStore({
      request: async (params) => {
        const data = await Api.postConcentrationClientList(params)
        return data
      },
    })
  }, [])

  return (
    <Table
      columnsFilter={'concentrationControl_Client_1'}
      onFilter={(key, val) => saveServer('concentrationControl_Client_1', val)}
      columnWidth={180}
      scroll={{ x: 1600 }}
      store={$table}
      editable={false}
      // columnWidth={140}
      searchbar={{
        labelCol: { span: 7 },
        items: formColumns,
        initialValues: {
          dataTimePoint: TIME_POINT,
        },
      }}
      actions={[]}
      columns={[...columns]}
    />
  )
}

export default observer(RiskConcentrationControlClient)
