import { observer } from '@zswl/admin'
import { MultiBar } from '@zswl/charts'
import { getChartsTooltip } from '@/components/Chart/TooltipEntries'

const colors = [
  '#85bffa',
  '#4056b6',
  '#96b1e9',
  '#faedc5',
  '#f1ae2d',

  '#bc77f4',
  '#FF9845',
  '#8fced2',
  '#5EB4FF',

  '#BEE1FF',
  '#A285D2',
  '#DACEED',
  '#FF9845',
  '#F9D9BC',
  '#A6D22A',
]

const ReportOperationContractMonitorBarCharts = ({ store }) => {
  const { loading, xData, chartsData } = store

  return (
    <MultiBar
      showLoading={loading}
      style={{ width: '100%', height: 400 }}
      xAxisConfig={[
        {
          data: xData,
          type: 'category',
          axisLabel: {
            interval: 0,
          },
          axisPointer: {
            type: 'shadow',
          },
        },
      ]}
      yAxisConfig={[
        {
          name: '时效(工作日)',
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
      ]}
      colors={colors}
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

export default observer(ReportOperationContractMonitorBarCharts)
