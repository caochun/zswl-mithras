import { App, Button, Page, Select, Table } from '@zswl/components'
import { history, observer } from '@zswl/admin'
import ALL_COLUMNS from '../Columns'
import { useMemo } from 'react'
import { getTableColumns } from '@/utils/table'
import { useGetStatus } from '@/utils/domains/blackGray/BlackGrayStatusUtils'
import { DeleteAction, WithdrawAction } from '../Actions'
import manualOutboundFormApi from '@/api/blackGray/manualOutboundFormApi'
import { saveServer } from '@/utils'

function Index({ path, store, type = 'outbound' }) {
  const { application } = useGetStatus()
  const columns = useMemo(() => {
    const nameColumns = [
      {
        title: '企业名称',
        actions: ({ id, enterpriseName: name, auditStatus, currentOperator }) => {
          const canEdit = auditStatus === 0 && currentOperator === App.getData().user.account
          return [{ name, to: `${path}/detail/${id}?${canEdit ? 'type=edit' : 'view=1'}` }]
        },
      },
      { title: '所属机构', dataIndex: 'applyOrganization', search: false },
      '业务类型',
      {
        title: '审批状态',
        search: {
          element: <Select options={'auditStatusEnum'} allowClear />,
        },
      },
      { title: '当前处理人', search: false },
      { title: '更新时间', dataIndex: 'updateTime' },
    ].filter(Boolean)
    return getTableColumns(ALL_COLUMNS, nameColumns, true)
  }, [])
  const { rows } = store.table.getSelected()
  const canEdit =
    rows.length === 1 &&
    [0, 2, 3, 5].includes(rows[0].auditStatus) &&
    (!rows[0].currentOperator || rows[0].currentOperator === App.getData().user.account)
  const canDelete = rows.length >= 1 && rows.every((item) => [0].includes(item.auditStatus))
  const taskIds = rows.map((v) => v.auditTaskId)
  return (
    <Page>
      <Table
        columnsFilter={'blackListManage_components_List'}
                onFilter={(key,val) => saveServer('blackListManage_components_List',val)}
        
        store={store.table}
        editable={false}
        selectable
        serial
        actions={[
          <Button.Add onClick={() => history.push(`${path}/detail`)} key="add">
            出库
          </Button.Add>,
          <Button.Edit
            onClick={() => history.push(`${path}/detail/${rows[0].id}`)}
            key="edit"
            disabled={!canEdit}
          >
            编辑
          </Button.Edit>,
          <WithdrawAction
            key={'withdraw'}
            store={store.table}
            taskIds={taskIds}
            fieldNames={{ approvalStatus: 'auditStatus' }}
          />,
          <DeleteAction
            key={'delete'}
            api={manualOutboundFormApi.postOutboundRemove}
            store={store.table}
            disabled={!canDelete}
          />,
        ]}
        scroll={{ x: 1100 }}
        columns={columns}
      />
    </Page>
  )
}

export default observer(Index)
