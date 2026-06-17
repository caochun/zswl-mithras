import { observer } from '@zswl/admin'
import { useState } from 'react'
import { DatePicker, Spin } from 'antd'
import moment from 'moment'
import Pie from './Pie'
import styles from './index.less'

const yearFormat = 'yyyy'

const Index = ({ store }) => {
  const { quarter, year, changeQuarter, changeYear, quarterSelectData, quarterSelectLoading } =
    store
  const [selectQuarater, setSelectQuarater] = useState(quarter)
  const [selectYear, setSelectYear] = useState(year)

  const onRadioChange = ({ value, data }) => {
    if (quarter === value) {
      return
    }
    setSelectQuarater(value)
    changeQuarter(value)
  }
  const onDateChange = (value) => {
    setSelectYear(value.format(yearFormat))
    changeYear(value.format(yearFormat))
  }
  return (
    <div className={styles.quarterWrap}>
      <div className={styles.header}>
        <div className={styles.title}>资产五级分类</div>
        <DatePicker
          onChange={onDateChange}
          picker="year"
          value={moment(selectYear, yearFormat)}
          allowClear={false}
          // disabledDate={(current) => current < moment().year()}
        />
      </div>
      <div className={styles.piePanel}>
        {quarterSelectData.length > 0 ? (
          quarterSelectData.map((item, index) => {
            return (
              <div
                className={styles.pieItem}
                key={`${selectYear}_${index}`}
                onClick={() =>
                  onRadioChange({ value: item.quarter, data: item.classificationAmounts })
                }
              >
                <Pie
                  selectYear={selectYear}
                  domId={`${selectYear}_${item.quarter}`}
                  quarater={index + 1}
                  selectQuarater={selectQuarater}
                  data={item.classificationAmounts ?? []}
                ></Pie>
              </div>
            )
          })
        ) : (
          <div className={styles.spin}>
            <Spin></Spin>
          </div>
        )}
      </div>
    </div>
  )
}

export default observer(Index)
