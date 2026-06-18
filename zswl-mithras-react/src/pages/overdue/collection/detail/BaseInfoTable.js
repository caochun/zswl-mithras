import { observer } from '@zswl/admin'
import { getTableColumns } from '@/utils'
import ALL_COLUMNS from '../Column'
import { ExportAction as Export } from '@/components/Actions'
import { Table } from '@zswl/components'
import { saveServer } from '@/utils'

function Index({ path, store }) {
  const columns = getTableColumns(ALL_COLUMNS, [
    {
      title: '合同编号',
      actions: ({ contractCode: name, id }) => [{ name, to: `/contract/list/detail/${id}` }],
    },
    '项目名称',
    '业务类型',
    '合同金额',
    '当前逾期天数',
    '逾期租金',
    '逾期罚息',
    '剩余本金',
    '剩余保证金',
    '风险敞口',
    { title: '项目主办', render: (val, { projSponsorName }) => projSponsorName },
    '业务部门',
    '合同状态',
  ])
  return (
    <div style={{ marginTop: 16 }}>
      <Table
        columnsFilter={'collection_detail_BaseInfoTable'}
        onFilter={(key, val) => saveServer('collection_detail_BaseInfoTable', val)}
        store={store.contractTable}
        editable={false}
        selectable={false}
        actions={[<Export onClick={store.exportContract} />]}
        scroll={{ x: 'auto' }}
        resizable

        columns={columns}
      />
    </div>
  )
}

export default observer(Index)
