import { Button, Page, Table } from '@zswl/components'
import { observer } from '@zswl/admin'
import store from './store'
import { getTableColumns } from '@/utils'
import ALL_COLUMNS from './Column'
import { ImportAction } from '@/components/Actions'
import EditModal from './EditModal'
import DataImport from './DataImport'
import { saveServer } from '@/utils'

const nameColumns = [
  {
    title: '评分卡名称',
    width: 220,
    actions: ({ scorecardName: name, id }) => [
      {
        name,
        to: `/risk/sourceCard/detail/${id}`,
        className: 'z-single-line',
        // style: { width: 180 },
      },
    ],
  },
  '适用风控行业分类',
  '说明',
  '适用年份',
  '状态',
  '创建时间',
  '更新时间',
]

const columns = getTableColumns(ALL_COLUMNS, nameColumns, true)
function Index({ path }) {
  const canDelete = store.table.selectedRowKeys.length > 0
  return (
    <Page>
      <Table
        store={store.table}
        editable={false}
        selectable
        columnsFilter={'risk_sourceCard_1'}
        onFilter={(key,val) => saveServer('risk_sourceCard_1',val)}
        actions={[
          <Button.Add onClick={store.add} key="add" access={'fundorganizationadd'}>
            新增
          </Button.Add>,
          <Button.Delete
            onClick={store.delete}
            key="delete"
            disabled={!canDelete}
            access="fundorganizationremove"
          >
            删除
          </Button.Delete>,
          <Button onClick={() => store.uploadModal.open()} key="import">
            基础数据导入
          </Button>,
          <Button type={'primary'} onClick={store.preview} key="preview">
            预览
          </Button>,
        ]}
        scroll={{
          x: 1300,
        }}
        columns={columns}
      />
      <EditModal />
      <DataImport store={store} />
    </Page>
  )
}

export default observer(Index)
