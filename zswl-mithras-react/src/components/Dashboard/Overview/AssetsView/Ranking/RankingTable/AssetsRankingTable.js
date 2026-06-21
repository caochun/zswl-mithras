import { Table } from '@zswl/components'
import { AmountFormat } from '@/components/Format'
import { observer } from '@zswl/admin'
import styles from './index.less'
import { saveServer } from '@/utils'

const AssetsRankingTable = ({ store, extra, type }) => {
  const getRowClassName = (record, index) => {
    return index % 2 === 0 ? styles['even-row'] : styles['odd-row']
  }

  return (
    <div className={styles.table}>
      <Table
              columnsFilter={'Ranking_RankingTable_1'}
              onFilter={(key,val) => saveServer('Ranking_RankingTable_1',val)}
        extra={extra}
        rowKey={'rank'}
        rowClassName={getRowClassName}
        scroll={{ x: false }}
        store={store}
        columns={[
          {
            title: ' ',
            dataIndex: 'rank',
            width: 40,
            render: (val) => <span className={styles.serial}>{val}</span>,
          },
          {
            title: type == 'province' ? '省份' : '经济圈',
            width: 80,
            dataIndex: 'dimensionality',
          },
          {
            title: type == 'province' ? '资产余额(万元)' : '经济圈投放金额(万元)',
            dataIndex: 'assetsBalance',
            align: 'right',
            width: 140,
            render: (val) => <AmountFormat value={val?.value} initFormat={1} />,
          },
          {
            title: '资产占比',
            dataIndex: 'assetsProportion',
            width: 80,
            align: 'right',
            render: (val) => <AmountFormat value={val?.value} unit="%" initFormat={1} />,
          },
          {
            title: '本年投放金额(万元)',
            dataIndex: 'loanThisYear',
            align: 'right',
            width: 120,
            render: (val) => <AmountFormat value={val?.value} initFormat={1} />,
          },
          {
            title: '存量项目数',
            dataIndex: 'stockProjectQuantity',
            width: 100,
            align: 'right',
            render: (val) => <AmountFormat value={val} initFormat={1} />,
          },
        ]}
      ></Table>
    </div>
  )
}

export default observer(AssetsRankingTable)
