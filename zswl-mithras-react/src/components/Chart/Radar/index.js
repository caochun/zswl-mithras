import ReactECharts from 'echarts-for-react'
import { useMemo, useRef } from 'react'
import styles from './index.less'
import _  from 'lodash'
import config from '../config'
import numeral from 'numeral'
import ReactDOMServer from 'react-dom/server'
import { observer } from '@zswl/admin'

const Radar = ({
  loading,
  loadingOption,
  data = {},
  onChange,
  chartBoxStyle,
  options,
  maxName,
}) => {
  const ref = useRef()
  const getOption = useMemo(() => {
    const indicator = (data?.MAX || []).map(({ name, value }) => ({
      name,
      max: value,
    }))
    const legend = []
    const series = []
    let unit = []
    Object.entries(data).forEach(([key, value]) => {
      const name = key === 'MAX' ? maxName : key
      legend.push(name)
      series.push({
        name,
        value: value.map(({ value: newValue }) => newValue),
      })
      unit = value.map(({ unitDisplay }) => unitDisplay)
    })

    const defaultOption = {
      color: config.colorWheel,
      legend: {
        data: legend,
      },
      tooltip: {
        trigger: 'axis',
        formatter: (params) => {
          params.config = {
            color: config.colorWheel,
            unit,
            indicator,
          }
          return Radar.tooltipFormat(params)
        },
      },
      radar: {
        // shape: 'circle',
        center: ['50%', '70%'],
        radius: '60%',
        indicator,
      },
      series: {
        name: '龙虎雷达图',
        type: 'radar',
        tooltip: {
          trigger: 'item',
        },
        data: series,
      },
    }
    return _.merge(defaultOption, options)
  }, [options, data])
  const onChartReady = (instance) => {
    instance.on('click', (params) => {
      onChange?.(params.data)
      instance.dispatchAction({
        type: 'showTip',
        seriesIndex: 0,
        dataIndex: params.dataIndex,
      })
    })
  }
  return (
    <div className={styles.lineChartWrap} style={{ ...chartBoxStyle }}>
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

Radar.tooltipFormat = function (params, noDot) {
  let str = `<div style="color: rgba(0,0,0,0.85);font-size: 12px;">${params.name}</div>`
  const { config: paramsConfig } = params
  params.value.forEach((data, index) => {
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
            backgroundColor: paramsConfig.color[index],
          }}
        ></span>
        <span
          style={{
            marginRight: '6px',
            fontSize: '12px',
            color: 'rgba(0, 0, 0, 0.85)',
          }}
        >
          {paramsConfig.indicator[index].name}：
        </span>
        <span
          style={{
            marginRight: '6px',
            fontSize: '12px',
            color: 'rgba(0, 0, 0, 0.85)',
          }}
        >
          {numeral(data).format(noDot ? '0,00' : '0,00.00')}
        </span>
        <span>{_.isArray(paramsConfig.unit) ? paramsConfig.unit[index] : paramsConfig.unit}</span>
      </div>
    )
  })
  return str
}
export default observer(Radar)
