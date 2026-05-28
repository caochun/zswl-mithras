import EditDescription from '@/components/Table/EditDescription'
import { TextAreaEditable } from '@/components/Format'
import { observer } from '@zswl/admin'
import { NoEnumFileTable } from '@/components'

const Index = ({ canEdit, store }) => {
  const columns = [
    { title: '资料名称', dataIndex: 'name' },
    { title: '上传人', dataIndex: 'createByName' },
    { title: '上传时间', dataIndex: 'createTime' },
  ]

  const params = {
    mainId: store.page.getParams().projectDistributionId,
    moduleType: 'KPI_PROJECT_DISTRIBUTION',
    materialsType: 'DESCRIBE',
  }

  return (
    <>
      <EditDescription
        title="补充信息"
        saveData={store.saveExtraData}
        detail={store.page.getData()}
        canEdit={canEdit}
        initEdit={false}
        columns={[
          {
            title: '说明',
            dataIndex: 'suppleDescribe',
            span: 2,
            editable: TextAreaEditable(),
          },
        ]}
      />
      <div style={{ marginTop: 20 }}></div>
      <NoEnumFileTable
        title={'补充资料'}
        canEdit={canEdit}
        columns={columns}
        canBatchDownload
        params={params}
      />
    </>
  )
}

export default observer(Index)
