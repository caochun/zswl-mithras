import { observer } from '@zswl/admin'
import { Modal } from '@zswl/components'
import { EditDescription } from '@/components/Table'
import ALL_COLUMNS from '../../PublicMonitorColumns/RiskPublicMonitorColumns'
import { getDescColumns } from '@/utils'

const RiskPublicMonitorChangeInfo = ({ store }) => {
  const { changeInfoModal, changeInfoDetail } = store
  const nameColumns = [
    // '企业名称',
    // '统一社会信用代码',
    // '变更事项',
    '企业编码',
    '变更事项描述',
    '变更前内容',
    '变更后内容',
  ]
  const columns = getDescColumns(ALL_COLUMNS(), nameColumns)

  return (
    <Modal
      store={changeInfoModal}
      title={'企业信息变更'}
      okText={'确定'}
      destroyOnClose
      width={1000}
      footer={null}
    >
      <EditDescription
        detail={changeInfoDetail}
        canEdit={false}
        columns={[
          ...columns,
          {
            title: '变更日期',
            dataIndex: 'changeDate',
          },
          {
            title: '更新日期',
            dataIndex: 'updateTime',
          },
        ]}
        title={null}
        hiddenButton
      />
    </Modal>
  )
}

export default observer(RiskPublicMonitorChangeInfo)
