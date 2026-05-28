import { observer } from '@zswl/admin'
import ReactECharts from 'echarts-for-react'

const RadarChart = ({ dataSource }) => {
  const getRadarOption = (data) => {
    if (!data?.peers?.length) return {}

    const { peers } = data
    const showCompanies = ['浙江浙商融资租赁有限公司', '行业平均']
    const indicators = [
      { name: '总资产' },
      { name: '净资产' },
      { name: '净利润' },
      { name: '杠杆率' },
      { name: 'ROE' },
      { name: 'ROA' },
    ]

    // 获取所有企业名称
    const companies = showCompanies

    // 生成每个公司的雷达图数据
    const seriesData = peers
      .filter((peer) => showCompanies.includes(peer.enterpriseName))
      .map((peer) => ({
        name: peer.enterpriseName,
        value: [
          peer.totalAssets,
          peer.netAssets,
          peer.netProfit,
          peer.leverageRatio,
          peer.roe,
          peer.roa,
        ],
        areaStyle: {
          color: 'rgba(145, 204, 117, 0.2)',
        },
      }))

    return {
      tooltip: {
        trigger: 'item',
        position: 'right',
        formatter: (params) => {
          const { name, value } = params
          let html = `${name}<br/>`
          indicators.forEach((indicator, index) => {
            let displayValue = value[index]
            // 根据不同指标格式化显示值
            switch (indicator.name) {
              case 'ROA':
              case 'ROE':
              case '杠杆率':
                displayValue = displayValue + '%'
                break
              case '总资产':
              case '净资产':
              case '净利润':
                displayValue = displayValue + '亿元'
                break
            }
            html += `${indicator.name}: ${displayValue}<br/>`
          })
          return html
        },
      },
      legend: {
        data: companies,
      },
      radar: {
        indicator: indicators,
        splitNumber: 4,
        radius: '70%',
        center: ['50%', '50%'],
        axisName: {
          color: '#999',
          fontSize: 12,
        },
        splitLine: {
          lineStyle: {
            color: ['#E6E6E6'],
          },
        },
        splitArea: {
          show: false,
        },
      },
      series: [
        {
          type: 'radar',
          data: seriesData,
        },
      ],
    }
  }

  const option = getRadarOption(dataSource)

  return <ReactECharts option={option} notMerge style={{ height: '100%', width: '100%' }} />
}

export default observer(RadarChart)
