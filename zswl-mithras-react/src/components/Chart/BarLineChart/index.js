import ReactECharts from 'echarts-for-react'
import _ from 'lodash'
import numeral from 'numeral'
import { useMemo } from 'react'
import config from '../config'
import styles from './index.less'

const BarLineChart = ({ loading, loadingOption, data = [], options, chartBoxStyle }) => {
  const getOption = useMemo(() => {
    const defaultOption = {
      color: config.colorWheel,
      legend: {
        show: true,
        itemGap: 32,
        data: data?.map((item) => item.dataType),
      },
      grid: {
        top: '18%',
        left: 40,
        right: 40,
        bottom: 20,
        containLabel: true,
      },
      xAxis: {
        axisLabel: {
          color: 'rgba(0,0,0,0.85)',
          // margin: 16,
          // rotate: 2,
          interval: 0, //强制显示所有标签
        },

        axisLine: {
          lineStyle: {
            type: 'solid',
            color: 'rgba(37,88,230,1)',
          },
        },
        data: (data?.[0]?.list || []).map(({ name }) => name),
      },
      tooltip: {
        trigger: 'axis',
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
          return BarLineChart.tooltipFormat(params)
        },
      },
      yAxis: [
        {
          name: `个`,
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
        {
          nameTextStyle: {
            color: 'rgba(0,0,0,0.85)',
            align: 'left',
          },
          type: 'value',
          axisLine: {
            show: false,
          },
          axisLabel: {
            color: 'rgba(0,0,0,0.85)',
            formatter: '{value}%',
          },
          splitLine: {
            show: false,
          },
        },
      ],
      series: data.map((v) => {
        return {
          name: v.dataType,
          type: 'bar',
          data: (v?.list || []).map(({ value }) => value),
          emphasis: {
            focus: 'series',
          },
          hoverValue: (v?.list || []).map(({ hoverValue }) => hoverValue),
        }
      }),
    }
    return _.merge(defaultOption, options)
  }, [options])

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
BarLineChart.tooltipFormat = function (params, { unit, noDot }) {
  let str = `<div style="color: rgba(0,0,0,0.85);font-size: 12px;">${params[0].axisValue}金额</div>`
  params.forEach((item) => {
    const { color, seriesName, data, axisIndex, seriesIndex, dataIndex } = item
    const exhibit = _.isObject(data)
      ? data.hoverValue
      : `${numeral(data).format(noDot ? '0,00' : '0,00.00')}${unit}`
    str += `<div style="display: flex;align-items: center"><span style="display:inline-block;margin-right:6px;width:4px;height:4px;border-radius: 50%;background-color:${color}"></span><span style="margin-right: 10px;font-size:12px;color: rgba(0,0,0,0.85)">${seriesName}</span>${exhibit}</div>`
  })
  return str
}
export default BarLineChart
