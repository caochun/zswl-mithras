import ReactECharts from 'echarts-for-react'
import * as echarts from 'echarts'
import numeral from 'numeral'
import styles from './index.less'
import { useMemo } from 'react'
import _ from 'lodash'
import config from '../config'
import { hexToRgba } from '@/utils'
import { observer } from '@zswl/admin'

const LineChart = ({
  loading,
  loadingOption,
  options,
  data = [],
  chartBoxStyle,
  unit = '万元',
  needGraphic = true,
  colorWheel = config.colorWheel,
}) => {
  const getOption = useMemo(() => {
    const defaultOption = {
      color: colorWheel,
      legend: {
        show: true,
        data: data?.map((item) => item.name),
      },
      grid: {
        top: 0,
        left: -20,
        right: 20,
        bottom: 20,
        containLabel: true,
      },
      xAxis: {
        axisLabel: {
          color: 'rgba(0,0,0,0.85)',
          margin: 16,
          interval: 0, //强制显示所有标签
          formatter: (params) => {
            let newParamsName = ''
            let paramsNameNumber = params.length
            let provideNumber = 10 // 一行显示几个字
            let rowNumber = Math.ceil(paramsNameNumber / provideNumber)
            if (paramsNameNumber > provideNumber) {
              for (let p = 0; p < rowNumber; p++) {
                let tempStr = ''
                let start = p * provideNumber
                let end = start + provideNumber
                if (p === rowNumber - 1) {
                  tempStr = params.substring(start, paramsNameNumber)
                } else {
                  tempStr = params.substring(start, end) + '\n'
                }
                newParamsName += tempStr
              }
            } else {
              newParamsName = params
            }
            return newParamsName
          },
        },
        axisLine: {
          show: false,
          lineStyle: {
            type: 'solid',
            color: 'rgba(37,88,230,1)',
          },
        },
        axisTick: {
          show: false,
        },
        data: (data?.[0]?.list || []).map(({ name }) => name),
      },
      tooltip: {
        trigger: 'axis',
        snap: true,
        axisPointer: {
          type: 'line',
          lineStyle: {
            type: 'solid',
            width: 1,
            color: 'rgba(37,88,230,1)',
          },
          crossStyle: {
            type: 'solid',
            width: 2,
            color: 'rgba(37,88,230,.6)',
          },
        },
        formatter: (params) => {
          return LineChart.tooltipFormat(params, unit)
        },
      },
      yAxis: {
        name: unit,
        nameGap: 24,
        nameTextStyle: {
          color: 'rgba(0,0,0,0.85)',
          align: 'right',
        },

        type: 'value',
        axisLine: {
          show: false,
        },
        axisLabel: {
          color: 'rgba(0,0,0,0.85)',
        },
        splitLine: {
          lineStyle: {
            color: 'rgba(236,237,240,1)',
          },
        },
      },
      series: data.map((v, index) => {
        return {
          name: v?.name,
          type: 'line',
          symbolSize: 0,
          data: v.list.map(({ value }) => value),
          emphasis: {
            focus: 'series',
          },
          smooth: true,
          areaStyle: needGraphic
            ? {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  { offset: 0, color: hexToRgba(colorWheel[index], 0.2) },
                  { offset: 1, color: hexToRgba(colorWheel[index], 0) },
                ]),
              }
            : null,
        }
      }),
    }
    return _.merge(defaultOption, options)
  }, [options, data])

  return (
    <div className={styles.chart} style={{ ...chartBoxStyle }}>
      <ReactECharts
        notMerge={true}
        lazyUpdate={true}
        style={{ width: '100%', height: '100%' }}
        loadingOption={loadingOption}
        showLoading={loading}
        option={getOption}
      />
    </div>
  )
}
LineChart.tooltipFormat = function (params, unit = '', noDot) {
  let str = `<div style="color: rgba(0,0,0,0.85);font-size: 12px;">${params[0].axisValue}</div>`
  params
    .sort((a, b) => b.data - a.data)
    .forEach((item) => {
      const { color, seriesName, data } = item
      str += `<div style="display: flex;align-items: center"><span style="display:inline-block;margin-right:6px;width:4px;height:4px;border-radius: 50%;background-color:${color}"></span><span style="margin-right: 10px;font-size:12px;color: rgba(0,0,0,0.85)">${seriesName}</span>${numeral(
        data?.value ?? data
      ).format(noDot ? '0,00' : '0,00.00')}${unit}</div>`
    })
  return str
}
export default observer(LineChart)
