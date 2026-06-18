import { AmountColumn, TextAreaColumn } from '@/components/Format'
import { hasValue } from '@/utils'

export const tableRequired = () => ({
  validator(r, value) {
    const errList = []
    let whetherToFillIn = true
    ;(value || []).forEach((v) => {
      const isFilled = ['currentPeriod', 'samePeriodLastYear', 'growthRate'].every((key) =>
        hasValue(v[key])
      )
      if (!isFilled && !hasValue(v['remark'])) {
        errList.push(v.subject)
        whetherToFillIn = false
      }
    })
    if (!whetherToFillIn) return Promise.reject(`请填写${errList.join('、')}的说明`)
    return Promise.resolve()
  },
})

const wrapItemProps = {
  rules: [],
}

export const columns = [
  { title: '科目', dataIndex: 'subject', editable: false },
  AmountColumn({ title: '本期', dataIndex: 'currentPeriod', editable: true, wrapItemProps }),
  AmountColumn({
    title: '上年同期',
    dataIndex: 'samePeriodLastYear',
    editable: true,
    wrapItemProps,
  }),
  AmountColumn({ title: '同比/增减率', dataIndex: 'growthRate', editable: true, wrapItemProps }),
  TextAreaColumn({ title: '说明', dataIndex: 'remark', editable: true }),
]
export const columns2 = [
  { title: '数据及指标', dataIndex: 'subject', editable: false, width: 150 },
  AmountColumn({ title: '本期', dataIndex: 'currentPeriod', editable: true, wrapItemProps }),
  AmountColumn({
    title: '上年同期',
    dataIndex: 'samePeriodLastYear',
    editable: true,
    wrapItemProps,
  }),
  AmountColumn({
    title: '同比/增减率',
    dataIndex: 'growthRate',
    editable: true,
    width: 140,
    wrapItemProps,
  }),
  TextAreaColumn({
    title: '说明',
    dataIndex: 'remark',
    editable: true,
    width: 180,
  }),
]
