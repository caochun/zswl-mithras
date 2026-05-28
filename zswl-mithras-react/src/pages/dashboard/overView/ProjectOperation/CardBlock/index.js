import MultipleFieldBlock from '../MultipleFieldBlock'
import IconFont from '@/components/Icon'
import { stepConfig } from '../utils'
import styles from './index.less'
import { observer } from '@zswl/admin'
import { useMemo } from 'react'

const Index = ({ itemData }) => {
  const titleIcon = useMemo(() => {
    const iconType = stepConfig.find((item) => item.stageName === itemData.stageName)?.iconType
    return <IconFont type={iconType} style={{ fontSize: 24, marginRight: 5 }}></IconFont>
  }, [itemData])

  const cardStyle = useMemo(() => {
    const borderColor = stepConfig.find(
      (item) => item.stageName === itemData.stageName
    )?.borderColor
    return {
      borderColor,
    }
  }, [itemData])

  const arrowIcon = useMemo(() => {
    const currentStep = stepConfig.find((item) => item.stageName === itemData.stageName)
    const ArrowIcon = currentStep?.arrowComp
    const arrowStyle = currentStep?.arrowStyle
    return (
      <div className={styles.arrow} style={{ ...arrowStyle }}>
        {ArrowIcon}
      </div>
    )
  }, [itemData])

  return (
    <div className={styles.item}>
      <div className={styles.card} style={{ ...cardStyle }}>
        <div className={styles.title}>
          {titleIcon}
          {itemData.stageName}
        </div>
        <MultipleFieldBlock data={itemData}></MultipleFieldBlock>
      </div>
      {/* {arrowIcon} */}
    </div>
  )
}

export default observer(Index)
