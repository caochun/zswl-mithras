import ReactECharts from 'echarts-for-react'
import { observer } from '@zswl/admin'
import { useEffect, useState } from 'react'
import Api from '@/api/liquidity/liquidityRiskApi'
import { LineChart } from '@/components/Chart/LineChartEntries'
import _, { flatMapDeep, set } from 'lodash'

const divorcer = 10000 * 10000

const LiquidityChart = ({ dataSource, ...rest }) => {
  const [options, setOptions] = useState({})

  const [loading, setLoading] = useState(false)
  const generateOptions = (indicators) => {}
  const formatData = () => {
    if (!dataSource?.length) {
      return {
        xAxisData: [],
        lineData: [],
        lineInterval: 0,
      }
    }

    const xAxisData = dataSource.map((item) => item.date)
    const showLabel = !(xAxisData.length > 10)
    // 定义需要展示的指标及其对应的属性名
    const indicators = [
      // { key: 'initialBalance', name: '期初余额' },
      // { key: 'expectedRentRecovery', name: '预计回收租金' },
      // { key: 'cashFlowExpenditure', name: '现金流支出' },
      // { key: 'debtRepayment', name: '债务偿还' },
      // { key: 'nonAbsRepayment', name: '非ABS还款' },
      // { key: 'absRepayment', name: 'ABS还款' },
      // { key: 'rigidExpenditure', name: '刚性支出' },
      { key: 'endingBalance', name: '公司总余额' },
      // { key: 'dailySupervisedAccountIncome', name: '监管户净流入（当日）' },
      { key: 'supervisedAccountFunds', name: '监管户余额' },
      // { key: 'dailyNonSupervisedAccountIncome', name: '非监管户净流入（当日）' },
      { key: 'nonSupervisedAccountFunds', name: '非监管户余额' },
      // { key: 'dailyLiquidityGap', name: '流动性缺口（当天）' },
      // { key: 'thirtyDayLiquidityCoverageRatio', name: '流动性覆盖率（30天）' },
    ]

    // 生成折线图数据
    const lineData = indicators.map(({ key, name }) => {
      return {
        name,
        type: 'line',
        // labelLine: {
        //   show: !['监管户净流入（当日）'].includes(name),
        // },
        label: {
          show: showLabel,
          formatter: function (params) {
            return params.value.toFixed(2) + 'W'
          },
          position: 'left',
        },
        data: dataSource.map((item) => ({
          value: (item[key]?.value || 0) / divorcer,
        })),
      }
    })

    // 计算y轴间隔
    const allValues = flatMapDeep(lineData, (series) => series.data.map((item) => item.value))
    const maxValue = Math.max(...allValues)
    const minValue = Math.min(...allValues)
    const lineInterval = Math.ceil(maxValue / 5)

    return {
      xAxisData,
      lineData,
      lineInterval,
      maxValue,
      minValue,
    }
  }

  const getBarData = async () => {
    setLoading(true)
    try {
      const { xAxisData, lineData, lineInterval, maxValue, minValue } = formatData()

      const newOptions = {
        color: ['#5088FF', '#48F0AF', '#FFD62A', '#FF5D5D', '#FF9B5D'],
        legend: {
          // data: legend,
          icon: 'rect',
          itemWidth: 10,
          itemHeight: 4,
        },

        toolbox: {
          top: 50,
        },
        tooltip: {
          trigger: 'axis',
          formatter: (params) => {
            const axisValue = params[0].axisValue
            const tooltipIndicators = [
              { key: 'dailySupervisedAccountIncome', name: '监管户净流入（当日）' },
              { key: 'supervisedAccountNetInflow', name: '监管户净流入（累计）' },
              { key: 'dailyNonSupervisedAccountIncome', name: '非监管户净流入（当日）' },
              { key: 'nonSupervisedAccountNetInflow', name: '非监管户净流入（累计）' },
            ]
            const newParams = tooltipIndicators.map(({ key, name }) => {
              const dateItem = dataSource.find((item) => item.date === axisValue)
              const value = (dateItem[key]?.value || 0) / divorcer
              return {
                axisValue,
                seriesName: name,
                value,
                data: { value },
              }
            })
            const sortArr = [
              '公司总余额',
              '监管户余额',
              '监管户净流入（当日）',
              '监管户净流入（累计）',
              '非监管户余额',
              '非监管户净流入（当日）',
              '非监管户净流入（累计）',
            ]
            return LineChart.tooltipFormat(
              [...params, ...newParams].sort((a, b) => {
                const indexA = sortArr.indexOf(a.seriesName)
                const indexB = sortArr.indexOf(b.seriesName)
                return indexA - indexB
              }),
              '万元'
            )
          },
        },
        xAxis: {
          data: xAxisData,
          axisLine: { onZero: true },
          axisLabel: {
            show: true,
          },
          splitLine: { show: false },
          splitArea: { show: false },
        },
        yAxis: [
          {
            type: 'value',
            name: '万元',
            axisLabel: {
              formatter: (value) => {
                return value.toFixed(2)
              },
            },
            // interval: lineInterval,
            max: maxValue,
            // min: minValue,
          },
        ],
        grid: {
          left: 20,
          top: 70,
          right: 20,
          bottom: '5%',
          containLabel: true,
        },
        series: lineData,
      }
      setOptions(newOptions)
      setLoading(false)
    } catch (e) {
      setLoading(false)
    }
  }
  useEffect(() => {
    getBarData()
  }, [dataSource])

  return (
    <div style={{ height: 600 }}>
      <ReactECharts
        notMerge={true}
        lazyUpdate={true}
        style={{ width: '100%', height: '100%' }}
        option={options}
        showLoading={loading}
      />
    </div>
  )
}

export default observer(LiquidityChart)
