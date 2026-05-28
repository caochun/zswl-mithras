import * as echarts from 'echarts'
import { useEffect } from 'react'
import styles from './index.less'
import classNames from 'classnames'
import { levelColor, quaraterMap } from '@/pages/afterLease/level5Classify/config'

const getOptions = ({ data, total }) => {
  return {
    tooltip: {
      trigger: 'item',
    },
    title: [
      {
        text: total,
        x: 'center',
        y: '42%',
        textStyle: {
          fontSize: 12,
          color: '#000',
        },
      },
    ],
    color: [...levelColor],
    series: [
      {
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        label: {
          show: false,
          position: 'center',
        },
        labelLine: {
          show: true,
        },
        data: [...data],
      },
    ],
  }
}

const Index = ({ domId, selectQuarater, quarater, data }) => {
  const quaraterText = quaraterMap.filter((item) => item.value === quarater)[0]?.text
  const total = data.reduce((prev, cur) => prev + cur?.value, 0)

  useEffect(() => {
    let chartDom = document.getElementById(domId)
    if (chartDom && data?.length > 0) {
      let myChart = echarts.init(chartDom)
      myChart.setOption(getOptions({ data, total }))
    }
  }, [domId, data])

  return (
    <div className={classNames(styles.cardWrap, quarater === selectQuarater ? styles.active : '')}>
      <div className={styles.title}>第{quaraterText}季度</div>
      <div className={styles.content}>
        <div
          id={domId}
          style={{ width: 120, height: 120, display: data.length > 0 ? 'block' : 'none' }}
        ></div>
        {data.length > 0 ? (
          <>
            <div className={styles.example}>
              {data.map((item, index) => {
                return (
                  <div key={index} className={styles.item}>
                    <span className={styles.point} style={{ background: levelColor[index] }}></span>
                    <span className={styles.name}>{item.name}</span>
                    <span className={styles.value}>{item.value}</span>
                  </div>
                )
              })}
            </div>
          </>
        ) : (
          <>
            <div className={styles.noData}>
              <img src="/public/assets/image/noData.svg" className={styles.img} />
              <div className={styles.text}>{'暂无数据'}</div>
            </div>
          </>
        )}
      </div>
    </div>
  )
}

export default Index
