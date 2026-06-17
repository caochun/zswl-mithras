import { Table, Button } from '@zswl/components'
import { getQuery, observer } from '@zswl/admin'
import ALL_COLUMNS from '@/components/BlackGray/Columns'
import { getTableColumns } from '@/utils'
import { Card, message } from 'antd'
import styles from './styles.less'
import recordTableApi from '@/api/blackList/recordTableApi'
import { DeleteAction, ExportAction } from '@/components/RiskActions'
import { saveServer } from '@/utils'

const nameColumns = [
  '企业名称',
  '统一社会信用代码',
  '业务类型',
  '黑灰标识',
  '入库时间',
  '观察期',
  { title: '申请原因', rename: '入库原因', width: 120 },
  '业务规模（万元）',
  '是否报送金控',
  '所属集团',
  {
    title: '所属集团黑灰标识',
    width: 160,
  },
]
const columns = getTableColumns(ALL_COLUMNS, nameColumns)

function Index({ path, store }) {
  const { rows } = store.table.getSelected()
  const { type } = getQuery()
  const { taskNum } = store.page.getData()
  const canDelete = rows.length === 1
  const list = store.table.getList()
  const tableLength = list.length

  const search = async () => {
    const res = await recordTableApi.postBatchModify({ taskNum })
    store.setIsSupplyGroupInfo(true)
    message.success('查询成功')
    store.table.search()
  }
  return (
    <Card title="黑灰名单列表" size={'small'} className={styles.detailTable}>
      <Table
        columnsFilter={path}
        onFilter={(key,val) => saveServer(path,val)}
        serial
        scroll={{ y: 600, x: 1000 }}
        store={store.table}
        selectable
        columnWidth={120}
        columns={columns}
        actions={[
          <ExportAction key={'export'} store={store.table} api={store.export}>
            导出
          </ExportAction>,
          ['edit', 'add'].includes(type) && (
            <Button.Edit
              onClick={() => store.singeModalStore.open(rows[0])}
              disabled={rows.length !== 1}
              key={'edit'}
            >
              编辑
            </Button.Edit>
          ),
          ['edit', 'add'].includes(type) && (
            <DeleteAction
              key={'delete'}
              store={store.table}
              disabled={!canDelete}
              api={recordTableApi.postRecordDelete}
            />
          ),
          <Button.Search key="search" onClick={search} disabled={!tableLength}>
            查询所属集团
          </Button.Search>,
        ]}
      />
    </Card>
  )
}

export default observer(Index)
