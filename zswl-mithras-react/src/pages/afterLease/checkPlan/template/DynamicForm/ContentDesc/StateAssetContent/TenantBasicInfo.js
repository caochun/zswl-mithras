import { observer } from '@zswl/admin'
import styles from '../style.less'
import InstructionsText from '../InstructionsText'

/**
 * 承租人基本情况组件
 * @param {Object} props - 组件属性
 * @param {boolean} props.editable - 是否可编辑
 * @param {boolean} props.required - 是否必填
 * @param {string} props.namePrefix - 表单字段名前缀，默认为 'SOA'
 */
const TenantBasicInfo = observer(({ editable, required = true, namePrefix }) => {
  return (
    <>
      <InstructionsText
        title="承租人本期是否出现变更股东、注册资本、经营范围、法定代表人等情况"
        radioName={`${namePrefix}_C_3_01_01`}
        textAreaName={`${namePrefix}_C_3_01_02`}
        editable={editable}
        required={required}
        triggerValue="1"
      />

      <InstructionsText
        title="承租人本期是否出现住所、通讯地址、联系人、联系方式变更"
        radioName={`${namePrefix}_C_3_02_01`}
        textAreaName={`${namePrefix}_C_3_02_02`}
        editable={editable}
        required={required}
        triggerValue="1"
      />

      <InstructionsText
        title="承租人主要职能定位及经营业务是否发生重大变化"
        radioName={`${namePrefix}_C_3_03_01`}
        textAreaName={`${namePrefix}_C_3_03_02`}
        editable={editable}
        required={required}
        triggerValue="1"
      />

      <InstructionsText
        title="承租人融资渠道是否通畅"
        radioName={`${namePrefix}_C_3_04_01`}
        textAreaName={`${namePrefix}_C_3_04_02`}
        editable={editable}
        required={required}
        triggerValue="0"
      />

      <InstructionsText
        title="是否存在被关闭或划转兼并的明确安排"
        radioName={`${namePrefix}_C_3_05_01`}
        textAreaName={`${namePrefix}_C_3_05_02`}
        editable={editable}
        required={required}
        triggerValue="1"
      />
      <div className={styles.titleRow}>承租人舆情信息</div>
      <InstructionsText
        title="是否存在重大负面舆情"
        radioName={`${namePrefix}_C_4_01`}
        textAreaName={`${namePrefix}_C_4_02`}
        editable={editable}
        required={required}
        triggerValue="1"
        label="具体分析"
      />
    </>
  )
})

export default TenantBasicInfo
