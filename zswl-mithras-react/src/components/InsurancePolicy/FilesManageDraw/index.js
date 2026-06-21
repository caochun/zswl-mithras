import { observer } from '@zswl/admin'
import { Drawer } from '@zswl/components'
import { NoEnumFileTable } from '@/components/Table'

const InsurancePolicyFilesDrawer = ({ store }) => {
  const { drawList, mainId } = store
  const params = {
    mainId,
    moduleType: 'PAYMENTPOLICY',
  }

  return (
    <Drawer store={store.filesManageDraw} width={800} destroyOnClose extra={null} title="查看附件">
      <NoEnumFileTable
        tableApi={() => {
          return { list: drawList }
        }}
        title={'保单资料'}
        canEdit={false}
        params={params}
        columns={[{ title: '附件名', dataIndex: 'name' }]}
      />
    </Drawer>
  )
}

export default observer(InsurancePolicyFilesDrawer)
