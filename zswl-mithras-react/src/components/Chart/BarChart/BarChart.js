import ReactECharts from 'echarts-for-react'
import ReactDOMServer from 'react-dom/server'
import numeral from 'numeral'
import { useMemo, useRef } from 'react'
import _ from 'lodash'
import styles from './index.less'

const BarChart = ({
  loading,
  loadingOption,
  data = [],
  onChange,
  height,
  options = {},
  widthChat,
}) => {
  const ref = useRef()

  const getOption = useMemo(() => {
    const defaultOption = {
      color: ['#2558E6', '#FEB74F', 'rgba(96,172,251,1)'],
      legend: {
        show: false,
      },
      grid: {
        top: 40,
        left: 20,
        right: 40,
        bottom: 40,
        containLabel: true,
      },
      xAxis: {
        name: [],
        axisLabel: {
          color: 'rgba(0,0,0,0.85)',
          margin: 16,
        },
        axisLine: {
          show: false,
        },
        type: 'value',
        splitLine: {
          lineStyle: {
            color: 'rgba(236,237,240,1)',
          },
        },
      },
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'line',
          lineStyle: {
            type: 'solid',
            width: 1,
            color: 'rgba(37,88,230,1)',
          },
          crossStyle: {
            type: 'solid',
            width: 2,
            color: 'rgba(37,88,230,.6)',
          },
        },
        formatter: (params) => {
          return BarChart.tooltipFormat(params)
        },
      },
      yAxis: {
        axisLine: {
          lineStyle: {
            type: 'solid',
            color: 'rgba(37,88,230,1)',
          },
        },
        type: 'category',
        axisLabel: {
          color: 'rgba(0,0,0,0.85)',
          show: true,
          fontSize: '11px',
        },
        data: [],
      },
      series: [{ data: [], name: '', type: 'bar', barWidth: 16 }],
    }
    return _.merge({}, defaultOption, options)
  }, [data, options])
  const onChartReady = (instance) => {
    instance.on('click', (params) => {
      onChange(params.data)
      instance.dispatchAction({
        type: 'showTip',
        seriesIndex: 0,
        dataIndex: params.dataIndex,
      })
    })
  }
  return (
    <div
      className={styles.lineChartWrap}
      style={{ height: height || 30 * (data?.length + 1), width: widthChat }}
    >
      <ReactECharts
        notMerge={true}
        ref={ref}
        lazyUpdate={true}
        style={{ width: '100%', height: '100%' }}
        loadingOption={loadingOption}
        showLoading={loading}
        onChartReady={onChartReady}
        option={getOption}
      />
    </div>
  )
}
BarChart.tooltipFormat = function (params, unit = '', noDot) {
  let str = `<div style="color: rgba(0,0,0,0.85);font-size: 12px;">${params[0].axisValue}</div>`

  params.forEach((item) => {
    const { color, seriesName, data = [] } = item

    str += ReactDOMServer.renderToStaticMarkup(
      <div
        style={{
          display: 'flex',
          alignItems: 'center',
        }}
      >
        <span
          style={{
            display: 'inline-block',
            marginRight: '6px',
            width: '4px',
            height: '4px',
            borderRadius: '50%',
            backgroundColor: color,
          }}
        ></span>
        <span
          style={{
            marginRight: '10px',
            fontSize: '12px',
            color: 'rgba(0, 0, 0, 0.85)',
          }}
        >
          {seriesName}
        </span>
        <span
          style={{
            marginRight: '20px',
            fontSize: '12px',
            color: 'rgba(0, 0, 0, 0.85)',
          }}
        >
          {numeral(data?.value ?? data).format(noDot ? '0,00' : '0,00.00')}
        </span>
        <span>{unit}</span>
      </div>
    )
  })
  return str
}
export default BarChart
