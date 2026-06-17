import { observer } from '@zswl/admin'
import { MultiBar } from '@zswl/charts'
import { getChartsTooltip } from '@/utils/dashboard'
import { formatDepartName } from '@/utils/dashboardOperation'

const Index = ({ store }) => {
  const { loading, personChartsData } = store

  return (
    <MultiBar
      showLoading={loading}
      style={{ width: '100%', height: 300 }}
      xAxisConfig={[
        {
          data: personChartsData[0]?.data.map((item) => item.name),
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
          name: '项目数(个)',
          type: 'value',
          position: 'left',
          nameTextStyle: {
            padding: [0, 0, 0, 60],
          },
          splitLine: {
            lineStyle: {
              type: 'solid',
              color: '#e9eaee',
            },
          },
        },
        {
          name: '金额(亿元)',
          type: 'value',
          position: 'right',
          axisLabel: {
            formatter: '{value} ',
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
      colors={['#85bffa', '#195be3', '#bc77f4', '#8fced2', '#fbcad1']}
      data={personChartsData}
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

export default observer(Index)
