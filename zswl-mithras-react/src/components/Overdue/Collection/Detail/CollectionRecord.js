import { Table } from '@zswl/components'
import { observer } from '@zswl/admin'
import { MatchOptionColumn, TextAreaColumn } from '@/components/Format'
import { ExportAction as Export } from '@/components/Actions'
import { saveServer } from '@/utils'

function Index({ path, store }) {
  const { rows, keys } = store.table.getSelected() ?? {}

  const columns = [
    { title: '催收记录编号', dataIndex: 'code' },
    MatchOptionColumn({
      title: '催收类型',
      dataIndex: 'type',
      matchOption: 'overdueCollectionType',
    }),
    { title: '催收日期', dataIndex: 'date' },
    { title: '催收人员', dataIndex: 'processPerson' },
    TextAreaColumn({ title: '催收进展', dataIndex: 'describe', width: 200 }),
    {
      title: '操作',
      dataIndex: 'action',
      width: 180,
      actions: (record) => [
        {
          name: '编辑',
          onClick: () => store.collectionModal.open(record),
          disabled: ['UNDER_APPROVAL'].includes(record.processStatus),
        },
        {
          name: '下载函件',
          onClick: () => store.download(record),
          disabled: record.type !== 'SEND_LETTER',
        },
        {
          name: '归档',
          onClick: () => store.collectionModal.open({ ...record, isArchive: true }),
          disabled: ['UNDER_APPROVAL'].includes(record.processStatus),
        },
        {
          name: '删除',
          onClick: () => store.deleteRow({ ...record }),
        },
      ],
    },
  ]
  return (
    <div>
      <div className="z-sub-title" style={{ padding: '16px 8px' }}>
        催收记录
      </div>
      <Table
        columnsFilter={'collection_detail_CollectionRecord'}
        onFilter={(key, val) => saveServer('collection_detail_CollectionRecord', val)}
        store={store.collectionRecordTable}
        editable={false}
        selectable
        actions={[<Export onClick={store.exportRecord} />]}
        resizable
        columns={columns}
      />
    </div>
  )
}

export default observer(Index)
