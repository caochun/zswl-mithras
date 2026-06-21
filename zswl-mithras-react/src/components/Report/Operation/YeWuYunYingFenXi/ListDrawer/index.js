import { observer } from '@zswl/admin'
import { Drawer, Table } from '@zswl/components'
import { getSearchColumns } from '@/utils'
import { ALL_COLUMNS } from './Column'
import { COMMON_COLUMNS } from '../../../OperationColumns'
import { initQueryDate, initYearQueryDate } from '@/utils/domains/report/ReportUtils'
import { reportTitle } from '../ReportOperationAnalysis'
import { saveServer } from '@/utils'

const ReportOperationAnalysisListDrawer = ({ store }) => {
  const { listDrawerTable, listDrawer } = store

  const columns = ALL_COLUMNS
  const searchItem = getSearchColumns(COMMON_COLUMNS, [
    '流程类型',
    '时间',
    '审批状态',
    '业务部门',
    '业务类型',
    '租赁类型',
  ])

  return (
    <Drawer
      store={listDrawer}
      width={'90%'}
      destroyOnClose
      extra={null}
      title={`${reportTitle}-明细`}
      onClose={listDrawer.close}
    >
      <Table
        scroll={{ x: true }}
        bordered
        extra={[
          {
            name: '导出',
            type: 'primary',
            onClick: () => store.detailExport({ columns }),
          },
        ]}
        editable={false}
        columnsFilter={`管报_业务运行分析报表_详情`}
        onFilter={(key,val) => saveServer('管报_业务运行分析报表_详情',val)}
        store={listDrawerTable}
        searchbar={{
          initialValues: {
            processStartDate: initYearQueryDate,
          },
          items: searchItem,
        }}
        columns={[...columns]}
      ></Table>
    </Drawer>
  )
}

export default observer(ReportOperationAnalysisListDrawer)
