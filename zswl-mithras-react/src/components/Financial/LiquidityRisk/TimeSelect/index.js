import { observer } from '@zswl/admin'
import { useEffect, useMemo, useState } from 'react'
import { DatePicker, Radio } from 'antd'
import moment from 'moment'
import style from './index.less'

const getTDate = (num) => {
  return [moment().format('yyyy-MM-DD'), moment().add(num, 'days').format('yyyy-MM-DD')].join('~')
}

// T5、T10、T30、T45
const options = [
  { label: 'T5', value: getTDate(5) },
  { label: 'T10', value: getTDate(10) },
  { label: 'T30', value: getTDate(30) },
  { label: 'T45', value: getTDate(45) },
]

const rangePresets = {
  T60: [moment(), moment().add(60, 'days')],
  T90: [moment(), moment().add(90, 'days')],
  T180: [moment(), moment().add(180, 'days')],
}

function FinancialLiquidityRiskTimeSelect({ path, onChange, value }) {
  const [radioValue, setRadioValue] = useState(getTDate(5))
  const RadioChange = (e) => {
    setRadioValue(e.target.value)
    onChange?.(e.target.value)
  }
  // 用户操作日为第0日，T1为操作日的后一天，截止时间为23：59：59。T5为0~5天，代表操作日当天与后连续5天。
  const timeChange = (date) => {
    const newRadioValue = date.map((item) => item.format('yyyy-MM-DD')).join('~')
    setRadioValue(newRadioValue)
    onChange?.(newRadioValue)
  }
  useEffect(() => {
    onChange(getTDate(5))
  }, [])
  return (
    <div className={style.wrap}>
      <Radio.Group
        options={options}
        onChange={RadioChange}
        value={radioValue}
        optionType="button"
        buttonStyle="solid"
      />
      <DatePicker.RangePicker
        onChange={timeChange}
        value={radioValue.split('~').map((item) => moment(item, 'yyyy-MM-DD'))}
        style={{ marginLeft: 12 }}
        disabled={[false, false]}
        // disabledDate={(current) => {
        //   return current && current < moment().endOf('day').subtract(1, 'days')
        // }}
        ranges={rangePresets}
      />
    </div>
  )
}
FinancialLiquidityRiskTimeSelect.getTNum = (value) => {
  const [start, end] = value.split('~')
  return moment(end).diff(start, 'day')
}
export default observer(FinancialLiquidityRiskTimeSelect)
