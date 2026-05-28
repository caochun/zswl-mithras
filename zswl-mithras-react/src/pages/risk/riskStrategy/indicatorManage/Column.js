import { FiledFormat } from '@/components/Format'
import math from '@/utils/math'
import { App } from '@zswl/components'
import { Radio, Tooltip } from 'antd'
import _ from 'lodash'
import FormWaringSet from './FormWaringSet'
import moment from 'moment'

const valueFormat = ({ value, unit }) => {
  let newValue = math.toNonExponentialPlus(math.format(math.divide(value, 10000)))
  if (unit === '%') newValue = math.toNonExponentialPlus(math.format(math.multiply(newValue, 100)))
  return `${newValue}${unit}`
}
// comapre two number
const compareNumber = (a, b, symbol) => {
  const numA = +a
  const numB = +b
  if (b === null) {
    return true
  }
  if (symbol === '>=') {
    return numA >= numB
  } else if (symbol === '<=') {
    return numA <= numB
  }
}
const { optionsType } = App.getData()

const ALL_COLUMNS = [
  { title: '指标编号', dataIndex: 'metricCode' },
  { title: '指标类型', dataIndex: 'metricType', matchOption: 'metricType', width: 180 },
  { title: '指标名称', dataIndex: 'metricName', width: 300 },
  {
    title: '指标类别',
    dataIndex: 'metricCategory',
    matchOption: 'metricCategory',
    width: 100,
  },
  // 预警监测状态
  {
    title: '预警监测状态',
    dataIndex: 'earlyWarningState',
    matchOption: 'earlyWarningState',
    width: 120,
    span: 2,
    render: (val) => {
      const title = App.matchOption('earlyWarningState', val).label
      return <span style={{ color: val === 1 ? 'green' : 'red' }}>{title}</span>
    },
    editable: (val) => {
      return {
        element: <Radio.Group options={optionsType?.earlyWarningState ?? []} />,
        initialValue: val?.earlyWarningState,
      }
    },
  },
  {
    title: '当前值',
    dataIndex: 'currentValueOne',
    render: (
      val,
      {
        valueUnitOne,
        valueUnitTwo,
        limitValueOne,
        limitValueTwo,
        comparisonMethodOne,
        comparisonMethodTwo,
        currentValueOne,
        currentValueTwo,
        nullReason,
      }
    ) => {
      const newValue = [
        {
          value: currentValueOne,
          symbol: comparisonMethodOne,
          waringValue: limitValueOne,
          unit: valueUnitOne,
        },
        {
          value: currentValueTwo,
          symbol: comparisonMethodTwo,
          waringValue: limitValueTwo,
          unit: valueUnitTwo,
        },
      ]
        .filter((v) => _.isNumber(v.value))
        .map(({ value, unit, waringValue, symbol }, i) => {
          const title = valueFormat({ value, unit })
          const isChange = !compareNumber(value, waringValue, symbol)
          return (
            <span key={title}>
              <span> {i !== 0 && '、'}</span>
              <span style={{ color: isChange && 'red' }}>{title}</span>
            </span>
          )
        })

      const title = newValue.length ? newValue : nullReason
      return <Tooltip title={title}>{title}</Tooltip>
    },
    span: 3,
    width: 160,
  },
  {
    title: '限额值',
    dataIndex: 'limitValueOne',
    render: (val, { limitValueOne, valueUnitOne, limitValueTwo, valueUnitTwo }) => {
      const newValue = [
        { value: limitValueOne, unit: valueUnitOne },
        { value: limitValueTwo, unit: valueUnitTwo },
      ]
        .filter((v) => _.isNumber(v.value))
        .map(({ value, unit }, i) => valueFormat({ value, unit }))
        .join('、')

      return <FiledFormat title={newValue} />
    },
    width: 160,
  },
  {
    title: '限额值设定',
    dataIndex: 'limitValue',
    render: (val, { limitValueOne, valueUnitOne, limitValueTwo, valueUnitTwo }) => {
      const newValue = [
        { value: limitValueOne, unit: valueUnitOne },
        { value: limitValueTwo, unit: valueUnitTwo },
      ]
        .filter((v) => _.isNumber(v.value))
        .map(({ value, unit }, i) => valueFormat({ value, unit }))
        .join('、')
      return <FiledFormat title={newValue} />
    },
    width: 160,
    editable: ({ limitValue }) => <FormWaringSet name="limitValue" value={limitValue} />,
  },
  {
    title: '预警值',
    dataIndex: 'earlyWarning',
    render: (val, { earlyWarningValueOne, valueUnitOne, earlyWarningValueTwo, valueUnitTwo }) => {
      const newValue = [
        { value: earlyWarningValueOne, unit: valueUnitOne },
        { value: earlyWarningValueTwo, unit: valueUnitTwo },
      ]
        .filter((v) => _.isNumber(v.value))
        .map(({ value, unit }, i) => valueFormat({ value, unit }))
        .join('、')

      return <FiledFormat title={newValue} />
    },
    width: 160,
    editable: ({ earlyWarningValue }) => (
      <FormWaringSet name="earlyWarningValue" text="预警值" value={earlyWarningValue} />
    ),
  },
  {
    title: '单位',
    dataIndex: 'valueUnitOne',
    render: (val, record) => {
      const newValue = [record.valueUnitOne, record.valueUnitTwo]
        .filter((v) => _.isNumber(v))
        .join('、')
      return <FiledFormat title={newValue} />
    },
    width: 80,
  },
  {
    title: '更新时间',
    dataIndex: 'updateTime',
    type: 'rangePicker',
    dateFormat: 'yyyy-MM-DD HH:mm:ss',
    itemProps: {
      transform: (val) => {
        const [startDataTime, endDataTime] = val || []
        return {
          updateTime: undefined,
          updateTimeFrom: startDataTime?.format('yyyy-MM-DD'),
          updateTimeTo: endDataTime?.format('yyyy-MM-DD'),
        }
      },
    },
    width: 200,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    dateFormat: 'yyyy-MM-DD HH:mm:ss',
  },
  {
    title: '计算说明',
    dataIndex: 'computationalLogic',
    span: 3,
  },
  //'数据时点'
  {
    dataIndex: 'date',
    title: '数据时点',
    width: 200,
    type: 'datePicker',
    dateFormat: 'yyyy-MM-DD HH:mm:ss',
    disabledDate: (current) => {
      // Can not select days before today and today
      return current && current > moment().subtract(1, 'day')
    },
    itemProps: {
      transform: (val) => ({
        date: val?.format('yyyy-MM-DD'),
      }),
    },
  },
]

export default ALL_COLUMNS
