import Title from '@/components/Dashboard/OverviewTitle'
import { Table } from '@zswl/components'
import { AmountFormat } from '@/components/Format'
import boardTransform from '@/components/Dashboard/assets/board_transform.png'
import Api from '@/api/dashboard/overview'
import { hasValue } from '@/utils'
import styles from './index.less'
import { saveServer } from '@/utils'

const render = ({ value, initFormat = 1 }) => {
  const newValue = value?.value
  if (!hasValue(newValue)) return '-'
  return <AmountFormat value={newValue} unit={value?.unit} initFormat={initFormat} />
}

const Index = ({ title, dataDate }) => {
  const table = Table.useStore({
    request: async () => {
      return await Api.postDashboardConversionStatistics()
    },
  })
  const getRowClassName = (record, index) => {
    return index % 2 === 0 ? styles['even-row'] : styles['odd-row']
  }
  return (
    <div>
      <Title
        title={title}
        extra={dataDate && <div className={styles.extra}>数据截止时间：{dataDate}</div>}
      ></Title>
      <div className={styles.content}>
        <div className={styles.pie}>
          <div className={styles.title}>定位目标客群</div>
          <img src={boardTransform}></img>
        </div>
        <div className={styles.table}>
          <Table
                  columnsFilter={'overView_BusinessTransformation_1'}
                  onFilter={(key,val) => saveServer('overView_BusinessTransformation_1',val)}
            pagination={false}
            rowKey={'id'}
            rowClassName={getRowClassName}
            scroll={{ x: true }}
            store={table}
            columns={[
              {
                title: ' ',
                dataIndex: 'stage',
              },
              {
                title: '历史合计',
                dataIndex: 'total',
                render: (val) => render({ value: val }),
              },
              {
                title: '历史转化率',
                dataIndex: 'conversionRate',
                render: (val) => render({ value: val }),
              },
              {
                title: '本年新增',
                dataIndex: 'incrementThisYear',
                render: (val) => render({ value: val }),
              },
              {
                title: '本年转化率',
                dataIndex: 'incrementConversionRateThisYear',
                render: (val) => render({ value: val }),
              },
            ]}
          ></Table>
        </div>
      </div>
    </div>
  )
}

export default Index
