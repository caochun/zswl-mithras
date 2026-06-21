import { observer } from '@zswl/admin'
import EChartsReact from 'echarts-for-react'
import styles from './index.less'
import { useState } from 'react'

const VisualizationTwoD = ({ pie, fxsl, warnPieData, opPieData }) => {
  const [activeTab, setActiveTab] = useState('warn')

  // 处理后端数据,将数据按照类型分组
  const processLineData = () => {
    const warnData = {}
    const opData = {}

    if (fxsl?.warnLineData) {
      Object.entries(fxsl.warnLineData).forEach(([date, value]) => {
        // 将日期格式转换为 MM/DD
        const formattedDate = new Date(date)
          .toLocaleDateString('zh-CN', {
            month: '2-digit',
            day: '2-digit',
          })
          .replace('/', '/')
        warnData[formattedDate] = value // 保留原始数据
      })
    }

    if (fxsl?.opLineData) {
      Object.entries(fxsl.opLineData).forEach(([date, value]) => {
        // 将日期格式转换为 MM/DD
        const formattedDate = new Date(date)
          .toLocaleDateString('zh-CN', {
            month: '2-digit',
            day: '2-digit',
          })
          .replace('/', '/')
        opData[formattedDate] = value // 保留原始数据
      })
    }

    // 获取所有不重复的日期并排序
    const allDates = [...new Set([...Object.keys(warnData), ...Object.keys(opData)])].sort()

    return {
      dates: allDates,
      warnData: allDates.map((date) => warnData[date] || 0),
      opData: allDates.map((date) => opData[date] || 0),
    }
  }

  const lineData = processLineData()

  // 处理饼图数据的函数
  const processPieData = (data) => {
    return Object.entries(data || {}).map(([name, value]) => ({
      name,
      value: (value * 100).toFixed(2), // 将小数转换为百分比
    }))
  }

  const lineChartOption = {
    grid: {
      top: 50,
      right: 30,
      bottom: 10,
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
      data: ['预警', '舆情'],
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
      minInterval: 1, // 设置y轴刻度间隔为整数
      axisLabel: {
        formatter: '{value}', // 显示整数标签
      },
      axisLine: { show: false },
      axisTick: { show: false },
      splitLine: { lineStyle: { color: '#E5E7EB' } },
    },
    series: [
      {
        name: '预警',
        type: 'line',
        smooth: true,
        data: lineData.warnData,
        itemStyle: { color: '#F45A5A' },
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
        data: lineData.opData,
        itemStyle: { color: '#396CFA' },
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

  const pieChartOption = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c}%',
    },
    legend: {
      type: 'scroll',
      orient: 'vertical',
      right: '5%',
      top: 'middle',
      itemWidth: 10,
      itemHeight: 10,
      pageButtonPosition: 'end',
      formatter: (name) => {
        const data = activeTab === 'warn' ? warnPieData : opPieData
        const value = data[name]
        return `${name}  ${(value * 100).toFixed(2)}%`
      },
    },
    color: [
      '#396CFA', // 蓝色
      '#4ECB73', // 绿色
      '#FAD337', // 黄色
      '#37CBCB', // 青色
      '#9B63F3', // 紫色
      '#FF9F7F', // 橙色
      '#F45A5A', // 红色
      '#34C759', // 翠绿
      '#5856D6', // 深紫
      '#FF2D55', // 粉红
      '#AF52DE', // 淡紫
      '#007AFF', // 天蓝
    ],
    series: [
      {
        type: 'pie',
        radius: ['30%', '45%'],
        center: ['20%', '50%'],
        label: {
          show: false,
        },
        labelLine: {
          show: false,
        },
        data: activeTab === 'warn' ? processPieData(warnPieData) : processPieData(opPieData),
      },
    ],
  }

  return (
    <div className={styles.haderRight}>
      <div className={styles.charts}>
        <div className={styles.chartCard}>
          <div className={styles.chartTitle}>风险客户数量变化</div>
          <div
            style={{
              background:
                'linear-gradient(178.98deg, rgba(255, 255, 255, 0.8) 0.87%, rgba(255, 255, 255, 0.2) 47.6%, rgba(255, 255, 255, 0.8) 95.69%)',
              borderRadius: 4,
            }}
          >
            <EChartsReact option={lineChartOption} style={{ height: '137px' }} />
          </div>
        </div>
        <div className={styles.chartCard}>
          <div className={styles.chartTitle}>
            各部门占比
            <div className={styles.tabs}>
              <span
                className={`${styles.tab} ${activeTab === 'warn' ? styles.active : ''}`}
                onClick={() => setActiveTab('warn')}
              >
                预警
              </span>
              <span
                className={`${styles.tab} ${activeTab === 'op' ? styles.active : ''}`}
                onClick={() => setActiveTab('op')}
              >
                舆情
              </span>
            </div>
          </div>
          <div
            style={{
              background:
                'linear-gradient(178.98deg, rgba(255, 255, 255, 0.8) 0.87%, rgba(255, 255, 255, 0.2) 47.6%, rgba(255, 255, 255, 0.8) 95.69%)',
              borderRadius: 4,
            }}
          >
            <EChartsReact option={pieChartOption} style={{ height: '137px' }} />
          </div>
        </div>
      </div>
    </div>
  )
}

export default observer(VisualizationTwoD)
