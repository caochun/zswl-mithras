import moment from 'moment'

export const rangePresets = {
  今天: [moment(), moment()],
  昨天: [moment().subtract('days', 1), moment().subtract('days', 1)],
  // 系统日的上一个自然月
  上个月: [
    moment().subtract('month', 1).startOf('month'),
    moment().subtract('month', 1).endOf('month'),
  ],
  近一周: [moment().subtract('days', 7), moment()],
  近两周: [moment().subtract('days', 14), moment()],
  近一个月: [moment().subtract('days', 30), moment()],
  近三个月: [moment().subtract('days', 90), moment()],
  近半年: [moment().subtract('days', 182), moment()], // 一年的一半
  近一年: [moment().subtract('days', 365), moment()],
  当月: [moment().startOf('month'), moment()],
  当季: [moment().startOf('quarter'), moment()],
  当年: [moment().startOf('year'), moment()],
  长期: [moment(), moment('2099-12-31')],
}

// antd 5 以上
// export const rangePresets = [
//   { label: '今天', value: [moment(), moment()] },
//   { label: '昨天', value: [moment().subtract('days', 1), moment().subtract('days', 1)] },
//   { label: '近一周', value: [moment().subtract('days', 7), moment()] },
//   { label: '近两周', value: [moment().subtract('days', 14), moment()] },
//   { label: '近一个月', value: [moment().subtract('days', 30), moment()] },
//   { label: '近三个月', value: [moment().subtract('days', 90), moment()] },
//   { label: '近半年', value: [moment().subtract('days', 182), moment()] }, // 一年的一},
//   { label: '近一年', value: [moment().subtract('days', 365), moment()] },
//   { label: '当月', value: [moment().startOf('month'), moment()] },
//   { label: '当季', value: [moment().startOf('quarter'), moment()] },
//   { label: '当年', value: [moment().startOf('year'), moment()] },
// ]
