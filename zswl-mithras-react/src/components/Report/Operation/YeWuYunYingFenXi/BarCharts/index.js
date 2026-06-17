import { observer } from '@zswl/admin'
import { MultiBar } from '@zswl/charts'
import { getChartsTooltip } from '@/components/Chart/tooltip'
import { RenderTooltip } from '@/components/Chart/tooltip'
import ReactDOMServer from 'react-dom/server'
import classNames from 'classnames'

const colors = [
  '#85bffa',
  '#4056b6',
  '#96b1e9',
  '#faedc5',
  '#f4cc5e',
  '#f1ae2d',
  '#fbcad1',

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

const Index = ({ store }) => {
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
            // formatter: formatDepartName,
          },
          axisPointer: {
            type: 'shadow',
          },
        },
      ]}
      yAxisConfig={[
        {
          name: '金额(万元)',
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
          name: '数量(个)',
          type: 'value',
          position: 'right',
          axisLabel: {
            formatter: '{value}',
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
          // tooltip: getChartsTooltip(),
          tooltip: getChartsTooltip({
            formatter: function (params) {
              const statistics1 = params.filter((item) => item.seriesName.includes('金额'))
              const statistics2 = params.filter((item) => item.seriesName.includes('数量'))
              return ReactDOMServer.renderToStaticMarkup(
                <div style={{ padding: '8px 16px' }}>
                  <div style={{ marginBottom: 6 }}>
                    <h4>{params?.[0]?.axisValueLabel}</h4>
                  </div>
                  <RenderTooltip params={statistics1} style={{ width: 550 }}></RenderTooltip>
                  <div style={{ marginTop: 8 }}></div>
                  <RenderTooltip params={statistics2} style={{ width: 550 }}></RenderTooltip>
                </div>
              )
            },
          }),
        }
      }}
    />
  )
}

export default observer(Index)
