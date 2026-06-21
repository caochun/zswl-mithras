import { observer } from '@zswl/admin'
import { Table, TableStore, Page, Button } from '@zswl/components'
import { message } from 'antd'
import DataUpload from '@/components/DataUpload'
import { DownloadTemplateAction as DownloadTemplate } from '@/components/Actions'
import Api from '@/api/baseData/leaseholdProperty'
import { saveServer } from '@/utils'

const BaseDataLeaseholdProperty = ({ pathname }) => {
  const $table = new TableStore({
    request: async (params) => {
      const data = await Api.list({
        ...params,
      })
      return data
    },
  })

  const importSheet = async (values) => {
    const { fileList } = DataUpload.classify(values)
    await Api.import({
      file: fileList[0],
    }).finally(() => {})
    message.success('导入成功')
    $table.search()
  }

  return (
    <Page>
      <Table
        columnsFilter={'baseData_lease_1'}
                onFilter={(key,val) => saveServer('baseData_lease_1',val)}
        
        store={$table}
        columnWidth={180}
        extra={[
          <div style={{ marginTop: 10 }}>
            <DownloadTemplate
              params={{
                templateName: 'TEMPLATE_OSS_NAME_LEASED_PROPERTY_TYPE',
                functionCode: 'leaseholdpropertydownload',
              }}
            />
          </div>,
          {
            name: (
              <DataUpload maxCount={1} onChange={importSheet} accept=".xlsx">
                <Button type="primary">导入</Button>
              </DataUpload>
            ),
            type: 'link',
          },
        ]}
        columns={[
          {
            title: '合同编号',
            dataIndex: 'contractCode',
          },
          {
            title: '租赁物类型',
            dataIndex: 'type',
          },
          {
            title: '创建人',
            dataIndex: 'createBy',
          },
          {
            title: '创建时间',
            dataIndex: 'createTime',
          },
        ]}
      />
    </Page>
  )
}
export default observer(BaseDataLeaseholdProperty)
