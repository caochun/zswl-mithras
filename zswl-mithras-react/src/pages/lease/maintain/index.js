import { Button, Page, Table } from '@zswl/components'
import { observer } from '@zswl/admin'
import { useMemo } from 'react'
import { getTableColumns, getFormColumns } from '@/utils'
import { LeaseMaintainColumns as ALL_COLUMNS } from '@/components/Lease/MaintainEntries'
import Store from './store'
import { saveServer } from '@/utils'

const nameColumns = (path) => [
  {
    title: '编号',
    actions: ({ leaseAuditFlowNumber, id }) => {
      return [
        {
          name: leaseAuditFlowNumber,
          to: `${path}/detail/${id}?type=manage`,
        },
      ]
    },
  },
  '客户名称',
  '项目名称',
  {
    title: '项目编号',
    dataIndex: 'projCode',
  },
  '合同编号',
  '项目主办',
  '项目协办',
  '租赁物状态',
  '创建时间',
]
const formNameColumns = [
  '编号',
  '项目名称',
  '租赁物状态',
  '客户名称',
  '合同编号',
  '项目主办',
  '项目协办',
]

function Index({ path }) {
  const columns = getTableColumns(ALL_COLUMNS, nameColumns(path))
  const formColumns = getFormColumns(ALL_COLUMNS, formNameColumns)

  const store = useMemo(() => {
    return new Store()
  }, [])

  return (
    <Page>
      <Table
        columnsFilter="lease_maintain"
        onFilter={(key,val) => saveServer('lease_maintain',val)}
        scroll={{
          x: 1300,
        }}
        store={store.$table}
        editable={false}
        selectable={{
          type: 'checkbox',
        }}
        columnWidth={180}
        searchbar={{
          labelCol: { span: 6 },
          items: formColumns,
        }}
        extra={[]}
        actions={[
          <Button onClick={store.batchExport} key="export" type="primary">
            批量导出
          </Button>,
        ]}
        columns={[...columns]}
      />
    </Page>
  )
}

export default observer(Index)
