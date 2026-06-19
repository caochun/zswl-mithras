import { observer } from '@zswl/admin'
import { Page, Table, SearchBar, Select } from '@zswl/components'
import { DatePicker, Input, Tooltip } from 'antd'
import { useEffect, useMemo } from 'react'
import { AmountEditable, InputEditable, InputNumberEditable, formatAmountWan } from '@/components/Format'
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
        title: '指标名称',
        dataIndex: 'metricName',
        width: 200,
        actions: ({ metricName: name, id }) => [
          { name, to: `/risk/cloudMetricValue/detail/${id}` },
        ],
      },

      {
        title: '一级分类',
        dataIndex: 'oneLevelType',
        width: 120,
      },
      {
        title: '二级分类',
        dataIndex: 'twoLevelType',
        width: 160,
      },
      {
        title: '指标大类',
        dataIndex: 'metricFirstType',
        width: 120,
      },
      {
        title: '指标小类',
        dataIndex: 'metricSecondType',
        width: 180,
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
                  rules: [
                    rules.required(),
                    {
                      validator(r, value) {
                        if (hasValue(value)) {
                          const valStr = value + ''
                          const index = valStr.indexOf('.')
                          if (index > 0 && valStr.substring(index + 1).length > 2) {
                            return Promise.reject('小数点后不能超过2位')
                          }
                        }
                        return Promise.resolve()
                      },
                    },
                  ],
                })
              : {
                  element: <Input.TextArea maxLength={500} />,
                  rules: [rules.required()],
                  style: {
                    marginBottom: 12,
                  },
                }
            : false
        },
        render: (value, { unit }) => {
          return valNeedFormatUnit.includes(unit) && !window.isNaN(value) ? (
            formatAmountWan(value)
          ) : hasValue(value) ? (
            <Tooltip title={value} placement="left">
              {value}
            </Tooltip>
          ) : (
            '-'
          )
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
        title: '单位',
        dataIndex: 'unit',
        width: 120,
        render: (v) => {
          return getKeyOptionsLabelMap('riskMetricUnit', allSelect)[v] || '-'
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
    <Page>
      <Table
        columnsFilter={'risk_cloudMetricValue_1'}
        onFilter={(key, val) => saveServer('risk_cloudMetricValue_1', val)}
        columnWidth={180}
        resizable
        rowClassName={(record, index) => {
          if (record.isRed) {
            return styles.redRow
          }
          return ''
        }}
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
            // labelCol={{ span: 7 }}
            limit="6"
            initialValues={{
              dataTime: moment().year(year).month(mouth),
            }}
          >
            <Item label="数据日期" name="dataTime">
              <DatePicker picker={'month'} allowClear={false} />
            </Item>
            <Item label="指标名称" name="metricName">
              <Input />
            </Item>
            <Item label="指标大类" name="metricFirstType">
              <Select options={allSelect.metricFirstType} />
            </Item>
            <Item label="报送频率" name="frequency">
              <Select options={allSelect.riskMetricFrequency} />
            </Item>
          </SearchBar>
        }
        columns={columns}
      />
    </Page>
  )
}

export default observer(Index)
