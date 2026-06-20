import { observer } from '@zswl/admin'
import { Page, Table, SearchBar, Button } from '@zswl/components'
import { DatePicker, Input, Select, Spin } from 'antd'
import { useMemo, useEffect } from 'react'
import { AmountFormat } from '@/components/Format'
import DataUpload from '@/components/DataUpload'
import { hasValue } from '@/utils'
import store from './store'
import moment from 'moment'
import styles from './index.less'
import { saveServer } from '@/utils'

const { Item } = SearchBar
const date = new Date()
const year = date.getFullYear()
const mouth = date.getMonth() - 1

const Index = () => {
  const { allSelect, loading } = store
  useEffect(() => {
    store.getSelect()
  }, [])
  const columns = useMemo(() => {
    const baseColumns = [
      {
        title: '来源sheet',
        dataIndex: 'factorTable',
        width: 120,
        render: (val) => {
          return val || '-'
        },
      },
      {
        title: '指标名称',
        dataIndex: 'factorName',
        width: 350,
        render: (val) => {
          return val || '-'
        },
      },
      {
        title: '数值',
        dataIndex: 'factorValue',
        align: 'right',
        render: (val, { unit }) => {
          return hasValue(val) ? (
            <>
              <AmountFormat value={val}></AmountFormat>
            </>
          ) : (
            '-'
          )
        },
      },
      {
        title: '日期',
        dataIndex: 'factorDate',
        render: (val) => {
          return val || '-'
        },
      },
    ]

    return baseColumns
  }, [])
  return (
    <Page>
      {loading && (
        <div className={styles.spinWrap}>
          <span className={styles.spin}>
            <Spin size="middle"></Spin>
          </span>
        </div>
      )}

      <Table
        columnsFilter={'risk_financeSheet_1'}
        onFilter={(key, val) => saveServer('risk_financeSheet_1', val)}
        resizable
        scroll={{ x: 1200 }}
        serial
        title={() => {
          return (
            <div className={styles.tableTitle}>
              <span>抽查数据统计：{store.table.getPagination().total}项</span>
              <span>单位：元</span>
            </div>
          )
        }}
        store={store.table}
        extra={[
          {
            name: (
              <DataUpload maxCount={1} onChange={store.importSheet} accept=".xlsx">
                <Button type="primary">报表导入</Button>
              </DataUpload>
            ),
            type: 'link',
          },
        ]}
        searchbar={
          <SearchBar
            labelCol={{ span: 7 }}
            limit="6"
            // searchButton={false}
            // resetButton={false}
            initialValues={{
              factorDate: moment().year(year).month(mouth),
            }}
          >
            <Item label="报表所属年月" name="factorDate">
              <DatePicker picker={'month'} allowClear={false} />
            </Item>
            <Item label="来源sheet" name="factorTable">
              <Select options={allSelect.riskMetricFactorTable} />
            </Item>
            <Item label="指标名称" name="factorName">
              <Input />
            </Item>
          </SearchBar>
        }
        columns={columns}
      />
    </Page>
  )
}

export default observer(Index)
