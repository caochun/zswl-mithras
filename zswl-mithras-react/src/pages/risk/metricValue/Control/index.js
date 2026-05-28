import { observer } from '@zswl/admin'
import { Table } from '@zswl/components'
import { getTableColumns, getFormColumns, hasValue } from '@/utils'
import store from './store'
import { Button, Tag, Space } from 'antd'
import CreateModal from './CreateModal'
import ALL_COLUMNS from '@/pages/risk/metricValue/Control/Column'
import moment from 'moment'
import styles from './index.less'
import { saveServer } from '@/utils'

// 获取当前月份所在季度的最后一个月
const currentMonth = moment()
const quarterEndMonth = currentMonth.endOf('quarter')

const nameColumns = [
  '数据时点',
  '业务类型',
  '创建类型',
  '标的物名称',
  '业务总额(万元)',
  '业务余额(万元)',
  '客户名称',
  '是否同业客户',
  '企业经济成分',
  '主办业务部门',
  '业务起始日期',
  '业务到期日',
  '合同保证价值(万元)',
  '担保人名称',
  '已计提减值(万元)',
  '逾期天数',
  '逾期金额',
  '资产质量分类',
]
const formNameColumns = ['数据时点', '客户名称', '业务类型', '创建类型']

const reportEnum = [
  {
    btnText: '报送数据',
    tagText: '数据未报送',
    tagColor: '#f50',
  },
  {
    btnText: '再次报送',
    tagText: '数据已报送',
    tagColor: '#87d068',
  },
]

const Index = () => {
  const columns = getTableColumns(ALL_COLUMNS, nameColumns)
  const formColumns = getFormColumns(ALL_COLUMNS, formNameColumns)
  const { others } = store
  const reportStatus = others?.reportStatus === true ? 1 : others?.reportStatus === false ? 0 : -1
  const statusText = reportEnum[reportStatus]

  return (
    <div>
      <Table
        columnsFilter={'metricValue_Control_1'}
        onFilter={(key, val) => saveServer('metricValue_Control_1', val)}
        title={() => {
          return (
            <div className={styles.tableTitle}>
              <div>
                {statusText && (
                  <Space>
                    <Button onClick={store.submit} type="primary">
                      {statusText.btnText}
                    </Button>
                    <Tag color={statusText.tagColor}>{statusText.tagText}</Tag>
                  </Space>
                )}
              </div>
              <Button onClick={store.createModal.open} type="primary">
                新增一条
              </Button>
            </div>
          )
        }}
        editable={false}
        resizable
        columnWidth={180}
        scroll={{ x: 1500 }}
        store={store.table}
        searchbar={{
          initialValues: {
            dataMonth: quarterEndMonth,
          },
          labelCol: { span: 6 },
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
                },
                {
                  name: '编辑',
                  onClick: () => store.createModal.open(record),
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

export default observer(Index)
