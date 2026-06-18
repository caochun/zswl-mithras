import { DatePicker, Select, Button } from 'antd'
import { Table, Drawer, Form } from '@zswl/components'
import { useEffect, useMemo, useState } from 'react'
import moment from 'moment'
import styles from './index.less'
import peerComparisonController from '@/api/dashboard/peerComparisonController'
import { AmountColumn } from '@/components/Format'
import { observer } from '@zswl/admin'
import { saveServer } from '@/utils'

const DetailTable = ({ store }) => {
  const { busiDate } = store.getInitialValues() ?? {}
  const [avgData, setAvgData] = useState({})
  const table = Table.useStore({
    request: async (params) => {
      const { busiDate, ...rest } = params
      const res = await peerComparisonController.postComparisonList({
        busiDate: busiDate && moment(busiDate).format('YYYY-12-31'),
        ...rest,
      })
      const avgData = res.filter((item) => item.enterpriseName === '行业平均') ?? []
      setAvgData(avgData[0])

      return res.filter((item) => item.enterpriseName !== '行业平均')
    },
  })
  useEffect(() => {
    if (busiDate) {
      table.getSearchStore().setParams({ busiDate: moment(busiDate) })
      table.search()
    }
  }, [busiDate])
  // 表格列配置
  const columns = [
    {
      title: '公司名称',
      dataIndex: 'enterpriseName',
      width: 200,
    },
    AmountColumn({ title: 'ROA', dataIndex: 'roa', suffix: '%', initFormat: 1 }),
    AmountColumn({ title: 'ROE', dataIndex: 'roe', suffix: '%', initFormat: 1 }),
    AmountColumn({ title: '总资产(元)', dataIndex: 'totalAssets', initFormat: 1 }),
    AmountColumn({ title: '净资产(亿元)', dataIndex: 'netAssets', initFormat: 1 }),
    AmountColumn({ title: '净利润(亿元)', dataIndex: 'netProfit', initFormat: 1 }),
    AmountColumn({ title: '杠杆率', dataIndex: 'leverageRatio', suffix: '%', initFormat: 1 }),
  ]

  // 底部显示行业平均值
  const summary = () => {
    const serial = table.getList().length
    return (
      <Table.Summary fixed>
        <Table.Summary.Row className={styles.summary}>
          <Table.Summary.Cell index={0}>{serial + 1}</Table.Summary.Cell>
          <Table.Summary.Cell index={1}>行业平均</Table.Summary.Cell>
          <Table.Summary.Cell index={2} align="right">
            {avgData?.roa ? `${avgData.roa}%` : '-'}
          </Table.Summary.Cell>
          <Table.Summary.Cell index={3} align="right">
            {avgData?.roe ? `${avgData.roe}%` : '-'}
          </Table.Summary.Cell>
          <Table.Summary.Cell index={4} align="right">
            {avgData?.totalAssets}
          </Table.Summary.Cell>
          <Table.Summary.Cell index={5} align="right">
            {avgData?.netAssets}
          </Table.Summary.Cell>
          <Table.Summary.Cell index={6} align="right">
            {avgData?.netProfit}
          </Table.Summary.Cell>
          <Table.Summary.Cell index={7} align="right">
            {avgData?.leverageRatio}
          </Table.Summary.Cell>
        </Table.Summary.Row>
      </Table.Summary>
    )
  }

  return (
    <Drawer
      title="指标明细表"
      placement="right"
      width={1200}
      className={styles.drawer}
      store={store}
      extra={null}
    >
      <Table
              columnsFilter={'IndustryIndexComparison_DetailTable_1'}
              onFilter={(key,val) => saveServer('IndustryIndexComparison_DetailTable_1',val)}
        columns={columns}
        store={table}
        serial
        pagination={false}
        scroll={{ y: 'calc(100vh - 300px)' }}
        searchbar={{
          initialValues: {
            busiDate: moment(busiDate),
          },
          items: [
            <Form.Item label="数据时点" dataIndex="busiDate">
              <DatePicker.YearPicker />
            </Form.Item>,
            { label: '公司名称', dataIndex: 'enterpriseName' },
          ],
        }}
        summary={summary}
      />
    </Drawer>
  )
}

export default observer(DetailTable)
