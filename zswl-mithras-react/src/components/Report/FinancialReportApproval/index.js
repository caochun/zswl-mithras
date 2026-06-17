import { Table, TableStore } from '@zswl/components'
import { COMMON_COLUMNS } from '@/pages/financialReport/Column'
import { observer } from '@zswl/admin'
import { useMemo } from 'react'
import financialReportApi from '@/api/financialReport/financialReportApi'
import DetailModal from '@/pages/financialReport/wait/DetailModal'
import Store from './store'

const Approval = ({ query, params }) => {
  const { id } = params
  const { businessVersion } = query
  const store = useMemo(() => new Store({ listType: 'approval', id, businessVersion }), [id])
  const columns = COMMON_COLUMNS({ handleDetail: store.handleDetail })

  return (
    <div>
      <Table
        columns={columns}
        store={store.approvalTable}
        scroll={{ x: 'max-content' }}
        searchbar={{
          items: [
            {
              label: '报表名称',
              dataIndex: 'reportCategoryCodeList',
              options: 'associationReportCategoryEnum',
              type: 'select',
              mode: 'multiple',
            },
            {
              label: '周期类型',
              dataIndex: 'reportPeriodCategoryList',
              options: 'associationReportPeriodCategoryEnum',
              type: 'select',
              mode: 'multiple',
            },
          ].filter(Boolean),
        }}
      />
      <DetailModal store={store} listType="approval" />
    </div>
  )
}

export default observer(Approval)
