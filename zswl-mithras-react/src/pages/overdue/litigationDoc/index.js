import { Button, Page, Table } from '@zswl/components'
import { observer } from '@zswl/admin'
import store from './store'
import { MatchOptionColumn } from '@/components/Format'
import { FounderSelect, PageListDown } from '@/components'
import AddModal from './AddModal'
import { saveServer } from '@/utils'

function Index({ path }) {
  const columns = [
    {
      title: '用印编号',
      dataIndex: 'code',
      actions: (record) => {
        return [
          {
            name: record.code,
            onClick: () =>
              store.createModal.open({
                ...record,
                disabled: ['UNDER_APPROVAL'].includes(record.processStatus),
              }),
          },
        ]
      },
    },
    MatchOptionColumn({
      title: '用印类型',
      dataIndex: 'type',
      matchOption: 'printingType',
      search: true,
    }),
    { title: '用印原因', dataIndex: 'reason' },
    {
      title: '申请人',
      dataIndex: 'applicant',
      search: {
        element: (
          <FounderSelect functionCode="selectfounder-adjust" params={{ job: null }}></FounderSelect>
        ),
      },
      render: (val, { createByName }) => createByName,
    },
    MatchOptionColumn({
      title: '流程状态',
      dataIndex: 'processStatus',
      matchOption: 'commonProcessStatus',
      width: 200,
    }),
    { title: '申请时间', dataIndex: 'createTime', width: 200 },
    {
      title: '文书附件',
      dataIndex: 'action',
      width: 240,
      actions: (record) => {
        return [
          { name: '文件下载', onClick: () => store.download(record) },
          {
            name: '归档',
            onClick: () => store.createModal.open({ ...record, isArchive: true }),
            disabled: ['UNDER_APPROVAL'].includes(record.processStatus),
          },
          {
            name: '删除',
            onClick: () => store.delete(record),
            confirm: ['UN_SUBMIT'].includes(record.processStatus),
            disabled: !['UN_SUBMIT'].includes(record.processStatus),
          },
        ]
      },
    },
  ]
  return (
    <Page>
      <Table
        onFilter={(key, val) => saveServer('litigationDoc', val)}
        store={store.table}
        editable={false}
        actions={[
          <Button.Add type="primary" onClick={() => store.createModal.open()}>
            发起审批
          </Button.Add>,
        ]}
        // scroll={{ x: 'auto' }}
        extra={[<PageListDown module={'litigationDoc'} table={store.table} />]}
        resizable
        columnsFilter="litigationDoc"
        columns={columns}
      />
      <AddModal
        modal={store.createModal}
        modalProps={{
          onCancel: () => {
            store.table.search()
            store.createModal.close()
          },
        }}
      />
    </Page>
  )
}

export default observer(Index)
