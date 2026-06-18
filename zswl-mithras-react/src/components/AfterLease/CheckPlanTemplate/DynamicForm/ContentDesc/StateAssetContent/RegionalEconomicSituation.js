import { observer } from '@zswl/admin'

import { Row, Col } from 'antd'
import { Form } from '@zswl/components'
import styles from '../style.less'
import RowSpan from '../RowSpan'
import { options } from '@/utils'
import InstructionsText from '../InstructionsText'

const { yesOrNoString: yesOrNo } = options

/**
 * 区域经济情况组件
 * @param {Object} props - 组件属性
 * @param {boolean} props.editable - 是否可编辑
 * @param {boolean} props.required - 是否必填
 * @param {string} props.namePrefix - 表单字段名前缀，默认为 'BUS'
 */
const RegionalEconomicSituation = observer(({ editable, required = true, namePrefix }) => {
  return (
    <>
      <div className={styles.titleRow}>区域经济情况</div>
      <InstructionsText
        title="承租人所在区域GDP、一般公共预算收入是否较上一年度下滑超过20%"
        radioName={`${namePrefix}_C_9_01_01`}
        textAreaName={`${namePrefix}_C_9_01_02`}
        editable={editable}
        required={required}
        triggerValue="1"
        placeholder="请填写情况说明"
        label="情况说明"
      />

      <InstructionsText
        title="承租人所在区域内是否有融资主体出现违约行为"
        radioName={`${namePrefix}_C_9_02_01`}
        textAreaName={`${namePrefix}_C_9_02_02`}
        editable={editable}
        required={required}
        triggerValue="1"
        placeholder="请填写情况说明"
        label="情况说明"
      />

      <RowSpan subTitle={'区域经济情况补充说明'} name={`${namePrefix}_C_9_03`} required={false}>
        <TextAreaReadOnly onlyRead={!editable} placeholder="请填写补充说明" />
      </RowSpan>
    </>
  )
})

export default RegionalEconomicSituation
