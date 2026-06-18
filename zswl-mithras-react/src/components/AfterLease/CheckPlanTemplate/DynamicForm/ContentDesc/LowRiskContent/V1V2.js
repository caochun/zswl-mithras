import { observer } from '@zswl/admin'

import styles from '../style.less'
import RowSpan from '../RowSpan'

function Index(props) {
  const { editable } = props
  return (
    <div>
      <div className={styles.titleRow}>有权机构审批意见落实情况</div>

      <RowSpan subTitle={'有权机构审批意见落实情况'} name="LR_C_1_01">
        <TextAreaReadOnly onlyRead={!editable} />
      </RowSpan>

      <div className={styles.titleRow}>客户经营分析</div>
      <RowSpan subTitle={'抵质押物是否合规、合法以及有效'} name="LR_C_2_01">
        <TextAreaReadOnly onlyRead={!editable} />
      </RowSpan>
      <RowSpan subTitle={'抵质押物是否存在被查封等情况'} name="LR_C_2_02">
        <TextAreaReadOnly onlyRead={!editable} />
      </RowSpan>
      <RowSpan subTitle={'承租人或者担保人是否存在重大诉讼等情况'} name="LR_C_2_03">
        <TextAreaReadOnly onlyRead={!editable} />
      </RowSpan>
    </div>
  )
}

export default observer(Index)
