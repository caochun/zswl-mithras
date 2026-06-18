import { App, Button, Page, Select, Table } from '@zswl/components'
import { history, observer } from '@zswl/admin'
import Store from './store'
import { BlackGrayColumns as ALL_COLUMNS } from '@/components/BlackGray/BlackGrayEntries'
import { useMemo } from 'react'
import { getTableColumns } from '@/utils/table'
import { useGetStatus } from '@/blackGray/BlackGrayStatusUtils'
import { WithdrawAction } from '@/components/RiskActions'
import { saveServer } from '@/utils'

const source = 'INTERNAL_APPROVAL'
function Index({ path }) {
  const { approval } = useGetStatus()
  const store = useMemo(() => new Store({ source }), [])
  const columns = useMemo(() => {
    const nameColumns = [
      {
        title: '任务编号',
        actions: ({ id, taskNum: name, auditStatus, currentOperator }) => {
          const isApproval = [1, 2].includes(auditStatus) && currentOperator === account
          return [{ name, to: `${path}/detail/${id}?${isApproval ? 'type=approval' : 'view=1'}` }]
        },
        search: true,
      },
      { title: '数据时点', search: true },
      { title: '所属机构', search: true },
      '任务类型',
      {
        title: '审批状态',
        search: {
          element: <Select options={approval} allowClear />,
        },
      },
      '报送截止日',
      { title: '当前处理人', search: false },
      '更新时间',
    ]
    return getTableColumns(ALL_COLUMNS, nameColumns, true)
  }, [])
  const { rows } = store.table.getSelected()
  const { account } = App.getData().user
  const canEdit =
    rows.length === 1 && [1].includes(rows[0].auditStatus) && rows[0].currentOperator === account
  const taskIds = rows.map((v) => v.auditTaskId)
  return (
    <Table
    columnsFilter={'warehouse_approval_InsideList'}
            onFilter={(key,val) => saveServer('warehouse_approval_InsideList',val)}
    
      store={store.table}
      editable={false}
      selectable
      serial
      actions={[
        <Button.Edit
          onClick={() => history.push(`${path}/detail/${rows[0].id}?type=approval`)}
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
