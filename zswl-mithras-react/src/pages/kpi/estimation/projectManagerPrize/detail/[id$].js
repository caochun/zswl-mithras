import { observer } from '@zswl/admin'
import { Table, Button, Page } from '@zswl/components'
import { getTableColumns, getFormColumns } from '@/utils'
import moduleColumns from '../Column'
import { PersonItem, DepartMentalItem } from '@/pages/kpi/estimation/Column'
import moment from 'moment'
import store from '../store'
import { saveServer } from '@/utils'

const Index = ({ params: { id } }) => {
  const { detailTable } = store

  const nameColumns = [
    '考核部门',
    '人员/部门',
    '主办利润奖金(元)',
    '主办投放奖金(元)',
    '协办利润奖金(元)',
    '协办投放奖金(元)',
    '推荐人利润奖金(元)',
    '推荐人投放奖金(元)',
    '合计(元)',
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
        columnsFilter="项目绩效测算表_项目经理奖金计算"
        onFilter={(key,val) => saveServer('项目绩效测算表_项目经理奖金计算',val)}
        rowKey={({ id }) => `${id}`}
        store={detailTable}
        searchbar={{
          items: [
            <DepartMentalItem />,
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
          x: 1500,
        }}
        columns={columns}
      />
    </Page>
  )
}
export default observer(Index)
