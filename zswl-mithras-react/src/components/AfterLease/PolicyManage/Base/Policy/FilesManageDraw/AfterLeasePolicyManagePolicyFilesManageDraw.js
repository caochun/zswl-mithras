import { observer } from '@zswl/admin'
import { Drawer } from '@zswl/components'
import { NoEnumFileTable } from '@/components/Table'

const AfterLeasePolicyManagePolicyFilesManageDraw = ({ store }) => {
  const { recordId } = store
  const params = {
    mainId: recordId,
    moduleType: 'POLICY_TMP',
  }

  return (
    <Drawer store={store.filesManageDraw} width={800} destroyOnClose extra={null} title="查看附件">
      <NoEnumFileTable
        title={'保单资料'}
        canEdit={true}
        params={params}
        columns={[{ title: '附件名', dataIndex: 'name' }]}
      />
    </Drawer>
  )
}

export default observer(AfterLeasePolicyManagePolicyFilesManageDraw)
