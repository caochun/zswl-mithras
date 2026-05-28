import FormAmount from '@/components/Form/FormAmount'
import styles from './style.less'
import { useEffect, useState } from 'react'

const StatisticCard = ({ title, value, unit = '万元', width }) => {
  const [initFormat, setInitFormat] = useState(10000 * 10000)
  const [newUnit, setNewUnit] = useState('万元')

  const formatValue = () => {
    // 当单位为%时,initFormat为10000
    if (unit === '%') {
      return { initFormat: 10000, unit: '%' }
    }
    // 其他情况下,根据数值大小自动转换单位
    if (!value) return 1
    const absValue = Math.abs(value)
    const UNIT_WAN = 10000
    const unitList = ['元', '万元', '亿元', '万亿元']
    for (let i = unitList.length - 1; i >= 0; i--) {
      if (absValue >= UNIT_WAN ** (i + 1)) {
        return { initFormat: UNIT_WAN ** (i + 1), unit: unitList[i] }
      }
    }
    return { initFormat: 10000 * 10000, unit: '元' }
  }

  useEffect(() => {
    const { initFormat, unit } = formatValue()
    setInitFormat(initFormat)
    setNewUnit(unit)
  }, [value, unit])

  return (
    <div className={styles.statisticCard}>
      <div className={styles.title} style={{ width }}>
        {title}
      </div>
      <div className={styles.value}>
        {value ? (
          <FormAmount.Format value={value} initFormat={initFormat} suffix={newUnit} />
        ) : (
          '待测算...'
        )}
      </div>
    </div>
  )
}

export default StatisticCard
