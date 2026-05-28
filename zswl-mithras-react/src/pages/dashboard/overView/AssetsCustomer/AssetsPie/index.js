import { CyclicPie } from '@zswl/charts'
import { useEffect } from 'react'
import { observer } from '@zswl/admin'
import styles from './index.less'

// charts组件库0.0.6 ，需要改成colors
const pieColors = [
  '#3377FF',
  '#31E8B4',
  '#2BC6FF',
  '#FFCA69',
  '#FF5962',
  '#39518A',
  '#C1F035',
  '#985FF7',
  '#43A8C7',
  '#FF9845',
]

const Index = ({ store }) => {
  const { assetIndustryDistribution, getAssetIndustryDistribution } = store
  console.log({ assetIndustryDistribution })

  useEffect(() => {
    getAssetIndustryDistribution()
  }, [])

  return (
    <div className={styles.content}>
      <div className={styles.pie}>
        <div className={styles.title}>资产行业分布</div>
        <CyclicPie
          data={assetIndustryDistribution}
          colors={pieColors}
          config={{
            legend: {
              left: 180,
              height: 200,
              textStyle: {
                height: 20,
                color: '#5E6066',
                rich: {
                  name: {
                    lineHeight: 20,
                    fontSize: 12,
                  },
                  value: {
                    padding: [0, 0, 0, -5],
                    fontSize: 12,
                    lineHeight: 20,
                  },
                  divide: {
                    padding: [0, 5],
                  },
                  percent: {
                    align: 'left',
                    fontSize: 12,
                    lineHeight: 19,
                  },
                },
              },
            },
            series: [
              {
                emphasis: { scale: false },
                radius: [85, 55],
                center: [90, 90],
                label: {
                  show: true,
                  formatter: () => {
                    return `{a|行业\n分类}`
                  },
                  rich: {
                    a: {
                      fontSize: 20,
                      lineHeight: 25,
                      color: '#333',
                    },
                  },
                },
              },
              {
                type: 'pie',
                radius: [90, 87],
                center: [90, 90],
                hoverAnimation: false,
                clockWise: false,
                itemStyle: {
                  normal: {
                    color: '#f1f2f5',
                  },
                },
                label: {
                  show: false,
                },
                data: [500],
              },
            ],
          }}
        ></CyclicPie>
      </div>
    </div>
  )
}

export default observer(Index)
