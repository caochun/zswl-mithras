import EChartsReact from 'echarts-for-react'
import { App } from '@zswl/components'
import { observer } from '@zswl/admin'
import styles from './index.less'
import { hasValue, amountFormat } from '@/utils'
import { pieColors } from '../../utils'

const Index = ({ pieData }) => {
  var total = pieData.reduce(function (sum, item) {
    return sum + (item.depositedAmount - 0)
  }, 0)

  const pieChartOption = {
    color: pieColors,
    tooltip: {
      show: false,
    },
    legend: {
      show: false,
    },
    graphic: {
      elements: [
        {
          type: 'text',
          left: 'center',
          top: 'center',
          style: {
            text: '总额(万元)\n' + amountFormat(total / (10000 * 10000)),
            textAlign: 'center',
            textVerticalAlign: 'middle',
            fontSize: 18,
            fill: '#000', // 文本颜色
          },
        },
      ],
    },
    series: [
      {
        type: 'pie',
        radius: ['45%', '70%'],
        label: {
          show: true,
          position: 'outside',
          formatter: (record) => {
            const { value, name, percentage } = record.data
            return [`{a|${name}：${percentage}}`, `{b|${amountFormat(value)}}`].join('\n')
          },
          rich: {
            a: {
              color: '#4f4f4f',
              lineHeight: 10,
            },
            b: {
              color: '#000',
              lineHeight: 22,
              fontSize: 14,
              fontWeight: 'bold',
              align: 'right',
            },
          },
        },
        textStyle: {
          rich: {
            value: {
              padding: [0, 0, 0, -5],
              fontSize: 14,
              lineHeight: 20,
            },
          },
        },
        labelLine: {
          show: true,
          length: 15,
          length2: 40,
          lineStyle: {
            color: '#818181',
          },
        },
        data:
          pieData?.map((item) => ({
            value: item.depositedAmount / 10000,
            name: App.matchOption('settingTimeTypeEnum', item.settingTime)?.label,
            percentage: `${item.payAmount?.value}${item.payAmount?.unit}`,
          })) || [],
      },
    ],
  }

  return (
    <div className={styles.content}>
      <EChartsReact option={pieChartOption} style={{ width: '100%', height: 320 }} />
    </div>
  )
}

export default observer(Index)
