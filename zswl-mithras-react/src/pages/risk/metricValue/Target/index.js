import { observer } from '@zswl/admin'
import { Page, Table, SearchBar, Select } from '@zswl/components'
import { DatePicker, Input, Tooltip } from 'antd'
import { useEffect, useMemo } from 'react'
import { AmountEditable, formatAmountWan } from '@/components/Format'
import { hasValue, getKeyOptionsLabelMap, rules } from '@/utils'
import store from './store'
import moment from 'moment'
import styles from './index.less'
import { saveServer } from '@/utils'

const { Item } = SearchBar

const date = new Date()
const year = date.getFullYear()
const mouth = date.getMonth()

const Index = () => {
  const { allSelect, lastReportTime, editIndex, valNeedFormatUnit } = store
  useEffect(() => {
    store.getSelect()
  }, [])
  const columns = useMemo(() => {
    const baseColumns = [
      {
        title: '是否必填',
        width: 100,
        dataIndex: 'needReport',
        render: (v) => {
          return getKeyOptionsLabelMap('needReportEumn', allSelect)[v] || '-'
        },
      },
      {
        title: '指标编号',
        dataIndex: 'metricCode',
      },
      {
        title: '指标名称',
        dataIndex: 'metricName',
        width: 420,
      },
      {
        title: '当期值(系统计算)',
        dataIndex: 'metricValue',
        align: 'right',
        render: (value, { unit }) => {
          return valNeedFormatUnit.includes(unit) && !window.isNaN(value) ? (
            formatAmountWan(value)
          ) : hasValue(value) ? (
            <Tooltip title={value}>{value}</Tooltip>
          ) : (
            '-'
          )
        },
      },
      {
        title: '当期值(人工修正)',
        dataIndex: 'metricValueAdjusted',
        align: 'right',
        editable: (record, rowIndex) => {
          return editIndex === rowIndex
            ? valNeedFormatUnit.includes(record.unit) && !window.isNaN(record.metricValueAdjusted)
              ? AmountEditable(record, 'metricValueAdjusted', {
                  required: true,
                  disabled: false,
                })
              : {
                  element: <Input />,
                  rules: [rules.required()],
                }
            : false
        },
        render: (value, { unit }) => {
          return valNeedFormatUnit.includes(unit) && !window.isNaN(value)
            ? formatAmountWan(value)
            : hasValue(value)
            ? value
            : '-'
        },
      },
      {
        title: '单位',
        dataIndex: 'unit',
        width: 120,
        render: (v) => {
          return getKeyOptionsLabelMap('riskMetricUnit', allSelect)[v] || '-'
        },
      },
      {
        title: '状态',
        dataIndex: 'status',
        width: 120,
        render: (v) => {
          return getKeyOptionsLabelMap('riskMetricStatus', allSelect)[v] || '-'
        },
      },
      {
        title: '报送频率',
        dataIndex: 'frequency',
        width: 120,
        render: (v) => {
          return getKeyOptionsLabelMap('riskMetricFrequency', allSelect)[v] || '-'
        },
      },
      {
        title: '状态',
        dataIndex: 'status',
        width: 120,
        render: (v) => {
          return getKeyOptionsLabelMap('riskMetricStatus', allSelect)[v] || '-'
        },
      },
      {
        title: '操作',
        width: 120,
        fixed: 'right',
        actions(record, rowIndex) {
          return [
            editIndex !== rowIndex && {
              name: '编辑',
              key: 'edit',
              onClick: () => store.editItem({ rowIndex }),
            },
            editIndex === rowIndex && {
              name: '取消',
              key: 'cancel',
              onClick: () => store.cancelEdit({ rowIndex }),
            },
            editIndex === rowIndex && {
              name: '确定',
              key: 'confirm',
              onClick: () => store.confirmEdit({ record, rowIndex }),
            },
          ].filter(Boolean)
        },
      },
    ]

    return baseColumns
  }, [allSelect, editIndex])
  return (
    <div>
      <Table
        columnsFilter={'metricValue_Target_1'}
        onFilter={(key, val) => saveServer('metricValue_Target_1', val)}
        columnWidth={180}
        resizable
        title={() => {
          return (
            <div className={styles.tableTitle}>
              <span>优先取人工填写数据报送</span>
              <span>上次批量报送时间：{lastReportTime || '-'}</span>
            </div>
          )
        }}
        scroll={{ x: 1200 }}
        store={store.table}
        extra={[
          {
            name: <span>系统计算</span>,
            onClick: store.calc,
            type: 'primary',
          },
          {
            name: <span>批量报送</span>,
            onClick: store.batchReport,
            type: 'primary',
          },
        ]}
        searchbar={
          <SearchBar
            labelCol={{ span: 7 }}
            limit="6"
            initialValues={{
              dataTime: moment().year(year).month(mouth),
            }}
          >
            <Item label="数据时点" name="dataTime">
              <DatePicker picker={'month'} allowClear={false} />
            </Item>
            <Item label="指标名称" name="metricName">
              <Input />
            </Item>
            <Item label="指标编号" name="metricCode">
              <Input />
            </Item>
            <Item label="是否必填" name="needReport">
              <Select options={allSelect.needReportEumn} />
            </Item>
          </SearchBar>
        }
        columns={columns}
      />
    </div>
  )
}

export default observer(Index)
