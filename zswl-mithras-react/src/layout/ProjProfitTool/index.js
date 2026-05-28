import { Modal, Table, Button } from '@zswl/components'
import { getTableColumns, getFormColumns,saveServer } from '@/utils'
import { observer } from '@zswl/admin'
import ALL_COLUMNS from './Column'
import myStore from './store'
import styles from './index.less'

const formNameColumns = ['合同编号']

const nameColumns = [
  '客户名称',
  '业务类型',
  '项目类型',
  '业务部门',
  '合同编号',
  '投放时间',
  '当年已确认收入(税后)',
  '当年测算利息收入(税后)',
  '营业收入',
  'FTP成本',
  '上期末风险金余额',
  '本期末风险金余额',
  '本年风险金计提/转回',
  '附加税',
  '利润总额',
  '利润总额(扣除费用)',
  '本年末剩余本金',
  '本年末保证金余额',
  '年末敞口',
]

const Index = ({ store }) => {
  const { tableData, $table } = myStore
  const columns = getTableColumns(ALL_COLUMNS({}), nameColumns)
  const formColumns = getFormColumns(ALL_COLUMNS({}), formNameColumns)

  return (
    <Modal
      store={store.projProfitTool}
      title={'利润测算'}
      okText={'确定'}
      width={1000}
      footer={null}
    >
      <Table
        searchbar={{
          layout: 'inline',
          items: formColumns,
          searchButton: null,
          resetButton: null,
        }}
        columnsFilter={'layout_ProjProfitTool_1'}
                onFilter={(key,val) => saveServer('layout_ProjProfitTool_1',val)}
        
        editable={false}
        title={() => {
          return (
            <div className={styles.tableHeader}>
              <div>存量项目会计利润测算表（测算时点{tableData.calculateDate}） 货币单位：元</div>
              <div>
                <Button type="primary" onClick={myStore.export}>
                  导出
                </Button>
              </div>
            </div>
          )
        }}
        store={$table}
        columnWidth={140}
        scroll={{
          x: 3000,
        }}
        columns={[...columns]}
      />
    </Modal>
  )
}

export default observer(Index)
