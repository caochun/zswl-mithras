import { observer } from '@zswl/admin'

import styles from '../style.less'
import RowSpan from '../RowSpan'

function Index(props) {
  const { editable, reportTemplateType } = props
  const isV2 = reportTemplateType === 'V2'
  return (
    <div>
      <div className={styles.titleRow}>检查总结</div>
      <RowSpan subTitle={'以上检查事项异常说明'} name="P_S_1_01">
        <TextAreaReadOnly onlyRead={!editable} />
      </RowSpan>
      <RowSpan subTitle={'有权机构审批意见未落实事项'} name="P_S_1_02">
        <TextAreaReadOnly onlyRead={!editable} />
      </RowSpan>
      <RowSpan subTitle={'其他重要跟踪事项'} name="P_S_1_03">
        <TextAreaReadOnly onlyRead={!editable} />
      </RowSpan>
      {isV2 && (
        <RowSpan subTitle={'检查分析与检查结果'} name="P_S_1_04">
          <TextAreaReadOnly onlyRead={!editable} />
        </RowSpan>
      )}
    </div>
  )
}

export default observer(Index)
