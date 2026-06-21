import { observer } from '@zswl/admin'
import { Page, Table, SearchBar, Select } from '@zswl/components'
import { DatePicker, Cascader, Input } from 'antd'
import { useEffect, useMemo } from 'react'
import { AmountFormat } from '@/components/Format'
import { hasValue, getKeyOptionsLabelMapPlus, getKeyOptionsLabelMap } from '@/utils'
import store from './store'
import moment from 'moment'
import styles from './index.less'
import { saveServer } from '@/utils'

const { Item } = SearchBar
const { SHOW_CHILD } = Cascader

const date = new Date()
const year = date.getFullYear()
const mouth = date.getMonth()

const RiskMetricTimed = () => {
  const { allSelect, industry } = store
  useEffect(() => {
    store.getSelect()
    store.initIndustry()
  }, [])
  const columns = useMemo(() => {
    const baseColumns = [
      {
        title: '该月在租项目名称',
        dataIndex: 'projName',
        width: 250,
        render: (v) => {
          return v || '-'
        },
      },
      {
        title: '项目类型',
        dataIndex: 'projType',
        width: 120,
        render: (v) => {
          return getKeyOptionsLabelMapPlus('projectType')[v] || '-'
        },
      },
      {
        title: '行业名称',
        dataIndex: 'industryTypeName',
        width: 200,
        render: (v) => {
          return v || '-'
        },
      },
      {
        title: '五级分类',
        dataIndex: 'level5Type',
        width: 120,
        render: (v) => {
          return getKeyOptionsLabelMap('riskMetricLevel5', allSelect)[v] || '-'
        },
      },
      {
        title: '客户名称',
        dataIndex: 'clientName',
        width: 250,
        render: (v) => {
          return v || '-'
        },
      },
      {
        title: '剩余本金(元)',
        dataIndex: 'leftCapital',
        align: 'right',
        width: 150,
        render: (val) => {
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
        title: '逾期金额(本金+利息)(元)',
        dataIndex: 'overdueTotal',
        width: 200,
        align: 'right',
        render: (val) => {
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
        title: '所属集团名称',
        dataIndex: 'belongGroupName',
        width: 200,
        render: (v) => {
          return v || '-'
        },
      },
      {
        title: '是否金控关联方',
        dataIndex: 'related',
        render: (v) => {
          return getKeyOptionsLabelMap('needReportEumn', allSelect)[v] || '-'
        },
      },
    ]

    return baseColumns
  }, [])
  return (
    <Page>
      <Table
        columnsFilter={'risk_metricTimed_1'}
        onFilter={(key, val) => saveServer('risk_metricTimed_1', val)}
        // columnWidth={200}
        resizable
        title={() => {
          return (
            <div className={styles.tableTitle}>
              <span></span>
              <span>系统每月自动存储计算数据</span>
            </div>
          )
        }}
        // scroll={{ x: 1600 }}
        store={store.table}
        searchbar={
          <SearchBar
            labelCol={{ span: 8 }}
            limit="3"
            // searchButton={false}
            // resetButton={false}
            initialValues={{
              dataTime: moment().year(year).month(mouth),
            }}
          >
            <Item label="数据时点" name="dataTime">
              <DatePicker picker={'month'} allowClear={false} />
            </Item>
            <Item label="项目类型" name="projType">
              <Select options={'projectType'} />
            </Item>
            <Item label="行业分类" name="industryType">
              <Cascader options={industry} showCheckedStrategy={SHOW_CHILD}></Cascader>
            </Item>
            <Item label="五级分类" name="level5Type">
              <Select options={allSelect.riskMetricLevel5} />
            </Item>
            <Item label="客户名称" name="clientName">
              <Input />
            </Item>
            <Item label="所属集团" name="belongGroupName">
              <Input />
            </Item>
            <Item label="是否金控关联方" name="related">
              <Select options={allSelect.needReportEumn} />
            </Item>
          </SearchBar>
        }
        columns={columns}
        actions={[]}
      />
    </Page>
  )
}

export default observer(RiskMetricTimed)
