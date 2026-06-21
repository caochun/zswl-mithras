import { observer } from '@zswl/admin'
import { MultiBar } from '@zswl/charts'
import { getChartsTooltip } from '@/utils/domains/dashboard/DashboardUtils'
import { formatDepartName } from '@/utils/domains/dashboard/DashboardUtilsOperation'

const LaunchCompleteBarChart = ({ store }) => {
  const { loading, chartsData } = store

  return (
    <MultiBar
      showLoading={loading}
      style={{ width: '100%', height: 300 }}
      xAxisConfig={[
        {
          data: chartsData[0]?.data.map((item) => item.name),
          type: 'category',
          axisLabel: {
            interval: 0,
            formatter: formatDepartName,
          },
          axisPointer: {
            type: 'shadow',
          },
        },
      ]}
      yAxisConfig={[
        {
          name: '投放(亿元)',
          type: 'value',
          position: 'left',
          nameTextStyle: {
            padding: [0, 0, 0, 50],
          },
          splitLine: {
            lineStyle: {
              type: 'solid',
              color: '#e9eaee',
            },
          },
        },
        {
          name: '完成率',
          type: 'value',
          position: 'right',
          axisLabel: {
            formatter: '{value} %',
          },
          axisLine: { show: true },
          splitLine: {
            show: false,
          },
          nameTextStyle: {
            padding: [0, 50, 0, 0],
          },
        },
      ]}
      colors={['#9dc4f8', '#3662e3', '#b986f3', '#a8d2d7']}
      data={chartsData}
      config={({ config }) => {
        return {
          legend: {
            type: 'scroll',
            orient: 'horizontal',
            width: '80%',
          },
          grid: [
            {
              top: 70,
            },
          ],
          tooltip: getChartsTooltip(),
        }
      }}
    />
  )
}

export default observer(LaunchCompleteBarChart)
