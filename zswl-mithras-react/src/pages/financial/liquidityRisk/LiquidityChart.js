import ReactECharts from 'echarts-for-react'
import { observer } from '@zswl/admin'
import { useEffect, useState } from 'react'
import Api from '@/api/liquidity/liquidityRiskApi'
import { BarChart } from '@/components/Chart/BarChartEntries'
import _, { flatMapDeep, set } from 'lodash'

const divorcer = 10000 * 10000

const LiquidityChart = ({ time = {}, store, ...rest }) => {
  const { days, timeFrom, timeTo } = time
  const { load, overdueRate } = store
  const [options, setOptions] = useState({})

  const emphasisStyle = {
    itemStyle: {
      shadowBlur: 10,
      shadowColor: 'rgba(0,0,0,0.3)',
    },
  }
  const formatBarData = (data = {}, defaultValues = {}) => {
    // 除数
    const { chartType, dataType, details } = data
    const bar1Name = {
      压力测试现金流: '压力测试-净流入',
      预估净现金流: '预估净现金流正值',
    }
    const bar2Name = {
      压力测试现金流: '压力测试-净流出',
      预估净现金流: '预估净现金流负值',
    }
    const bar1 = {
      type: chartType,
      name: bar1Name[dataType],
      data: details.map((item) => ({
        value: item.value > 0 ? item.value / divorcer : 0,
        name: item.name,
      })),
      emphasis: emphasisStyle,
      large: true,
      yAxisIndex: 1,
      ...defaultValues,
    }
    const bar2 = {
      type: chartType,
      name: bar2Name[dataType],
      barGap: '-50%',
      emphasis: emphasisStyle,
      data: details.map((item) => (item.value < 0 ? item.value / divorcer : 0)),
      large: true,
      yAxisIndex: 1,
      ...defaultValues,
    }
    return [bar1, bar2]
  }

  const [loading, setLoading] = useState(false)
  const getBarData = async () => {
    setLoading(true)
    console.log('timeFrom: ', timeFrom)
    try {
      const data = await Api.postChartQuery({ timeFrom, timeTo, estimatedOverdueRate: overdueRate })
      const lineData = data.slice(0, 4).map((item) => ({
        type: item.chartType,
        name: item.dataType === '预计现金流流入' ? '预估现金流流入' : item.dataType,
        data: item.details.map((v) => ({ name: v.name, value: v.value / divorcer })),
        large: true,
        yAxisIndex: 0,
      }))
      // 拆分成正值和负值 两组数据
      const barData1 = formatBarData(data[4], { barMaxWidth: 24, z: 0 })
      const barData2 = formatBarData(data[5], { barMaxWidth: 12, z: 1 })
      const barData = [barData1[0], barData2[0], barData1[1], barData2[1]]

      const legend = [
        '预估现金流流入',
        '预估现金流流出',
        '压力测试-流入',
        '压力测试-流出',
        '预估净现金流正值',
        '预估净现金流负值',
        '压力测试-净流入',
        '压力测试-净流出',
      ]
      const xAxisData = data[0].details.map((item) => item.name)
      const getInterval = (arr, splitNumber) => {
        const flatArr = flatMapDeep(arr.map((item) => item.data.map((v) => v?.value ?? v)))
        const max = Math.max(...flatArr)
        const min = Math.min(...flatArr)

        const interval = Math.ceil((max - (min > 0 ? 0 : min)) / splitNumber)
        return { max, min, interval }
      }
      const { interval: barInterval, min: barMin, max: barMax } = getInterval([...barData], 5)
      const { interval: lineInterval, min: lineMin, max: lineMax } = getInterval([...lineData], 5)
      const minIsZero = barMin >= 0 && lineMin >= 0
      // 根据legend数组排序

      const series = [...barData, ...lineData].sort((a, b) => {
        const aIndex = legend.findIndex((item) => item === a.name)
        const bIndex = legend.findIndex((item) => item === b.name)
        return aIndex - bIndex
      })

      const newOptions = {
        color: [
          '#39518A',
          '#FF5962',
          '#C1F035',
          '#985FF7',
          '#2C68FF',
          '#2BC6FF',
          '#31E8B4',
          '#FFCA69',
          '#43A8C7',
          '#FF9845',
        ],
        legend: {
          data: legend,
        },
        // brush: {
        //   toolbox: ['rect'],
        //   xAxisIndex: 0,
        // },
        toolbox: {
          // feature: {
          //   magicType: {
          //     type: ['stack'],
          //   },
          //   dataView: {},
          // },
          top: 50,
        },
        tooltip: {
          trigger: 'axis',
          formatter: (params) => {
            return BarChart.tooltipFormat(params, '万元')
          },
        },
        xAxis: {
          data: xAxisData,
          axisLine: { onZero: true },
          splitLine: { show: false },
          splitArea: { show: false },
        },
        yAxis: [
          {
            type: 'value',
            name: '万元',
            interval: lineInterval,
            max: (value) => {
              const { max, min } = value
              const absMax = Math.max(Math.abs(max), Math.abs(min))
              return Math.ceil(absMax)
            },
            min: (value) => {
              const { max, min } = value
              if (minIsZero) return 0
              const absMax = Math.max(Math.abs(max), Math.abs(min))
              return Math.floor(-absMax)
            },
          },
          {
            type: 'value',
            name: '万元',
            interval: barInterval,
            max: (value) => {
              const { max, min } = value
              const absMax = Math.max(Math.abs(max), Math.abs(min))
              return Math.ceil(absMax)
            },
            min: (value) => {
              const { max, min } = value
              if (minIsZero) return 0
              const absMax = Math.max(Math.abs(max), Math.abs(min))
              return Math.floor(-absMax)
            },
          },
        ],
        grid: {
          top: 100,
          bottom: '5%',
          containLabel: true,
        },
        series,
      }
      setOptions(newOptions)
      setLoading(false)
    } catch (e) {
      setLoading(false)
    }
  }
  useEffect(() => {
    if (timeFrom && timeTo) getBarData()
  }, [timeFrom, timeTo])
  useEffect(() => {
    load && getBarData()
  }, [load])
  return (
    <div style={{ height: 600 }}>
      <ReactECharts
        notMerge={true}
        lazyUpdate={true}
        style={{ width: '100%', height: '100%' }}
        option={options}
        showLoading={loading}
      />
    </div>
  )
}

export default observer(LiquidityChart)
