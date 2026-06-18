import { observer } from '@zswl/admin'
import { Modal } from '@zswl/components'
import { EditDescription } from '@/components/Table'
import { RiskPublicMonitorColumns as ALL_COLUMNS } from '@/components/Risk/PublicMonitorColumnsEntries'
import { getDescColumns } from '@/utils'

const Index = ({ store }) => {
  const { courtAnnounceModal, courtAnnounceDetail } = store
  const nameColumns = [
    '企业名称',
    '统一社会信用代码',
    '公告类型',
    '案件号',
    '法院名称',
    '当事人名称',
    '原告',
    '法官姓名',
    '省份信息',
    '法律程序级别',
    '公告内容',
  ]
  const columns = getDescColumns(ALL_COLUMNS(), nameColumns)

  return (
    <Modal store={courtAnnounceModal} title={'法院公告'} destroyOnClose width={1000} footer={null}>
      <EditDescription
        detail={courtAnnounceDetail}
        canEdit={false}
        columns={[
          ...columns,
          {
            title: '发布日期',
            dataIndex: 'publDate',
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

export default observer(Index)
