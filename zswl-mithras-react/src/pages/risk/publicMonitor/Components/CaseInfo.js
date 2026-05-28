import { observer } from '@zswl/admin'
import { Modal } from '@zswl/components'
import EditDescription from '@/components/Table/EditDescription'
import ALL_COLUMNS from '../Column'
import { getDescColumns } from '@/utils'

const Index = ({ store }) => {
  const { caseInfoModal, caseInfoDetail } = store
  const nameColumns = [
    '企业名称',
    '统一社会信用代码',
    '案件号',
    '法院名称',
    '立案日期',
    '地区',
    '案件状态描述',
    '链接地址',
    // '创建日期',
    // '修改日期',
  ]
  const columns = getDescColumns(ALL_COLUMNS(), nameColumns)

  return (
    <Modal store={caseInfoModal} title={'立案信息'} destroyOnClose width={1000} footer={null}>
      <EditDescription
        detail={caseInfoDetail}
        canEdit={false}
        columns={[...columns]}
        title={null}
        hiddenButton
      />
    </Modal>
  )
}

export default observer(Index)
