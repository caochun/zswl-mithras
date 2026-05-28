import { observer } from '@zswl/admin'
import EChartsReact from 'echarts-for-react'
import styles from './index.less'

const VisualizationTowD = () => {
  const lineChartOption = {
    // backgroundColor:
    //   'linear-gradient(178.98deg, rgba(255, 255, 255, 0.8) 0.87%, rgba(255, 255, 255, 0.2) 47.6%, rgba(255, 255, 255, 0.8) 95.69%)',
    grid: {
      top: 40,
      right: 30,
      bottom: 10,
      left: 40,
      containLabel: true,
    },
    tooltip: { trigger: 'axis' },
    legend: {
      data: ['红灯', '黄灯', '舆情'],
      //   right: 10,
      center: 0,
      top: 10,
    },
    xAxis: {
      type: 'category',
      data: ['08/01', '08/02', '08/03', '08/04', '08/05'],
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
        data: [12, 15, 18, 10, 5],
        itemStyle: { color: '#ff4d4f' },
        lineStyle: { width: 2 },
      },
      {
        name: '黄灯',
        type: 'line',
        smooth: true,
        data: [10, 12, 8, 15, 6],
        itemStyle: { color: '#faad14' },
        lineStyle: { width: 2 },
      },
      {
        name: '舆情',
        type: 'line',
        smooth: true,
        data: [5, 8, 6, 10, 4],
        itemStyle: { color: '#1890ff' },
        lineStyle: { width: 2 },
      },
    ],
  }

  const pieChartOption = {
    tooltip: { trigger: 'item' },
    legend: {
      orient: 'vertical',
      right: '5%',
      top: 'middle',
      itemWidth: 10,
      itemHeight: 10,
      formatter: (name) => {
        const data = {
          司法风险预警: 48,
          工商信息变更: 36,
          经营风险预警: 45,
        }
        return `${name} ${data[name]}`
      },
    },
    series: [
      {
        type: 'pie',
        radius: ['50%', '70%'],
        center: ['30%', '50%'],
        label: { show: false },
        data: [
          { value: 48, name: '司法风险预警' },
          { value: 36, name: '工商信息变更' },
          { value: 45, name: '经营风险预警' },
        ],
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
            <EChartsReact option={lineChartOption} style={{ height: '140px' }} />
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
            <EChartsReact option={pieChartOption} style={{ height: '140px' }} />
          </div>
        </div>
      </div>
    </div>
  )
}

export default observer(VisualizationTowD)
