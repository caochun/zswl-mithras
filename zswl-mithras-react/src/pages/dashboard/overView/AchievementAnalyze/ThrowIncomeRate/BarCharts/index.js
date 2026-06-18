import { observer } from '@zswl/admin'
import { MultiBar } from '@zswl/charts'
import ReactDOMServer from 'react-dom/server'
import { getChartsTooltip } from '@/dashboard/DashboardUtils'
import {
  lineSeriesItem,
  RenderTooltip,
  getInterval,
} from '@/dashboard/DashboardUtilsOperation'

const colors = ['#4575f5', '#b3c8fb', '#AFEDDA']

const Index = ({ store }) => {
  const { loading, xData, chartsLineData } = store
  const { max, min, interval } = getInterval([...chartsLineData], 10)

  return (
    <div>
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
          },
        ]}
        yAxisConfig={[
          {
            name: ' ',
            type: 'value',
            position: 'left',
            max,
            min: min > 0 ? min - 1 : min,
            interval,
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
        colors={colors}
        data={[
          ...chartsLineData.map((item, index) => {
            if (index === chartsLineData.length - 1) {
              return lineSeriesItem({
                ...item,
                yAxisIndex: 0,
                symbol: 'none',
                endLabel: {
                  show: true,
                },
                lineStyle: {
                  type: 'solid',
                  width: 3,
                },
              })
            }
            return lineSeriesItem({
              ...item,
              yAxisIndex: 0,
              label: { show: true },
              lineStyle: {
                type: 'solid',
                width: 3,
              },
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
                return ReactDOMServer.renderToStaticMarkup(
                  <div style={{ padding: '8px 16px' }}>
                    <div style={{ marginBottom: 6 }}>
                      <h4>{params?.[0]?.axisValueLabel}</h4>
                    </div>
                    <RenderTooltip params={params}></RenderTooltip>
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

export default observer(Index)
