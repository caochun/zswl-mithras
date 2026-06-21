import { observer } from '@zswl/admin'
import { Table, Button, Page } from '@zswl/components'
import { getTableColumns } from '@/utils'
import { DepartMentalItem } from '../../../EstimationFields'
import moduleColumns from '../Column'
import moment from 'moment'
import store from '../store'
import { saveServer } from '@/utils'

const KpiEstimationDepartmentalPoolDetail = ({ params: { id } }) => {
  const { detailTable } = store

  const nameColumns = [
    '考核部门',
    '主办',
    {
      title: '详情-部门池利润(元)',
      rename: '部门池利润(元)',
    },
    {
      title: '详情-部门池投放额(元)',
      rename: '部门池投放额(元)',
    },
    {
      title: '详情-部门池利润奖金(元)',
      rename: '部门池利润奖金(元)',
    },
    {
      title: '详情-部门池投放奖金(元)',
      rename: '部门池投放奖金(元)',
    },
    {
      title: '详情-部门池奖金合计(元)',
      rename: '部门池奖金合计(元)',
    },
  ]
  const columns = getTableColumns(moduleColumns(), nameColumns)
  const calculateDate = moment(`${id}`).format('yyyy年MM月')

  return (
    <Page
      store={store.detailPage}
      params={{ id }}
      header={{
        arrow: false,
        title: `月份：${calculateDate}`,
        onBack: () => {},
      }}
    >
      <Table
        columnsFilter="项目绩效测算表_部门池详情"
        onFilter={(key,val) => saveServer('项目绩效测算表_部门池详情',val)}
        rowKey={({ id }) => `${id}`}
        store={detailTable}
        searchbar={{
          items: [
            <DepartMentalItem></DepartMentalItem>,
            // <PersonItem searchForm={store.detailTable.searchStore.formStore} />,
          ],
        }}
        actions={[
          <Button type="primary" onClick={() => store.detailExport({ columns })}>
            导出
          </Button>,
        ].filter(Boolean)}
        editable={false}
        scroll={{
          x: true,
        }}
        columns={columns}
      />
    </Page>
  )
}
export default observer(KpiEstimationDepartmentalPoolDetail)
