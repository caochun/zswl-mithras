import { observer } from '@zswl/admin'
import EChartsReact from 'echarts-for-react'
import styles from './index.less'

const VisualizationTowD = ({ pie, fxsl }) => {
  // 处理后端数据,将数据按照类型分组
  const processLineData = () => {
    const redLight = {}
    const yellowLight = {}
    const opinion = {}

    if (Array.isArray(fxsl)) {
      fxsl.forEach((item) => {
        if (item.cardDetail) {
          Object.entries(item.cardDetail).forEach(([date, value]) => {
            // 将日期格式转换为 MM/DD
            const formattedDate = new Date(date)
              .toLocaleDateString('zh-CN', {
                month: '2-digit',
                day: '2-digit',
              })
              .replace('/', '/')
            switch (item.cardCodeName) {
              case '红灯':
                redLight[formattedDate] = value
                break
              case '黄灯':
                yellowLight[formattedDate] = value
                break
              case '舆情':
                opinion[formattedDate] = value
                break
            }
          })
        }
      })
    }

    // 获取所有不重复的日期并排序
    const allDates = [
      ...new Set([...Object.keys(redLight), ...Object.keys(yellowLight), ...Object.keys(opinion)]),
    ].sort()

    return {
      dates: allDates,
      redLight: allDates.map((date) => redLight[date] || 0),
      yellowLight: allDates.map((date) => yellowLight[date] || 0),
      opinion: allDates.map((date) => opinion[date] || 0),
    }
  }

  const lineData = processLineData()

  const lineChartOption = {
    grid: {
      top: 60,
      right: 30,
      bottom: 30,
      left: 40,
      containLabel: true,
    },
    tooltip: {
      trigger: 'axis',
      formatter: function (params) {
        let result = params[0].axisValue + '<br/>'
        params.forEach((param) => {
          result += `${param.seriesName}: ${param.value}<br/>`
        })
        return result
      },
    },
    legend: {
      data: ['红灯', '黄灯', '舆情'],
      center: 0,
      top: 10,
    },
    xAxis: {
      type: 'category',
      data: lineData.dates,
      axisLine: { lineStyle: { color: '#CCCCCC' } },
      axisTick: { show: false },
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      axisTick: { show: false },
      splitLine: { lineStyle: { color: '#E5E7EB' } },
    },
    series: [
      {
        name: '红灯',
        type: 'line',
        smooth: true,
        data: lineData.redLight,
        itemStyle: { color: '#ff4d4f' },
        lineStyle: { width: 2 },
        symbol: 'circle',
        symbolSize: 8,
        label: {
          show: true,
          position: 'top',
          formatter: '{c}',
        },
      },
      {
        name: '黄灯',
        type: 'line',
        smooth: true,
        data: lineData.yellowLight,
        itemStyle: { color: '#faad14' },
        lineStyle: { width: 2 },
        symbol: 'circle',
        symbolSize: 8,
        label: {
          show: true,
          position: 'top',
          formatter: '{c}',
        },
      },
      {
        name: '舆情',
        type: 'line',
        smooth: true,
        data: lineData.opinion,
        itemStyle: { color: '#1890ff' },
        lineStyle: { width: 2 },
        symbol: 'circle',
        symbolSize: 8,
        label: {
          show: true,
          position: 'top',
          formatter: '{c}',
        },
      },
    ],
  }
  const isSmall = window.innerWidth < 1500
  const pieChartOption = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)',
    },
    grid: {
      top: 60,
      right: 30,
      bottom: 30,
      left: -30,
    },
    legend: {
      show: !isSmall,
      orient: 'vertical',
      right: '5%',
      top: 'middle',
      itemWidth: 10,
      itemHeight: 10,
      formatter: (name) => {
        const item = pie?.find((p) => p.cardCode === name)
        const total = pie?.reduce((sum, curr) => sum + curr.amount, 0) || 0
        const percentage = total ? (((item?.amount || 0) / total) * 100).toFixed(2) : 0
        return `${name} ${item?.amount || 0} (${percentage}%)`
      },
    },
    series: [
      {
        minAngle: 15,
        startAngle: 230,
        type: 'pie',
        radius: ['30%', '40%'],
        center: ['20%', '50%'],
        left: isSmall ? '40%' : '10%',
        bleedMargin: 20,
        label: {
          show: false
        },
        labelLine: {
          normal: {
            length: 5,
            smooth: true,
          },
        },
        data:
          pie?.map((item) => ({
            value: item.amount,
            name: item.cardCode,
          })) || [],
      },
    ],
  }
  return (
    <div className={styles.haderRight}>
      <div className={styles.charts}>
        <div className={styles.chartCard}>
          <div className={styles.chartTitle}>风险数量变化</div>
          <div
            style={{
              background:
                'linear-gradient(178.98deg, rgba(255, 255, 255, 0.8) 0.87%, rgba(255, 255, 255, 0.2) 47.6%, rgba(255, 255, 255, 0.8) 95.69%)',
              borderRadius: 4,
            }}
          >
            <EChartsReact option={lineChartOption} style={{ height: '200px' }} />
          </div>
        </div>
        <div className={styles.chartCard}>
          <div className={styles.chartTitle}>风险类型占比</div>

          <div
            style={{
              background:
                'linear-gradient(178.98deg, rgba(255, 255, 255, 0.8) 0.87%, rgba(255, 255, 255, 0.2) 47.6%, rgba(255, 255, 255, 0.8) 95.69%)',
              borderRadius: 4,
            }}
          >
            <EChartsReact option={pieChartOption} style={{ height: '200px' }} />
          </div>
        </div>
      </div>
    </div>
  )
}

export default observer(VisualizationTowD)
