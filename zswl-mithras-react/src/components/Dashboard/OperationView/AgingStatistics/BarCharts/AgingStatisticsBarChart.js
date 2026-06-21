import { observer } from '@zswl/admin'
import { MultiBar } from '@zswl/charts'
import ReactDOMServer from 'react-dom/server'
import { App } from '@zswl/components'
import { getChartsTooltip } from '@/utils/domains/dashboard/DashboardUtils'
import {
  lineSeriesItem,
  RenderTooltip,
  getInterval,
} from '@/utils/domains/dashboard/DashboardUtilsOperation'

const colors = [
  '#85bffa',
  '#4056b6',
  '#96b1e9',
  '#dafcfd',
  '#faedc5',
  '#f4cc5e',
  '#f1ae2d',
  '#fbcad1',

  '#bc77f4',
  '#bc77f4',
  '#8fced2',
  '#8fced2',

  '#5EB4FF',
  '#BEE1FF',
  '#A285D2',
  '#DACEED',
  '#FF9845',
  '#F9D9BC',
  '#A6D22A',
]

const AgingStatisticsBarChart = ({ store }) => {
  const { loading, chartsBarData, chartsLineData } = store
  const { max, min, interval } = getInterval([...chartsBarData, ...chartsLineData], 5)

  return (
    <div>
      <MultiBar
        showLoading={loading}
        style={{ width: '100%', height: 400 }}
        xAxisConfig={[
          {
            data: [
              '立项均耗',
              '尽调-出具尽调报告均耗',
              '评审均耗',
              '纪要均耗',
              '立项-投放均耗',
              '评审-投放',
            ],
            type: 'category',
            axisLabel: {
              interval: 0,
            },
          },
        ]}
        yAxisConfig={[
          {
            name: '平均耗时(工作日)',
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
            max,
            min,
            interval,
          },
        ]}
        colors={colors}
        data={[
          ...chartsBarData.map(({ name, data }) => ({
            name,
            yAxisIndex: 0,
            data,
            type: 'bar',
            barWidth: 15,
            barGap: '40%',
          })),
          ...chartsLineData.map((item, index) => {
            return lineSeriesItem({
              yAxisIndex: 0,
              // symbol: index % 2 !== 0 ? 'none' : 'emptyCircle',
              lineStyle: {
                type: index % 2 !== 0 ? 'dashed' : 'solid',
              },
              ...item,
            })
          }),
        ]}
        config={({ config }) => {
          return {
            legend: {
              // type: 'scroll',
              orient: 'horizontal',
              width: '80%',
            },
            grid: [
              {
                top: 80,
              },
            ],
            tooltip: getChartsTooltip({
              formatter: function (params) {
                const statisticsLabel = App.getData().optionsType.workbenchOperationStatistics.map(
                  (item) => item.label
                )
                const statistics1 = params.filter(
                  (item) => !statisticsLabel.includes(item.seriesName)
                )
                const statistics2 = params.filter((item) =>
                  statisticsLabel.includes(item.seriesName)
                )
                return ReactDOMServer.renderToStaticMarkup(
                  <div style={{ padding: '8px 16px' }}>
                    <div style={{ marginBottom: 6 }}>
                      <h4>{params?.[0]?.axisValueLabel}（单位：工作日）</h4>
                    </div>
                    <RenderTooltip params={statistics1}></RenderTooltip>
                    <RenderTooltip params={statistics2}></RenderTooltip>
                  </div>
                )
              },
            }),
          }
        }}
      />
    </div>
  )
}

export default observer(AgingStatisticsBarChart)
