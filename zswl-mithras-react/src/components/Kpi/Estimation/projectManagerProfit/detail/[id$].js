import { observer } from '@zswl/admin'
import { Table, Button, Page } from '@zswl/components'
import { getTableColumns } from '@/utils'
import { PersonItem, DepartMentalItem, ProjectClassItem } from '../../../EstimationFields'
import moduleColumn from '../Column'
import moment from 'moment'
import store from '../store'
import { saveServer } from '@/utils'

const Index = ({ params: { id } }) => {
  const { detailTable } = store

  const nameColumns = [
    '考核部门',
    '人员/部门',
    {
      title: '本年存量利润',
      children: [
        {
          title: '本年存量-主办利润-产业(元)',
          rename: '主办利润-产业(元)',
        },
        {
          title: '本年存量-主办利润-公用事业(元)',
          rename: '主办利润-公用事业(元)',
        },
        {
          title: '本年存量-主办投放额-产业(元)',
          rename: '主办投放额-产业(元)',
        },
        {
          title: '本年存量-主办投放额-公用事业(元)',
          rename: '主办投放额-公用事业(元)',
        },
        {
          title: '本年存量-协办利润-产业(元)',
          rename: '协办利润-产业(元)',
        },
        {
          title: '本年存量-协办利润-公用事业(元)',
          rename: '协办利润-公用事业(元)',
        },
        {
          title: '本年存量-协办投放额-产业(元)',
          rename: '协办投放额-产业(元)',
        },
        {
          title: '本年存量-协办投放额-公用事业(元)',
          rename: '协办投放额-公用事业(元)',
        },
        {
          title: '本年存量-推荐人利润-产业(元)',
          rename: '推荐人利润-产业(元)',
        },
        {
          title: '本年存量-推荐人利润-公用事业(元)',
          rename: '推荐人利润-公用事业(元)',
        },
        {
          title: '本年存量-推荐人投放额-产业(元)',
          rename: '推荐人投放额-产业(元)',
        },
        {
          title: '本年存量-推荐人投放额-公用事业(元)',
          rename: '推荐人投放额-公用事业(元)',
        },
        {
          title: '本年存量-利润合计(元)',
          rename: '投放合计(元)',
        },
        {
          title: '本年存量-利润合计(元)',
          rename: '利润合计(元)',
        },
      ],
    },
    {
      title: '本年新增利润',
      children: [
        {
          title: '本年新增-主办利润-产业(元)',
          rename: '主办利润-产业(元)',
        },
        {
          title: '本年新增-主办利润-公用事业(元)',
          rename: '主办利润-公用事业(元)',
        },
        {
          title: '本年新增-主办投放额-产业(元)',
          rename: '主办投放额-产业(元)',
        },
        {
          title: '本年新增-主办投放额-公用事业(元)',
          rename: '主办投放额-公用事业(元)',
        },
        {
          title: '本年新增-协办利润-产业(元)',
          rename: '协办利润-产业(元)',
        },
        {
          title: '本年新增-协办利润-公用事业(元)',
          rename: '协办利润-公用事业(元)',
        },
        {
          title: '本年新增-协办投放额-产业(元)',
          rename: '协办投放额-产业(元)',
        },
        {
          title: '本年新增-协办投放额-公用事业(元)',
          rename: '协办投放额-公用事业(元)',
        },
        {
          title: '本年新增-推荐人利润-产业(元)',
          rename: '推荐人利润-产业(元)',
        },
        {
          title: '本年新增-推荐人利润-公用事业(元)',
          rename: '推荐人利润-公用事业(元)',
        },
        {
          title: '本年新增-推荐人投放额-产业(元)',
          rename: '推荐人投放额-产业(元)',
        },
        {
          title: '本年新增-推荐人投放额-公用事业(元)',
          rename: '推荐人投放额-公用事业(元)',
        },
        {
          title: '本年新增-利润合计(元)',
          rename: '投放合计(元)',
        },
        {
          title: '本年新增-利润合计(元)',
          rename: '利润合计(元)',
        },
      ],
    },
  ]
  const columns = getTableColumns(moduleColumn(), nameColumns)
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
        bordered
        columnsFilter="项目绩效测算表_项目经理利润完成率详情"
        onFilter={(key,val) => saveServer('项目绩效测算表_项目经理利润完成率详情',val)}
        rowKey={({ id }) => `${id}`}
        store={detailTable}
        searchbar={{
          items: [
            <DepartMentalItem />,
            // <ProjectClassItem />,
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
export default observer(Index)
