// import store from '../store'
import styles from '../index.less'
import { Button, Table } from '@zswl/components'
import { amountFormat } from '@/utils'
import { history, observer, toJS } from '@zswl/admin'
import { Modal } from 'antd'
import { saveServer } from '@/utils'

const OverdueSummary = ({ store, baseStore }) => {
  const { list, page } = store
  return (
    <div className={styles.wrap}>
      <div className={styles.titleWrap}>
        <div className={styles.title}>回款情况信息</div>
      </div>
      {list?.map((item, index) => {
        return (
          <OverdueTable
            key={item.paymentId || item.paymentCode}
            data={item}
            store={store}
            baseStore={baseStore}
          />
        )
      })}
    </div>
  )
}
const OverdueTable = ({ data, store, baseStore }) => {
  const { page } = store
  const { paymentCode, overdueRents = [] } = data || {}
  return (
    <div className={styles.tableWrap}>
      <a
        className={styles.code}
        onClick={() => {
          history.push(
            `/cpm/contractCpm/detail/${page.getParams().contractId}?rentActualCode=${paymentCode}`
          )
          baseStore.$projectDetailDrawer.close()
        }}
      >
        {paymentCode}
      </a>
      <Table
        resizable
        dataSource={overdueRents}
        columnsFilter={'ProjectDetail_OverdueSummary_1'}
        onFilter={(key,val) => saveServer('ProjectDetail_OverdueSummary_1',val)}

        rowClassName={(record) => {
          const { overdue } = record

          return overdue ? styles.tableError : ''
        }}
        rowKey="phase"
        columns={[
          {
            title: '期项',
            width: 80,
            dataIndex: 'phase',
            tooltip: true,
          },
          {
            title: '计划收款日期',
            width: 200,
            dataIndex: 'planCollectionDate',
            tooltip: true,
          },
          {
            title: '本金',
            width: 130,
            dataIndex: 'principal',
            tooltip: true,
            render: (val) => amountFormat(val / 10000),
          },
          {
            title: '利息',
            width: 130,
            dataIndex: 'interest',
            tooltip: true,
            render: (val) => amountFormat(val / 10000),
          },
          {
            title: '罚息',
            width: 130,
            dataIndex: 'penaltyInterest',
            tooltip: true,
            render: (val) => (val ? amountFormat(val / 10000) : '-'),
          },
          {
            title: '逾期天数',
            width: 130,
            dataIndex: 'overdueDay',
            tooltip: true,
          },
          {
            title: '核销状态',
            width: 130,
            dataIndex: 'writeOffStatus',
            tooltip: true,
          },
        ]}
      />
    </div>
  )
}
export default observer(OverdueSummary)
