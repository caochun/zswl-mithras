import { App, Button, Page, Table } from '@zswl/components'
import { history, observer } from '@zswl/admin'
import store from './store'
import ALL_COLUMNS from '../../Columns'
import { useMemo } from 'react'
import { getTableColumns } from '@/utils/table'
import { WithdrawAction } from '../../actions'
import { saveServer } from '@/utils'

function Index({ path }) {
  const { account } = App.getData().user
  const columns = useMemo(() => {
    const nameColumns = [
      {
        title: '企业名称',

        actions: ({ id, enterpriseName: name, auditStatus, currentOperator }) => {
          const isApproval = auditStatus === 1 && currentOperator === account
          return [{ name, to: `${path}/detail/${id}?${isApproval ? 'type=approval' : 'view=1'}` }]
        },
      },
      { title: '所属机构', dataIndex: 'applyOrganization', search: false },
      '业务类型',
      // '黑灰标识',
      { title: '计划出库时间', rename: '原计划出库日期', search: false },
      {
        title: '审批状态',
        search: false,
        // search: {
        //   element: <Select options={approval} allowClear />,
        // },
      },
      { title: '当前处理人', search: false },
      { title: '更新时间', dataIndex: 'updateTime' },
    ]
    return getTableColumns(ALL_COLUMNS, nameColumns, true)
  }, [])
  const { rows } = store.table.getSelected()
  const canEdit =
    rows.length === 1 && [1].includes(rows[0].auditStatus) && rows[0].currentOperator === account
  const taskIds = rows.map((v) => v.auditTaskId)
  return (
    <Page>
      <Table
        columnsFilter={'outbound_approval_1'}
        onFilter={(key, val) => saveServer('outbound_approval_1', val)}
        store={store.table}
        editable={false}
        selectable
        serial
        actions={[
          <Button.Edit
            onClick={() =>
              history.push(`${path}/detail/${rows[0].id}?auditTaskId=${rows[0].auditTaskId}`)
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
    </Page>
  )
}

export default observer(Index)
