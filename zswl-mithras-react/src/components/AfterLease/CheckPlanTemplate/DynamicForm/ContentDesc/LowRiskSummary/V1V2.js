import { observer } from '@zswl/admin'

import styles from '../style.less'
import RowSpan from '../RowSpan'

function AfterLeaseLowRiskSummaryV1V2(props) {
  const { editable } = props
  return (
    <div>
      <div className={styles.titleRow}>检查总结</div>
      <RowSpan subTitle={'检查分析与检查结果'} name="LR_S_1_01">
        <TextAreaReadOnly onlyRead={!editable} />
      </RowSpan>
    </div>
  )
}

export default observer(AfterLeaseLowRiskSummaryV1V2)
