import EChartsReact from 'echarts-for-react'
import styles from './styles.less' // 使用 CSS Modules
import { observer } from '@zswl/admin'

const VisualizationHistory = ({ store }) => {
  // 处理外部数据，将其转换为图表所需格式
  const processData = (rawData = {}) => {
    const dates = Object.keys(rawData).map((date) => date.replace('-', '/')) // 格式化日期为 "YYYY/MM"
    const values = Object.values(rawData).map((value) => {
      return value >= 100000000 ? value / 100000000 : value
    })
    return { dates, values }
  }

  // 假设 `data` 是外部传入的 JSON 数据
  const { dates, values } = processData(store?.creditHistory ?? {})

  // 计算Y轴最大值和间隔
  const maxValue = Math.max(...values)
  const yAxisMax = Math.ceil(maxValue * 1.2) // 最大值增加20%的空间
  const yAxisInterval = Math.ceil(yAxisMax / 5) // 将Y轴分成5个区间

  // 构建图表配置项
  const historyChartOption = {
    grid: {
      top: 10,
      right: 20,
      bottom: 30,
      left: 40,
      containLabel: true,
    },
    tooltip: {
      trigger: 'axis',
      formatter: function (params) {
        const value = params[0].value
        return `${params[0].axisValue}<br/>${value}`
      },
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dates,
      axisLine: { lineStyle: { color: '#CCCCCC' } },
      axisLabel: {
        color: '#666666',
        fontSize: 12,
        formatter: (value) => {
          return value.replace(/-/g, '/')
        },
      },
    },
    yAxis: {
      type: 'value',
      splitLine: {
        lineStyle: {
          color: '#E5E7EB',
          type: 'dashed',
        },
      },
      axisLabel: {
        color: '#666666',
        fontSize: 12,
        formatter: (value) => {
          return value
        },
      },
      min: 0,
      max: yAxisMax,
      interval: yAxisInterval,
    },
    series: [
      {
        type: 'line',
        smooth: true,
        symbol: 'circle', // 设置数据点为圆形
        symbolSize: 6, // 设置数据点大小
        itemStyle: {
          color: '#1890FF',
          borderWidth: 2,
          borderColor: '#fff', // 数据点白色边框
        },
        lineStyle: {
          color: '#1890FF',
          width: 2,
        },
        label: {
          show: true,
          position: 'top',
          formatter: '{c}',
          color: '#666666',
          fontSize: 12,
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              {
                offset: 0,
                color: 'rgba(24, 144, 255, 0.2)',
              },
              {
                offset: 1,
                color: 'rgba(24, 144, 255, 0)',
              },
            ],
          },
        },
        data: values,
      },
    ],
  }

  return (
    <div className={styles['history-chart-container']}>
      <div className={styles['chart-title']}>
        历史授信情况
        <span style={{ float: 'right', fontSize: '12px', color: '#666' }}>单位：万元</span>
      </div>
      <div className={styles['chart-content']}>
        <EChartsReact option={historyChartOption} style={{ height: '180px', width: '100%' }} />
      </div>
    </div>
  )
}

export default observer(VisualizationHistory)
