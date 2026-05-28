import moment from 'moment'
import { App } from '@zswl/components'
import { message } from 'antd'
import { flatMapDeep } from 'lodash'

// 默认查询时间格式化
export const dataFormat = 'yyyy-MM-DD'

// 格式化，选中月的起始日-选中月的结束日
export const formatQueryDate = ({
  dateRange = [],
  dataIndex = 'processStartDate',
  startField = 'processStartDateFrom',
  endField = 'processStartDateTo',
}) => {
  return {
    [dataIndex]: undefined,
    [startField]: dateRange[0]?.format(dataFormat),
    [endField]: dateRange[1]?.endOf('months').format(dataFormat),
  }
}

// 默认公共产业类型
// export const initType = 'PUBLIC_CATEGORY'
export const initType = 'INDUSTRY_CATEGORY'

// 默认查询时间范围，本月的起始日-本月的结束日
export const initQueryDate = [moment().startOf('month'), moment().endOf('month')]

export const initYearQueryDate = [moment().startOf('year'), moment().endOf('year')]

export const lineSeriesItem = (item) => {
  return {
    name: '',
    type: 'line',
    yAxisIndex: 1,
    smooth: false,
    data: [],
    symbol: 'emptyCircle',
    symbolSize: 6,
    endLabel: {
      show: false,
    },
    xAxis: {
      axisLine: {
        show: false,
      },
    },
    ...item,
  }
}

export const barSeriesItem = (item) => {
  return {
    yAxisIndex: 0,
    type: 'bar',
    barWidth: 15,
    barGap: '30%',
    // label: { show: true, position: 'top' },
    data: [],
    ...item,
  }
}
