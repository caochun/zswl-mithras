import moment from 'moment'
import { App } from '@zswl/components'
import { message } from 'antd'
import { flatMapDeep } from 'lodash'
import { amountFormat } from '@/utils'

// x轴部门名称格式化
export const formatDepartName = (value) => {
  let depart = value.replace('业务部', '').replace('团队', '')
  if (depart.length === 2) {
    depart += '业务'
  }
  return depart
}

// 默认项目阶段
export const initProjStage = 'PROJECT_STAGE_ESTABLISH'

// 默认公共产业类型
// export const initType = 'PUBLIC_CATEGORY'
export const initType = 'INDUSTRY_CATEGORY'

// 默认查询时间范围，本月的起始日-本月的结束日
export const initQueryDate = [moment().startOf('month'), moment().endOf('month')]

export const initYearQueryDate = [moment().startOf('year'), moment().endOf('year')]

// 默认查询时间格式化
export const dataFormat = 'yyyy-MM-DD'

// 格式化，选中月的起始日-选中月的结束日
export const formatQueryDate = ({
  dateRange = [],
  dataIndex = 'queryDate',
  startField = 'queryDateFrom',
  endField = 'queryDateTo',
}) => {
  return {
    [dataIndex]: undefined,
    [startField]: dateRange[0]?.startOf('month').format(dataFormat),
    [endField]: dateRange[1]?.endOf('month').format(dataFormat),
  }
}

export const sameYearQueryDate = (queryDate) => {
  const startYear = queryDate[0].format('YYYY')
  const endYear = queryDate[1].format('YYYY')
  if (startYear !== endYear) {
    message.info('不支持跨年份选择月份')
    return false
  }
  return true
}

export const getOperationStatisticStage = (dataIndex) => {
  return App.matchOption('workbenchOperationStatistics', dataIndex)?.label
}

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

export const RenderTooltip = ({ params, style }) => {
  return (
    <div style={{ display: 'flex', width: 400, flexWrap: 'wrap', ...style }}>
      {params.map(({ color, data = {}, seriesName }, index) => {
        return (
          <div
            key={index}
            style={{
              marginBottom: 5,
              display: 'flex',
              alignItems: 'center',
              width: '50%',
            }}
          >
            <span
              style={{
                display: 'inline-block',
                marginRight: '6px',
                width: '12px',
                height: '12px',
                backgroundColor: color,
              }}
            ></span>
            <span
              style={{
                fontSize: '14px',
                color: '#5e6066',
              }}
            >
              {seriesName}
            </span>
            <span>：</span>
            <span
              style={{
                fontSize: '14px',
                color: '#5e6066',
              }}
            >
              {amountFormat(data?.value ?? data)}
              {data?.unit ?? ''}
            </span>
          </div>
        )
      })}
    </div>
  )
}

export const getInterval = (arr, splitNumber) => {
  const flatArr = flatMapDeep(arr?.map((item) => item.data?.map((v) => v?.value ?? v)))

  const max = Math.ceil(Math.max(...flatArr))
  const min = Math.ceil(Math.min(...flatArr))

  const interval = Math.ceil((max - (min > 0 ? 0 : min)) / splitNumber)
  return { max, min, interval }
}
