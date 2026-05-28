import { ChinaMap } from '@zswl/charts'
import { observer } from '@zswl/admin'
import { renderToString } from 'react-dom/server'
import { Spin } from 'antd'
import styles from './index.less'
import { AmountFormat } from '@/components/Format'
import { amountFormat } from '@/utils'

const Index = ({ store }) => {
  const { provinceRankingTableData } = store
  const rankData = provinceRankingTableData?.map((item) => {
    return {
      ...item,
      name: item.dimensionality,
      value: item.assetsBalance.value,
    }
  })

  const getToolTip = ({ data }) => {
    const curData = rankData.find((item) => data.name.includes(item?.name))
    if (!curData) return null
    return (
      <div className={styles.wrap}>
        <div className={styles.header}>
          <div className={styles.title}>{curData.name}</div>
          <div className={styles.sort}>排名：{curData.rank}</div>
        </div>
        <div className={styles.row}>
          <div className={styles.row_name}>资产余额：</div>
          <div className={styles.row_value}>
            {AmountFormat({ value: curData.assetsBalance.value, initFormat: 1 })}
            {curData.assetsBalance.unit}
          </div>
        </div>
        <div className={styles.row}>
          <div className={styles.row_name}>本年新增投放：</div>
          <div className={styles.row_value}>
            {AmountFormat({ value: curData.loanThisYear.value, initFormat: 1 })}
            {curData.loanThisYear.unit}
          </div>
        </div>
        <div className={styles.row}>
          <div className={styles.row_name}>存量项目个数：</div>
          <div className={styles.row_value}>
            {AmountFormat({ value: curData.stockProjectQuantity, initFormat: 1 })}
          </div>
        </div>
        <div className={styles.row}>
          <div className={styles.row_name}>存量客户数：</div>
          <div className={styles.row_value}>
            {AmountFormat({ value: curData.stockClientQuantity, initFormat: 1 })}
          </div>
        </div>
      </div>
    )
  }
  if (store.provinceRankingTable.loading) {
    return (
      <div className={styles.loading}>
        <Spin />
      </div>
    )
  }
  return (
    <ChinaMap
      style={{ height: 400 }}
      config={() => {
        return {
          series: {
            itemStyle: {
              normal: {
                areaColor: '#fafcff',
                borderWidth: 0.5, // 分界线宽度
                borderColor: '#aaa', // 分界线颜色
                borderType: 'dashed',
                borderDashOffset: 20,
              },
              emphasis: {
                areaColor: '#2658e4',
              },
            },
            label: {
              show: false,
            },
            tooltip: {
              show: true,
              trigger: 'item',
              backgroundColor: 'rgba(255, 255, 255, 0.9)',
              extraCssText: 'box-shadow: 0px 2px 5px rgba(0, 0, 0, 0.3);',
              formatter: function (params) {
                return renderToString(
                  getToolTip({
                    data: params,
                  })
                )
              },
            },
            data: rankData,
          },
          data: rankData,
          visualMap: {
            orient: 'horizontal',
            min: 0,
            max: 500000, // 单位是 万元，100亿
            calculable: false,
            left: 50,
            top: 'bottom',
            hoverLink: false,
            text: ['高', '低'],
            calculable: false,
            formatter: function (value) {
              return amountFormat(value) + '万元'
            },
            inRange: {
              color: ['#f8fcff', '#1e53de'],
            },
          },
        }
      }}
    />
  )
}
export default observer(Index)
