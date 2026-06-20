import { Button, Page, Table } from '@zswl/components'
import { observer } from '@zswl/admin'
import Store from './store'
import { useMemo } from 'react'
import { DateColumn, MatchOptionColumn } from '@/components/Format'
import CreateModal from './CreateModal'
import { saveServer } from '@/utils'

function Index({ path, tableParams, detail, afterClose }) {
  const store = useMemo(() => new Store({ afterClose }), [afterClose])
  const canDelete = store.table.selectedRowKeys.length > 0
  const columns = [
    {
      title: '项目编号',
      dataIndex: 'projCode',
      width: 120,
      actions: ({ projCode: name, id }) => [
        { name, onClick: () => window.open(`/customer/debtRat/detail/${id}`) },
      ],
    },
    { title: '项目名称', dataIndex: 'projName' },
    { title: '主承租人名称', dataIndex: 'clientName', width: 160 },
    { title: '主承租人信用代码', dataIndex: 'clientUscCode', width: 200 },
    { title: '模型名称', dataIndex: 'modelName', width: 200 },
    { title: '模型编号', dataIndex: 'modelCode', width: 200 },
    { title: '建议项目限额', dataIndex: 'projQuota' },
    DateColumn({ title: '生效日期', dataIndex: 'effectTime' }),
    DateColumn({ title: '失效日期', dataIndex: 'abandonTime' }),
    { title: '发起人', dataIndex: 'createByName' },
    { title: '发起机构', dataIndex: 'startOrg' },
    MatchOptionColumn({
      title: '评级状态',
      dataIndex: 'ratingStatus',
      matchOption: 'customerRatStatus',
    }),
    {
      title: '操作',
      actions: (record) => {
        const disabled = record.ratingStatus === 'true'
        return [
          { name: '修改', onClick: () => store.edit(record), disabled },
          {
            name: '删除',
            onClick: () => store.delete(record.id),
            confirm: true,
            disabled,
            style: { color: disabled ? undefined : 'red' },
          },
        ]
      },
    },
  ]
  return (
    <Page params={{ ...tableParams, detail }} store={store.page} noStyle>
      <Table
        columnsFilter={'customer_debtRat_1'}
        onFilter={(key,val) => saveServer('customer_debtRat_1',val)}
        store={store.table}
        editable={false}
        selectable
        columnWidth={120}
        actions={[
          <Button.Add onClick={store.add} key="add">
            新增债项评级
          </Button.Add>,
        ]}
        scroll={{ x: 'auto' }}
        columns={columns}
      />
      <CreateModal tableParams={tableParams} store={store} />
    </Page>
  )
}

export default observer(Index)
