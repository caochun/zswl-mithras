import { observer } from '@zswl/admin'
import { Table, TableStore, Page } from '@zswl/components'
import { getTableColumns, getFormColumns } from '@/utils'
import ALL_COLUMNS from '@/components/Budget/ProjectProfitColumns'
import Api from '@/api/kpi/projProfit/projProfit'
import { saveServer } from '@/utils'

const formNameColumns = ['业务部门', '主办人员', '合同编号']
const nameColumns = [
  '考核部门',
  '业务部门',
  '主办人员',
  '合同编号',
  '项目名称',
  '业务大类',
  '业务小类',
  '投放日',
  '风控行业分类',
  // '本月利息收入',
  // '本月其他收入',
  '本月收入',
  { title: '当年累计收入', rename: '本年累计收入' },
  '本月资金成本',
  '本年累计资金成本',
  '本月毛利',
  '本年累计毛利',
  '本月风险金余额',
  '本月风险金计提/冲抵',
  // '年初风险金余额',
  '累计风险金计提/冲抵',
  '本年累计附加税',
  '本年累计印花税',
  '本年累计利润总额',
  '费用比例',
  '本年累计利润总额（扣费后）',
]

const Index = ({ params: { id } }) => {
  const columns = getTableColumns(ALL_COLUMNS({}), nameColumns)
  const formColumns = getFormColumns(ALL_COLUMNS(), formNameColumns)

  const $table = new TableStore({
    request: async (params) => {
      const data = await Api.postDetailPagelist({
        ...params,
        projectProfitId: id,
      })
      return data
    },
  })

  return (
    <Page>
      <Table
      columnsFilter="projProfit_detail_idjs"
      onFilter={(key,val) => saveServer('projProfit_detail_idjs',val)}


        title={() => {
          return <div style={{ textAlign: 'right' }}>货币单位：元</div>
        }}
        store={$table}
        columnWidth={180}
        editable={false}
        searchbar={{
          items: formColumns,
        }}
        scroll={{
          x: true,
        }}
        columns={columns}
      />
    </Page>
  )
}
export default observer(Index)
