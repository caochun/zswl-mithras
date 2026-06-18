import { observer } from '@zswl/admin'
import { Modal } from '@zswl/components'
import EditDescription from '@/components/Table/EditDescription'
import ALL_COLUMNS from '@/components/Risk/PublicMonitorColumns'
import { getDescColumns } from '@/utils'

const Index = ({ store }) => {
  const { courtSessionModal, courtSessionDetail } = store

  const nameColumns = [
    '企业名称',
    '统一社会信用代码',
    '法院名称',
    '合法日期',
    '案由',
    '案由代码',
    '原告',
    '被告',
    '首席法官',
    '省份信息',
    // '创建日期',
    // '修改日期',
  ]
  const columns = getDescColumns(ALL_COLUMNS(), nameColumns)

  return (
    <Modal store={courtSessionModal} title={'开庭公告'} destroyOnClose width={1000} footer={null}>
      <EditDescription
        detail={courtSessionDetail}
        canEdit={false}
        columns={[
          ...columns,
          // {
          //   title: '创建日期',
          //   dataIndex: 'insertTime',
          // },
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
