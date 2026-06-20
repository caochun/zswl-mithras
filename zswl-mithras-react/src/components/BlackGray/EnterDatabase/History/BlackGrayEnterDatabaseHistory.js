import { Button, Page, Select, Table } from '@zswl/components'
import { observer } from '@zswl/admin'
import store from './store'
import ALL_COLUMNS from '../../Columns'
import { useMemo } from 'react'
import { getTableColumns } from '@/utils/table'
import { ExportAction } from '../../actions'
import { Checkbox } from 'antd'
import recordTableApi from '@/api/blackGray/recordTableApi'
import { saveServer } from '@/utils'

function Index({ path }) {
  const columns = useMemo(() => {
    const nameColumns = [
      '企业名称',
      '入库方式',
      '黑灰标识',
      '业务类型',
      {
        title: '报告机构',
        search: {
          element: <Select options={'orgOptions'} />,
        },
      },
      '所属集团',
      '入库时间',
      '计划出库时间',
      '所属集团黑灰标识',
      { title: '申请原因描述', rename: '申请入库原因' },
      '业务规模（万元）',
    ]
    return getTableColumns(ALL_COLUMNS, nameColumns, true)
  }, [])
  return (
    <Page>
      <Table
        columnsFilter={'enterDatabase_history_1'}
        onFilter={(key, val) => saveServer('enterDatabase_history_1', val)}
        store={store.table}
        editable={false}
        columnWidth={120}
        serial
        selectable
        actions={[
          <ExportAction
            api={(params) =>
              recordTableApi.getRecordExport({
                ...params,
                isHistory: 0,
                isStock: store.isStock ? 0 : 1,
              })
            }
            store={store.table}
            key="export"
          />,
        ]}
        scroll={{
          x: 1200,
        }}
        extra={[
          <Checkbox key={'wait'} checked={store.isStock} onClick={store.checkChange}>
            仅显示当前在库
          </Checkbox>,
        ]}
        columns={columns}
      />
    </Page>
  )
}

export default observer(Index)
