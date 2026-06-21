import { observer } from '@zswl/admin'
import { Table } from '@zswl/components'
import { getTableColumns, getFormColumns, hasValue } from '@/utils'
import store from './store'
import { Button } from 'antd'
import CreateModal from './CreateModal'
import ALL_COLUMNS from '../../MetricValueJinKonColumns'
import styles from './index.less'
import { saveServer } from '@/utils'

const formNameColumns = ['交易对手名称', '交易金额', '交易日期', '关联交易级别', '报送状态']
const nameColumns = [
  '主体机构名称',
  '交易对手名称',
  '报送状态',
  '一级分类',
  '二级分类',
  {
    title: '交易金额',
    rename: '交易金额(万)',
  },
  '交易日期',
  '交易对手上一年末审计净资产(万)',
  '交易目的',
  '关联交易级别',
  '重大交易原因',
  '交易描述',
  '风险/影响',
  '董事会/委员会意见',
]

const RiskMetricJinKon = () => {
  const columns = getTableColumns(ALL_COLUMNS, nameColumns)
  const formColumns = getFormColumns(ALL_COLUMNS, formNameColumns)

  return (
    <div>
      <Table
        columnsFilter={'metricValue_JinKon_1'}
        onFilter={(key, val) => saveServer('metricValue_JinKon_1', val)}
        title={() => {
          return (
            <div className={styles.tableTitle}>
              <Button onClick={store.submit} type="primary">
                批量报送
              </Button>
              <Button onClick={store.createModal.open} type="primary">
                新增一条
              </Button>
            </div>
          )
        }}
        selectable={{
          type: 'checkbox',
          getCheckboxProps: (record) => {
            if (record.reportStatus === 'REPORTED') {
              return { disabled: true }
            }
            return null
          },
        }}
        editable={false}
        resizable
        columnWidth={180}
        scroll={{ x: 1500 }}
        store={store.table}
        searchbar={{
          labelCol: { span: 8 },
          items: formColumns,
        }}
        columns={[
          ...columns,
          {
            title: '操作',
            width: 150,
            fixed: 'right',
            actions: (record) => {
              return [
                {
                  name: '删除',
                  onClick: store.delete,
                  disabled: record.reportStatus === 'REPORTED',
                },
                {
                  name: '编辑',
                  onClick: () => store.createModal.open(record),
                  disabled: record.reportStatus === 'REPORTED',
                },
              ]
            },
          },
        ]}
      />
      <CreateModal store={store}></CreateModal>
    </div>
  )
}

export default observer(RiskMetricJinKon)
