import ReactECharts from 'echarts-for-react'
import { observer } from '@zswl/admin'
import { useEffect, useState } from 'react'
import LineChart from '@/components/Chart/LineChart'
import _, { flatMapDeep, set } from 'lodash'

const divorcer = 10000

const LiquidityChart = ({ dataSource, indicators, handleClick }) => {
  const [options, setOptions] = useState({})

  const [loading, setLoading] = useState(false)
  const formatData = () => {
    if (!dataSource?.length) {
      return {
        xAxisData: [],
        lineData: [],
        lineInterval: 0,
      }
    }

    const xAxisData = dataSource.map((item) => item.date)

    // 生成折线图数据
    const lineData = indicators.map(({ key, name }) => {
      return {
        name,
        type: 'line',
        label: {
          show: false,
          formatter: function (params) {
            return params.value.toFixed(2) + 'W'
          },
          position: 'left',
        },
        data: dataSource.map((item) => ({
          value: (item[key] || 0) / divorcer,
        })),
      }
    })

    // 计算y轴间隔
    const allValues = flatMapDeep(lineData, (series) => series.data.map((item) => item.value))
    const maxValue = Math.max(...allValues)
    const minValue = Math.min(...allValues)
    const lineInterval = Math.ceil(maxValue / 5)

    return {
      xAxisData,
      lineData,
      lineInterval,
      maxValue,
      minValue,
    }
  }

  const getBarData = async () => {
    setLoading(true)
    try {
      const { xAxisData, lineData, lineInterval, maxValue, minValue } = formatData()

      const newOptions = {
        color: ['#5088FF', '#48F0AF', '#FFD62A'],
        legend: {
          show: false,
        },

        toolbox: {
          top: 50,
        },
        tooltip: {
          trigger: 'axis',
          formatter: (params) => {
            return LineChart.tooltipFormat(params, '万元')
          },
        },
        // dataZoom: [
        //   {
        //     type: 'inside',
        //     start: 0,
        //     end: 20,
        //   },
        //   {
        //     start: 0,
        //     end: 20,
        //   },
        // ],
        xAxis: {
          data: xAxisData,
          axisLine: { onZero: true },
          axisLabel: {
            show: true,
          },
          splitLine: { show: false },
          splitArea: { show: false },
        },
        yAxis: [
          {
            type: 'value',
            name: '万元',
            axisLabel: {
              formatter: (value) => {
                return value.toFixed(2)
              },
            },
            // interval: lineInterval,
            max: maxValue,
            min: minValue,
          },
        ],
        grid: {
          left: 20,
          top: 70,
          right: 20,
          bottom: '5%',
          containLabel: true,
        },
        series: lineData,
      }
      setOptions(newOptions)
      setLoading(false)
    } catch (e) {
      setLoading(false)
    }
  }
  useEffect(() => {
    getBarData()
  }, [dataSource])

  return (
    <div style={{ height: 400 }}>
      <ReactECharts
        notMerge={true}
        lazyUpdate={true}
        style={{ width: '100%', height: '100%' }}
        option={options}
        showLoading={loading}
        onEvents={{
          click: (params) => {
            handleClick?.(params)
          },
        }}
      />
    </div>
  )
}

export default observer(LiquidityChart)
