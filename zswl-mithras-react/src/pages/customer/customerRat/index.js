import { Button, Modal, Page, Table } from '@zswl/components'
import { observer } from '@zswl/admin'
import store from './store'
import { getTableColumns } from '@/utils'
import { CustomerRatColumns as ALL_COLUMNS } from '@/components/Customer/CustomerEntries'
import { useMemo } from 'react'
import CreateModal from './CreateModal'
import useSearch from '@/utils/hooks/useSearch'
import { saveServer } from '@/utils'

function Index({ path, query }) {
  const canDelete = store.table.selectedRowKeys.length > 0
  const columns = useMemo(() => {
    const nameColumns = [
      {
        title: '客户编号',
        search: true,
        width: 140,
        actions: ({ clientCode: name, pricingFrequency, id, modelCode }) => [
          {
            name,
            to: `${path}/detail/${id}?model=${modelCode}`,
          },
        ],
      },
      { title: '客户名称', search: true, width: 260 },
      { title: '模型名称', search: true },
      '评级结果',
      '评级认定结果',
      { title: '发起时间', search: true },
      { title: '发起机构', width: 140 },
      '发起人',
      { title: '流程状态', search: true },
      { title: '评级状态', search: true },
      { title: '评级类型', search: true },
    ]

    return [
      ...getTableColumns(ALL_COLUMNS, nameColumns, true),
      {
        title: '操作',
        width: 200,
        actions: (record) => {
          const disabled = record.ratingStatus === 'true'
          return [
            {
              name: '修改',
              onClick: () => store.edit(record),
              disabled,
            },
            { name: '查看调整记录', onClick: () => store.view(record.id) },
            {
              name: '删除',
              onClick: () => store.delete(record.id),
              confirm: '确定删除该评级吗？',
              disabled,
              style: { color: disabled ? undefined : 'red' },
            },
          ]
        },
      },
    ]
  }, [])
  useSearch({ query, store: store.table })
  return (
    <Page>
      <Table
        columnsFilter={'customer_customerRat_1'}
        onFilter={(key,val) => saveServer('customer_customerRat_1',val)}
        store={store.table}
        editable={false}
        autoRequest={false}
        columnWidth={140}
        actions={[
          <Button.Add onClick={store.add} key="add">
            发起评级
          </Button.Add>,
        ]}
        scroll={{ x: 1200 }}
        columns={columns}
      />
      <CreateModal store={store} />
      <Modal store={store.recordModal} title="调整记录" width={800} footer={[]}>
        <Table
                columnsFilter={'customer_customerRat_2'}
                onFilter={(key,val) => saveServer('customer_customerRat_2',val)}
          editable={false}
          autoRequest={false}
          columnWidth={80}
          scroll={{ x: 'auto' }}
          pagination={false}
          columns={[
            { title: '调整时间', dataIndex: 'overturnTime' },
            { title: '系统评级结果', dataIndex: 'score' },
            { title: '审查结果', dataIndex: 'finalScore' },
            { title: '调整类型', dataIndex: 'adjustType' },
            { title: '调整理由', dataIndex: 'overturnOpinion' },
            { title: '调整人', dataIndex: 'overturnUserName' },
          ]}
          store={store.recordTable}
        />
      </Modal>
    </Page>
  )
}

export default observer(Index)
