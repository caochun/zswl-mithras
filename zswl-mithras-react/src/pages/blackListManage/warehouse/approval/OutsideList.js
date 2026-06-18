import { App, Button, Page, Select, Table } from '@zswl/components'
import { history, observer } from '@zswl/admin'
import Store from './store'
import { BlackGrayColumns as ALL_COLUMNS } from '@/components/BlackGray/BlackGrayEntries'
import { useMemo } from 'react'
import { getTableColumns } from '@/utils/table'
import { useGetStatus } from '@/blackGray/BlackGrayStatusUtils'
import { WithdrawAction } from '@/components/RiskActions'
import { MatchOptionColumn } from '@/components/Format'
import { saveServer } from '@/utils'

const source = 'EXTERNAL_APPROVAL'
function Index({ path }) {
  const { approval } = useGetStatus()
  const store = useMemo(() => new Store({ source }), [])

  const columns = useMemo(() => {
    const nameColumns = [
      {
        title: '企业名称',
        actions: ({ id, enterpriseName: name, auditTaskId }) => [
          { name, to: `${path}/outDetail/${id}?view=1` },
        ],
      },
      { title: '所属机构', dataIndex: 'applyOrganization' },
      '业务类型',
      '黑灰标识',
      {
        title: '审批状态',
        search: {
          element: <Select options={approval} allowClear />,
        },
      },
      { title: '当前处理人', search: false },
      { title: '更新时间', dataIndex: 'updateTime' },
    ]
    return getTableColumns(ALL_COLUMNS, nameColumns, true)
  }, [])
  const { rows } = store.table.getSelected()
  const { account } = App.getData().user
  const canEdit =
    rows.length === 1 && [1, 2].includes(rows[0].auditStatus) && rows[0].currentOperator === account
  const taskIds = rows.map((v) => v.auditTaskId)
  return (
    <Table
      columnsFilter={'warehouse_approval_OutsideList'}
              onFilter={(key,val) => saveServer('warehouse_approval_OutsideList',val)}
      
      store={store.table}
      editable={false}
      selectable
      serial
      actions={[
        <Button.Edit
          onClick={() =>
            history.push(`${path}/outDetail/${rows[0].id}?auditTaskId=${rows[0].auditTaskId}`)
          }
          key="edit"
          disabled={!canEdit}
          type="primary"
        >
          审批
        </Button.Edit>,
        <WithdrawAction
          key={'withdraw'}
          store={store.table}
          taskIds={taskIds}
          fieldNames={{ approvalStatus: 'auditStatus' }}
        />,
      ]}
      scroll={{
        x: 1200,
      }}
      columns={columns}
    />
  )
}

export default observer(Index)
