import { Button, Page, Table } from '@zswl/components'
import { observer } from '@zswl/admin'
import Store from './store'
import EditModal from './EditModal'
import { getTableColumns, getFormColumns } from '@/utils'
import ALL_COLUMNS from '../../Column'
import { Space } from 'antd'
import { useMemo } from 'react'
import { saveServer } from '@/utils'

function Index({ id: parentId }) {
  const store = useMemo(() => new Store({ id: parentId }), [parentId])
  const nameColumns = [
    {
      title: '担保编号',
      actions: ({ guaranteeCode: name, id }) => [
        { name, onClick: () => store.createModal.open({ id }) },
      ],
      access: 'fundguaranteeinfodetail',
    },
    '总担保额度（元）',
    '已使用担保额度（元）',
    '剩余担保额度（元）',
    '担保生效时间',
    '额度是否可循环',
    '资料名称',
    '担保状态',
    '创建日期',
    '更新日期',
    '备注',
  ]

  const columns = useMemo(() => getTableColumns(ALL_COLUMNS, nameColumns), [])
  const canDelete = store.table.selectedRowKeys.length > 0
  return (
    <div>
      <div
        style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignSelf: 'center',
          padding: '12px 0',
        }}
      >
        <p style={{ fontWeight: 'bold', fontSize: 16, margin: 0 }}>担保信息</p>
        <Space>
          <Button.Delete
            onClick={store.delete}
            key="Delete"
            disabled={!canDelete}
            access="fundguaranteeinforemove"
          >
            关闭担保
          </Button.Delete>
          <Button.Add
            onClick={() => store.createModal.open()}
            key="add"
            access={'fundguaranteeinfoadd'}
          >
            新增担保
          </Button.Add>
        </Space>
      </div>
      <Table
              columnsFilter={'detail_GuaranteeInfo_1'}
              onFilter={(key,val) => saveServer('detail_GuaranteeInfo_1',val)}
        store={store.table}
        editable={false}
        selectable
        columns={columns}
        columnWidth={180}
        scroll={{ x: 2000 }}
      />
      <EditModal store={store} />
    </div>
  )
}

export default observer(Index)
