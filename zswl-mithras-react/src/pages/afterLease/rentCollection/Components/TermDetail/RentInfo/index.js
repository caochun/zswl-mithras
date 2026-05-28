import styles from '../index.less'
import classNames from 'classnames'
import { App, Table } from '@zswl/components'
import { amountFormat } from '@/utils'
import { history, observer } from '@zswl/admin'
import { saveServer } from '@/utils'

const RentInfo = ({ baseStore, store }) => {
  const { rentDetail } = store.initData
  const { collectionCode, writeOffStatus, id, collections, totalAmount, collectionAmount } =
    rentDetail || {}

  return (
    <div className={styles.wrap}>
      <div className={styles.writeOffStatusWrap}>
        <a
          className={styles.code}
          onClick={() => {
            history.push(`/cpm/collectionWriteOff/detail/${id}`)
            baseStore.$termDetailDrawer.close()
          }}
        >
          {collectionCode}
        </a>
        <div className={classNames(styles.writeOffStatus, styles[`${writeOffStatus}`])}>
          {App.matchOption('collectionWriteOffStatusEnum', writeOffStatus).label}
        </div>
      </div>

      <div className={styles.titleWrap}>
        <div className={styles.title}>计划收款信息</div>
        <div className={styles.total}>合计：{amountFormat(totalAmount / 10000)}元</div>
      </div>
      <Table
        resizable
        dataSource={[rentDetail]}
        style={{ marginBottom: 24 }}
        columnsFilter={'TermDetail_RentInfo_1'}
        onFilter={(key, val) => saveServer('TermDetail_RentInfo_1', val)}
        pagination={false}
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
            title: '减免罚息',
            width: 130,
            dataIndex: 'creditAmount',
            tooltip: true,
            render: (val) => (val ? amountFormat(val / 10000) : '-'),
          },
        ]}
      />
      <div className={styles.titleWrap}>
        <div className={styles.title}>收款记录明细</div>
        <div className={styles.total}>
          合计：{collectionAmount ? amountFormat(collectionAmount / 10000) : '-'}元
        </div>
      </div>
      <Table
        dataSource={collections}
        columnsFilter={'TermDetail_RentInfo_2'}
        onFilter={(key, val) => saveServer('TermDetail_RentInfo_2', val)}
        columns={[
          {
            title: '收款方式',
            width: 80,
            dataIndex: 'collectionType',
            tooltip: true,
          },
          {
            title: '实收日期',
            width: 200,
            dataIndex: 'collectionDate',
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
            render: (val) => amountFormat(val / 10000),
          },
        ]}
      />
    </div>
  )
}

export default observer(RentInfo)
