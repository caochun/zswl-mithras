import { observer } from '@zswl/admin'
import { Descriptions, Page, Table } from '@zswl/components'
import store from './store'
import { getTableColumns } from '@/utils'
import ALl_COLUMNS from '../../../Columns'
import { useMemo } from 'react'
import { saveServer } from '@/utils'

const Index = ({ params, query }) => {
  const detail = store.page.getData()
  const columns = useMemo(() => {
    const nameColumns = [
      { title: '所属机构', dataIndex: 'applyOrganization' },
      '业务类型',
      '黑灰标识',
      { title: '申请原因', rename: '入库原因', dataIndex: 'applyReasonType', render: (val) => val },
      '入库时间',
      '计划出库时间',
      { title: '业务规模（万元）', render: (val) => val },
      '名单来源',
      '所属集团',
      '所属集团黑灰标识',
    ]
    return getTableColumns(ALl_COLUMNS, nameColumns)
  }, [])
  return (
    <Page params={{ ...params, ...query }} store={store.page} current={'在库明细'}>
      <Descriptions
        items={getTableColumns(ALl_COLUMNS, ['企业名称', '统一社会信用代码'], true)}
        dataSource={detail}
      />
      <div className="z-sub-title ">入库信息</div>
      <Table
        columnsFilter={'allQuery_detail_idjs'}
        onFilter={(key, val) => saveServer('allQuery_detail_idjs', val)}
        columns={columns}
        store={store.table}
        serial
        columnWidth={120}
        scroll={{ x: 'auto' }}
        pagination={false}
        // actions={[<ExportAction api={listLibraryApi.getInfoExport} store={store} key="export" />]}
      />
    </Page>
  )
}
export default observer(Index)
