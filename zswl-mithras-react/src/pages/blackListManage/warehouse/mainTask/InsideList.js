import { getTableColumns } from '@/utils'
import { getRandomString, history, http, makeAutoObservable, observer } from '@zswl/admin'
import { App, Button, Page, PageStore, Table, TableStore, Tabs } from '@zswl/components'
import { useMemo } from 'react'
import { BlackGrayColumns as ALl_COLUMNS } from '@/components/BlackGray/BlackGrayEntries'
import { DeleteAction, WithdrawAction } from '@/components/RiskActions'
import warehouseTaskApi from '@/api/blackList/warehouseTaskApi'
import _ from 'lodash-es'
import { saveServer } from '@/utils'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  table = new TableStore({
    request: async ({ auditStatus, ...params }) => {
      return await warehouseTaskApi.postTaskList({
        ...params,
        auditStatus: _.isNumber(auditStatus) ? [auditStatus] : undefined,
        businessSource: 'INTERNAL_APPROVAL',
      })
    },
  })
  page = new PageStore()
  report = async (v) => {
    const { path, sub } = this.page.getParams()

    const id = await warehouseTaskApi.postTaskAdd({
      source: 1,
      businessSource: 'INTERNAL_APPROVAL',
      taskType: 'NOT_TIMED',
    })
    history.push(`${path}/detail/${id}?type=edit`)
  }
}

const InsideList = ({ path, sub }) => {
  const store = useMemo(() => new Store(), [])
  const columns = useMemo(() => {
    const nameColumns = [
      {
        title: '任务编号',
        actions: ({ id, taskNum: name, auditStatus, currentOperator }) => {
          const canEdit =
            [0, 2, 3].includes(auditStatus) && currentOperator === App.getData().user.account
          return [{ name, to: `${path}/detail/${id}?${canEdit ? 'type=edit' : 'view=1'}` }]
        },
        search: true,
      },
      { title: '数据时点', search: true },
      { title: '所属机构', search: true },
      '任务类型',
      { title: '审批状态', search: true },
      { title: '当前处理人', search: false },
      '下级任务进度',
      '报送截止日',
      { title: '是否超时', search: true },
      { title: '更新时间', dataIndex: 'gmtUpdate' },
    ]
    return getTableColumns(ALl_COLUMNS, nameColumns, true)
  }, [])
  const { rows } = store.table.getSelected()
  const canEdit = rows.length === 1 && [0, 2, 3, 5].includes(rows[0].auditStatus)

  //定期报送
  const isTimed =
    rows.length === 1 &&
    rows.every((item) => {
      return [0, 2, 3].includes(item.auditStatus) && item.taskType === 'TIMED'
    })
  //非定期报送
  const isNOT_TIMED =
    rows.length === 1 &&
    rows.every((item) => {
      return [0, 2, 3].includes(item.auditStatus) && item.taskType === 'NOT_TIMED'
    })

  const taskIds = rows.map((v) => v.auditTaskId)
  const canDelete =
    rows.length === 1 &&
    rows.every((item) => {
      return [0].includes(item.auditStatus) && item.taskType === 'NOT_TIMED'
    })
  return (
    <Page noStyle store={store.page} params={{ sub, path }}>
      <Table
        columnsFilter={'warehouse_mainTask_InsideList'}
        onFilter={(key,val) => saveServer('warehouse_mainTask_InsideList',val)}
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
              history.push(
                `${path}/detail/${rows[0].id}?auditTaskId=${rows[0]?.auditTaskId}&type=edit`
              )
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
            api={warehouseTaskApi.postTaskRemove}
            store={store.table}
            disabled={!canDelete}
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

export default observer(InsideList)
