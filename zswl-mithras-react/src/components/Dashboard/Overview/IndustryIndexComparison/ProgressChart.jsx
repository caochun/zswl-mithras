import ReactECharts from 'echarts-for-react'
import { transformData } from './utils'
import { observer } from '@zswl/admin'

const locationIcon =
  'M2.3824 0.972168C1.96277 0.972168 1.50293 1.31198 1.50293 1.73253V14.2708C1.50293 14.6895 1.95251 15.031 2.37135 15.031C2.78993 15.031 3.25082 14.6895 3.25082 14.2707V1.73264C3.25082 1.31211 2.80113 0.972277 2.3824 0.972277V0.972168ZM11.4085 2.0157C8.94234 2.0157 8.94234 1.00486 6.47629 1.00486C5.05273 1.00486 3.95348 1.66898 3.95348 1.66898L3.9432 9.23637C3.9432 9.23637 5.05273 8.57315 6.47631 8.57315C8.94235 8.57315 8.94235 9.584 11.4085 9.584C12.9514 9.584 14.5011 8.77392 14.5011 8.77392V1.20729C14.5011 1.20729 12.9514 2.01568 11.4085 2.01568L11.4085 2.0157Z'
const companyIcon = 'M4 3H12V10L8 13L4 10V3Z'
const colors = ['#FF4B57', '#45CB90', '#F7C137', '#4B7CF3', '#8E5FF5', '#5CC5CD']

const ProgressChart = ({ dataSource }) => {
  const getProgressOption = (response) => {
    if (!response) return {}

    const dataSource = transformData(response)
    const indicators = ['ROA', 'ROE', '总资产', '净资产', '净利润', '杠杆率']
    const companies = Array.from(new Set(dataSource.map((item) => item.company)))

    // 生成每个公司的数据数组
    const seriesData = companies.map((company, index) => {
      const values = indicators.map((indicator) => {
        const item = dataSource.find((d) => d.company === company && d.indicator === indicator)
        return item ? item.value : 0
      })

      // 第一个公司使用旗帜图标，其他使用盾牌图标
      const isFirst = company === '浙江浙商融资租赁有限公司'
      const iconPath = isFirst ? locationIcon : companyIcon

      // 颜色映射

      return {
        name: company,
        type: 'custom',
        renderItem: (params, api) => {
          const value = api.value(0)
          const coord = api.coord([value, api.value(1)])
          return {
            type: 'path',
            shape: {
              pathData: iconPath,
            },
            position: [coord[0] - 5, coord[1] - 20],
            scale: isFirst ? [1.2, 1.2] : [1, 1],
            style: {
              fill: colors[index % colors.length],
            },
            z2: 100,
          }
        },
        data: values.map((value, index) => [value, index]),
        z: 2,
      }
    })
    return {
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow',
        },
        formatter: (params) => {
          const indicator = indicators[params[0].dataIndex]
          let html = `${indicator}<br/>`
          params.forEach((param) => {
            if (param.seriesName === '最大值') return

            // 从原始数据中找到对应的数据项
            const dataItem = dataSource.find(
              (item) => item.company === param.seriesName && item.indicator === indicator
            )
            if (!dataItem) return
            const index = companies.findIndex((company) => company === dataItem.company)

            let displayValue = dataItem.rawValue
            // 根据不同指标格式化显示值
            switch (indicator) {
              case 'ROA':
              case 'ROE':
              case '杠杆率':
                displayValue = Number(displayValue).toFixed(2) + '%'
                break
              case '总资产':
              case '净资产':
              case '净利润':
                displayValue = Number(displayValue).toFixed(2) + '亿元'
                break
            }
            const icon =
              dataItem?.company === '浙江浙商融资租赁有限公司'
                ? `${locationIcon}`
                : `${companyIcon}`
            html += `<svg width="12" height="12">
            <path d="${icon}" fill="${colors[index % colors.length]}" />
            </svg>${param.seriesName}: ${displayValue}<br/>`
          })
          return html
        },
      },
      legend: {
        data: companies.map((company, index) => ({
          name: company,
          itemStyle: {
            color: colors[index % colors.length],
          },
          icon:
            company === '浙江浙商融资租赁有限公司'
              ? `path://${locationIcon}`
              : `path://${companyIcon}`,
        })),
        right: '1%',
        orient: 'vertical',
        align: 'left',
        top: 'center',
        textStyle: {
          color: '#333',
        },
        itemWidth: 15,
        itemHeight: 10,
      },
      grid: {
        left: '5%',
        right: 240,
        bottom: '15%',
        containLabel: true,
      },
      yAxis: [
        {
          type: 'category',
          data: indicators,
          axisTick: {
            show: false,
          },
          axisLine: {
            show: false,
          },
          axisLabel: {
            color: '#333',
          },
        },
      ],
      xAxis: [
        {
          type: 'value',
          max: 1,
          axisLabel: {
            formatter: '{value}',
            show: false,
          },
          splitLine: {
            show: false,
          },
        },
      ],
      series: [
        {
          name: '最大值',
          type: 'bar',
          barWidth: 10,
          itemStyle: {
            color: {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 1,
              y2: 0,
              colorStops: [
                {
                  offset: 0,
                  color: '#EEF2FF',
                },
                {
                  offset: 1,
                  color: '#539ef5',
                },
              ],
            },
            borderRadius: [10, 10, 10, 10],
          },
          data: new Array(indicators.length).fill(1),
          z: 1,
          showInLegend: false,
        },
        ...seriesData,
      ],
    }
  }

  const option = getProgressOption(dataSource)

  return <ReactECharts option={option} notMerge style={{ height: '100%', width: '100%' }} />
}

export default observer(ProgressChart)
