import { observer } from '@zswl/admin'
import { MultiBar } from '@zswl/charts'
import ReactDOMServer from 'react-dom/server'
import { App } from '@zswl/components'
import { getChartsTooltip } from '@/utils/dashboard'
import {
  lineSeriesItem,
  RenderTooltip,
  getInterval,
} from '@/utils/dashboardOperation'

const colors = [
  '#4575f5',
  '#b3c8fb',
  '#35D2A2',
  '#AFEDDA',
  '#39518A',
  '#B0B9D0',
  '#F6BD16',
  '#FCEBB9',
  '#F36C6C',
  '#FBC5C5',
  '#5EB4FF',
  '#BEE1FF',
  '#A285D2',
  '#DACEED',
  '#FF9845',
  '#F9D9BC',
  '#A6D22A',
  '#DCEDAA',
  '#43A8C7',
  '#B4DCE9',
]

const Index = ({ store }) => {
  const { loading, xData, chartsBarData, chartsLineData } = store
  const { max, min, interval } = getInterval([...chartsBarData], 5)

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
            name: '单位(亿元)',
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
            // max,
            // min,
            // interval,
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
        colors={colors}
        data={[
          ...chartsBarData.map(({ name, data }) => ({
            name,
            yAxisIndex: 0,
            data,
            stack: 1,
            type: 'bar',
            barWidth: 15,
            barGap: '10%',
          })),
          ...chartsLineData.map((item, index) => {
            return lineSeriesItem({
              ...item,
              yAxisIndex: index === 0 ? 1 : 0,
              symbol: 'none',
              lineStyle: {
                type: index % 2 ? 'solid' : 'dashed',
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
                const splitArray = (arr) => {
                  const length = arr.length
                  const lastTwo = arr.slice(length - 2)
                  const remaining = arr.slice(0, length - 2)
                  return [remaining, lastTwo]
                }

                const [statistics1, statistics2] = splitArray(params)

                return ReactDOMServer.renderToStaticMarkup(
                  <div style={{ padding: '8px 16px' }}>
                    <div style={{ marginBottom: 6 }}>
                      <h4>{params?.[0]?.axisValueLabel}</h4>
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

export default observer(Index)
