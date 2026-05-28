import { getTableColumns } from '@/utils'
import { Page, Table, TableStore, Tabs } from '@zswl/components'
import ALl_COLUMNS from '../../Column'
import { observer } from '@zswl/admin'
import { useMemo, useState } from 'react'
import recordTableApi from '@/api/blackList/recordTableApi'
import { ImportAction } from '@/components/RiskActions'
import { saveServer } from '@/utils'

const CustomTable = ({ params }) => {
  const { blackGrayType } = params
  const table = useMemo(
    () =>
      new TableStore({
        request: async (tableParams) => {
          return await recordTableApi.postRecordList({
            source: 'INTERNAL_UPLOAD',
            ...params,
            ...tableParams,
          })
        },
      }),
    [params]
  )
  const columns = getTableColumns(
    ALl_COLUMNS,
    [
      '企业名称',
      { title: '业务类型', search: false },
      '报告机构',
      '入库时间',
      '计划出库时间',
      { title: '申请原因描述', rename: '申请入库原因' },
      '所属集团',
      '业务规模（万元）',
    ],
    true
  )
  return (
    <Table
      columnsFilter={'enterDatabase_upload_1'}
              onFilter={(key,val) => saveServer('enterDatabase_upload_1',val)}
      
      columns={columns}
      store={table}
      editable={false}
      columnWidth={120}
      actions={[
        <ImportAction
          key="import"
          title={blackGrayType === 'BLACK_LIST' ? '黑名单导入' : '灰名单导入'}
          store={table}
          api={({ file }) =>
            recordTableApi.postRecordUpload({ file, source: 'INTERNAL_UPLOAD', blackGrayType })
          }
          template={recordTableApi.getTemplateDownload}
          accept=".xlsx"
        />,
      ]}
    ></Table>
  )
}

const Index = () => {
  const [activeKey, setActiveKey] = useState('BLACK_LIST')
  const tabItems = [
    {
      key: 'BLACK_LIST',
      label: '黑客户名单',
      children: <CustomTable params={{ blackGrayType: activeKey }} />,
    },
    {
      key: 'GRAY_LIST',
      label: '灰客户名单',
      children: <CustomTable params={{ blackGrayType: activeKey }} />,
    },
  ]
  return (
    <Page>
      <Tabs items={tabItems} activeKey={activeKey} onChange={setActiveKey} />
    </Page>
  )
}

export default observer(Index)
