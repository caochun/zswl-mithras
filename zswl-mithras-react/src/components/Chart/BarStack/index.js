import ReactECharts from 'echarts-for-react'
import ReactDOMServer from 'react-dom/server'
import numeral from 'numeral'
import { useMemo, useRef } from 'react'
import styles from './index.less'
import _ from 'lodash'
import { observer } from '@zswl/admin'

const BarStack = ({
  loading,
  loadingOption,
  options = [],
  chartBoxStyle,
  onChange,
  data = [],
  provideNumber = 10,
  maxRow,
  colorList = ['#5d86e5', '#d3be51', '#ea3323'],
}) => {
  const ref = useRef()

  const getOption = useMemo(() => {
    const defaultOption = {
      // dataZoom: {
      //   type: 'slider',
      //   start: 0,
      //   end: 4, // 一次显示5个数据项
      //   show: true,
      //   handleSize: 20,
      //   labelFormatter: (value, name) => {
      //     // 标签格式化为“条目0”
      //     return '条目' + value
      //   },
      // },
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow',
        },
        formatter: (params) => {
          return BarStack.tooltipFormat(params, '万元')
        },
      },
      legend: {},
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true,
      },
      rich: {
        wrap: {
          fontSize: 14, // 字体大小
          lineHeight: 30, // 行高
          width: 120, // 每行文本的宽度
        },
      },
      xAxis: {
        type: 'category',
        data: (data?.[0]?.list || []).map(({ name }) => name),
        rotate: 45,

        axisLabel: {
          fontSize: 10,
          formatter: (params) => {
            let newParamsName = ''
            let paramsNameNumber = params.length
            let rowNumber = Math.ceil(paramsNameNumber / provideNumber)
            if (paramsNameNumber > provideNumber) {
              for (let p = 0; p < (maxRow ?? rowNumber); p++) {
                let tempStr = ''
                let start = p * provideNumber
                let end = start + provideNumber
                if (p === rowNumber - 1) {
                  tempStr = params.substring(start, paramsNameNumber)
                  if (tempStr.length > provideNumber - 1) {
                    tempStr = tempStr.substring(0, provideNumber) + '..'
                  }
                } else {
                  tempStr = params.substring(start, end) + '\n'
                }
                newParamsName += tempStr
              }
            } else {
              newParamsName = params
            }
            return newParamsName
          },
          rich: {
            wrap: {
              // width: 50, // 定义每行文本宽度为 100 像素
              wrap: true,
            },
          },
        },
      },
      yAxis: {
        name: '万元',
        type: 'value',
      },
      series: data.map((v, index) => {
        return {
          name: v.dataType,
          type: 'bar',
          stack: 'Ad',
          data: (v?.list || []).map(({ value }) => value),
          emphasis: {
            focus: 'series',
          },
          color: colorList[index],
        }
      }),
    }
    return _.merge(defaultOption, options)
  }, [options, data])
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
BarStack.tooltipFormat = function (params, unit = '', noDot) {
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
          {numeral(data).format(noDot ? '0,00' : '0,00.00')}
        </span>
        <span>{unit}</span>
      </div>
    )
  })
  return str
}
export default observer(BarStack)
