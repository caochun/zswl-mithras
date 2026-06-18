import { App, Button, Page, Table } from '@zswl/components'
import { history, observer } from '@zswl/admin'
import store from './store'
import { BlackGrayColumns as ALL_COLUMNS } from '@/components/BlackGray/BlackGrayEntries'
import { useMemo } from 'react'
import { getTableColumns } from '@/utils/table'
import Select from 'antd/es/select'
import useGetStatus from '@/utils/hooks/useGetStatus'
import { saveServer } from '@/utils'

function Index({ path }) {
  const { completed } = useGetStatus()
  const columns = useMemo(() => {
    const nameColumns = [
      {
        title: '企业名称',
        actions: ({ id, enterpriseName: name }) => [{ name, to: `${path}/detail/${id}?view=1` }],
      },
      '业务类型',
      '黑灰标识',
      '所属集团黑灰标识',
      '申请时间',
      '所属集团',
      { title: '当前处理人', search: false },
      {
        title: '审批状态',
        search: {
          element: <Select options={completed} allowClear />,
        },
      },
    ]
    return getTableColumns(ALL_COLUMNS, nameColumns, true)
  }, [])
  const { rows } = store.table.getSelected()
  const canEdit = rows.length === 1 && [0, 2, 3, 5].includes(rows[0].auditStatus)
  return (
    <Page>
      <Table
        columnsFilter={'enterDatabase_application_1'}
                onFilter={(key,val) => saveServer('enterDatabase_application_1',val)}
        
        store={store.table}
        editable={false}
        selectable
        serial
        actions={[
          <Button.Add onClick={() => history.push(`${path}/detail`)} key="add">
            新增
          </Button.Add>,
          <Button.Edit
            onClick={() =>
              history.push(`${path}/detail/${rows[0].id}?auditTaskId=${rows[0]?.auditTaskId}`)
            }
            key="edit"
            disabled={!canEdit}
          >
            编辑
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
