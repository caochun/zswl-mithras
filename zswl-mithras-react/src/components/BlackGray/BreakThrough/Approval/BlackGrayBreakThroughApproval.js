import { App, Button, Page, Select, Table } from '@zswl/components'
import { history, observer } from '@zswl/admin'
import store from './store'
import ALL_COLUMNS from '../../Columns'
import { useMemo } from 'react'
import { getTableColumns } from '@/utils/table'
import { useGetStatus } from '@/utils/domains/blackGray/BlackGrayStatusUtils'
import { saveServer } from '@/utils'

function Index({ path }) {
  const { approval } = useGetStatus()
  const columns = useMemo(() => {
    const nameColumns = [
      {
        title: '企业名称',
        actions: ({ id, enterpriseName: name }) => [{ name, to: `${path}/detail/${id}?view=1` }],
      },
      '黑灰标识',
      { title: '申请时间', dataIndex: 'createTime', search: false },
      { title: '报告机构', rename: '申请机构' },
      {
        title: '业务类型',
        rename: '拟开展业务类型',
        dataIndex: 'proposedBusinessType',
      },
      '当前处理人',
      {
        title: '审批状态',
        search: {
          element: <Select options={approval} allowClear />,
        },
      },
    ]
    return getTableColumns(ALL_COLUMNS, nameColumns, true)
  }, [])
  const { rows } = store.table.getSelected()
  const { account } = App.getData().user
  const canEdit =
    rows.length === 1 && [1].includes(rows[0].auditStatus) && rows[0].currentOperator === account
  return (
    <Page>
      <Table
        columnsFilter={'breakThrough_approval_1'}
        onFilter={(key, val) => saveServer('breakThrough_approval_1', val)}
        store={store.table}
        editable={false}
        selectable
        serial
        actions={[
          <Button.Edit
            onClick={() => history.push(`${path}/detail/${rows[0].id}`)}
            key="edit"
            disabled={!canEdit}
            type="primary"
          >
            审批
          </Button.Edit>,
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
