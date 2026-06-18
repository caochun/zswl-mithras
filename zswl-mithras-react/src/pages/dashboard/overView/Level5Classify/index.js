import { DashboardOverviewTitle as Title } from '@/components/Dashboard/DashboardEntries'
import { Table, Button } from '@zswl/components'
import { AmountFormat } from '@/components/Format'
import { UnorderedListOutlined } from '@ant-design/icons'
import { history, observer } from '@zswl/admin'
import { Space, Spin } from 'antd'
import styles from './index.less'
import { CyclicPie } from '@zswl/charts'
import Store from './Store'
import { useMemo } from 'react'
import { amountFormat, hasValue } from '@/utils'
import Level5ClassifyDrawer from './Level5ClassifyDrawer'
import { canSeeDetailFn } from '@/dashboard/DashboardUtils'
import { saveServer } from '@/utils'

const levelColor = {
  NORMAL: '#06EAB2',
  ATTENTION: '#2D66FF',
  SECONDARY: '#2EC5FF',
  SUSPICIOUS: '#FFCA69',
  LOSS: '#FF5962',
}

// charts组件库0.0.6 ，需要改成colors
const pieColors = Object.values(levelColor)

const Index = ({ title, dataDate }) => {
  const store = useMemo(() => {
    return new Store()
  }, [])

  const getRowClassName = (record, index) => {
    return index % 2 === 0 ? styles['even-row'] : styles['odd-row']
  }

  const data = store.tableData.map((item) => {
    return {
      name: item.assetClassifyResultDisplay,
      value: item.riskExposure?.value,
      unit: item.riskExposure?.unit,
    }
  })

  return (
    <div>
      <Title
        title={title}
        extra={dataDate && <div className={styles.extra}>数据截止时间：{dataDate}</div>}
      ></Title>
      <div className={styles.content}>
        <div className={styles.pie}>
          {!store.tableData.length ? (
            <Spin></Spin>
          ) : (
            <CyclicPie
              style={{ width: 220 }}
              data={data}
              colors={pieColors}
              config={{
                legend: {
                  show: false,
                },
                series: [
                  {
                    emphasis: { scale: false },
                    radius: [100, 70],
                    center: [110, 110],
                    label: {
                      show: true,
                      formatter: () => {
                        return `{a|资产\n五级分类}`
                      },
                      rich: {
                        a: {
                          fontSize: 20,
                          lineHeight: 25,
                          color: '#333',
                        },
                      },
                    },
                  },
                  {
                    type: 'pie',
                    radius: [106, 102],
                    center: [110, 110],
                    hoverAnimation: false,
                    clockWise: false,
                    itemStyle: {
                      normal: {
                        color: '#f1f2f5',
                      },
                    },
                    label: {
                      show: false,
                    },
                    data: [500],
                  },
                ],
              }}
            ></CyclicPie>
          )}
        </div>

        <div className={styles.table}>
          <Table
                  columnsFilter={'overView_Level5Classify_1'}
                  onFilter={(key,val) => saveServer('overView_Level5Classify_1',val)}
            extra={
              canSeeDetailFn() && (
                <Button
                  icon={<UnorderedListOutlined />}
                  onClick={() => {
                    // window.open('/afterLease/level5Classify')
                    store.level5ClassifyDrawerDrawer.open()
                  }}
                >
                  五级分类明细
                </Button>
              )
            }
            pagination={false}
            rowKey={'assetClassifyResultCode'}
            rowClassName={getRowClassName}
            scroll={{ x: true }}
            store={store.table}
            columns={[
              {
                title: ' ',
                dataIndex: 'assetClassifyResultDisplay',
                render: (val, { assetClassifyResultCode }) => {
                  return (
                    <Space>
                      <div
                        style={{
                          width: 10,
                          height: 10,
                          background: levelColor[assetClassifyResultCode],
                        }}
                      ></div>
                      <span>{val}</span>
                    </Space>
                  )
                },
              },
              {
                title: '风险敞口(亿元)',
                dataIndex: 'riskExposure',
                render: (val) => <AmountFormat value={val?.value} initFormat={1} />,
              },
              {
                title: '资产余额(亿元)',
                dataIndex: 'assetBalance',
                render: (val) => <AmountFormat value={val?.value} initFormat={1} />,
              },
              {
                title: '占比',
                dataIndex: 'proportion',
                render: (val) => <AmountFormat value={val?.value} initFormat={1} unit="%" />,
              },
              {
                title: '环比',
                dataIndex: 'chainRatio',
                render: (val) => <AmountFormat value={val?.value} initFormat={1} unit="%" />,
              },
            ]}
          ></Table>
        </div>
      </div>
      <Level5ClassifyDrawer store={store}></Level5ClassifyDrawer>
    </div>
  )
}

export default observer(Index)
