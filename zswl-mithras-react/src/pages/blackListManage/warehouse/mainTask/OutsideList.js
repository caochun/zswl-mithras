import { getTableColumns } from '@/utils'
import { getRandomString, history, http, makeAutoObservable, observer } from '@zswl/admin'
import { App, Button, Page, PageStore, Table, TableStore, Tabs } from '@zswl/components'
import { useMemo } from 'react'
import ALl_COLUMNS from '../../Column'
import { DeleteAction, WithdrawAction } from '@/components/RiskActions'
import recordTableApi from '@/api/blackList/recordTableApi'
import { saveServer } from '@/utils'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  table = new TableStore({
    request: async (params) => {
      return await recordTableApi.postRecordList({ ...params, source: 'EXTERNAL_APPROVAL' })
    },
  })
  page = new PageStore()
  report = async (v) => {
    const { path, sub } = this.page.getParams()
    const { rows, keys } = this.table.getSelected()
    history.push(`${path}/outDetail?type=add`)
  }
}

const OutsideList = ({ path, sub }) => {
  const store = useMemo(() => new Store(), [])
  const columns = useMemo(() => {
    const nameColumns = [
      {
        title: '企业名称',
        actions: ({ id, enterpriseName: name }) => [{ name, to: `${path}/outDetail/${id}?view=1` }],
      },
      { title: '所属机构', dataIndex: 'applyOrganization', search: false },
      '业务类型',
      '黑灰标识',
      { title: '审批状态', search: true },
      { title: '当前处理人', search: false },
      { title: '更新时间', dataIndex: 'updateTime' },
    ]
    return getTableColumns(ALl_COLUMNS, nameColumns, true)
  }, [])
  const { rows } = store.table.getSelected()
  const canEdit =
    rows.length === 1 &&
    [0, 2, 3, 5].includes(rows[0].auditStatus) &&
    (!rows[0].currentOperator || rows[0].currentOperator === App.getData().user.account)
  const canDelete =
    rows.length === 1 &&
    rows.every((item) => {
      return [0].includes(item.auditStatus)
    })
  const taskIds = rows.map((v) => v.auditTaskId)
  return (
    <Page noStyle store={store.page} params={{ sub, path }}>
      <Table
        columnsFilter={'warehouse_mainTask_OutsideList'}
                onFilter={(key,val) => saveServer('warehouse_mainTask_OutsideList',val)}
        
        store={store.table}
        editable={false}
        columnWidth={120}
        serial
        selectable
        actions={[
          <Button.Add onClick={store.report} key="add">
            报送
          </Button.Add>,
          <Button.Edit
            onClick={() =>
              history.push(`${path}/outDetail/${rows[0].id}?auditTaskId=${rows[0]?.auditTaskId}`)
            }
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
            store={store.table}
            disabled={!canDelete}
            api={recordTableApi.postRecordDelete}
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

export default observer(OutsideList)
