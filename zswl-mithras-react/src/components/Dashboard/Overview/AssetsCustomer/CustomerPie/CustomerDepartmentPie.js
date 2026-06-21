import { CyclicPie } from '@zswl/charts'
import { observer } from '@zswl/admin'
import { useEffect, useMemo } from 'react'
import { amountFormat } from '@/utils'
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

const CustomerDepartmentPie = ({ store }) => {
  const { customerDepartment, getCustomerDepartmentData } = store

  useEffect(() => {
    getCustomerDepartmentData()
  }, [])

  const total = useMemo(() => {
    return Math.round(
      customerDepartment
        ?.map((item) => +item.value)
        .reduce((value, total) => {
          return value + total
        }, 0)
    )
  }, [customerDepartment])

  return (
    <div className={styles.content}>
      <div className={styles.pie}>
        <div className={styles.title}>客户部门分布</div>
        <CyclicPie
          data={customerDepartment}
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
                    return `{a|总客户数}\n{b|${amountFormat(total ?? 0)}}`
                  },
                  rich: {
                    a: {
                      fontSize: 14,
                      lineHeight: 25,
                      color: '#8D9099',
                    },
                    b: {
                      fontSize: 32,
                      color: '#17181A',
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

export default observer(CustomerDepartmentPie)
