import { observer } from '@zswl/admin'
import { forwardRef } from 'react'
import styles from '../style.less'
import TenantBusinessAnalysis from './TenantBusinessAnalysis'
import TenantBasicInfo from './TenantBasicInfo'
import GuarantorBasicInfo from './GuarantorBasicInfo'
import RegionalEconomicSituation from './RegionalEconomicSituation'
import LeasedProperty from './LeasedProperty'

/**
 * 主组件 - 业务内容表单
 * @param {Object} props - 组件属性
 * @param {boolean} props.editable - 是否可编辑
 * @param {string} props.reportTemplateType - 报告模板类型
 * @param {string} props.chiName - 企业名称
 * @param {string} props.namePrefix - 表单字段名前缀，默认为 'SOA'
 */
function AfterLeaseStateAssetContentV1(props, ref) {
  const { editable, reportTemplateType, chiName, namePrefix = 'SOA' } = props
  const isV1 = reportTemplateType === 'V1'
  const isV2 = reportTemplateType === 'V2'
  const isV3 = reportTemplateType === 'V3'
  const baseProps = { isV1, isV2, isV3, namePrefix, ...props }

  return (
    <div className={styles.contentBox}>
      <TenantBusinessAnalysis {...baseProps} />
      <TenantBasicInfo {...baseProps} />
      <GuarantorBasicInfo {...baseProps} />
      <RegionalEconomicSituation {...baseProps} />
      <LeasedProperty {...baseProps} />
    </div>
  )
}

export default observer(forwardRef(AfterLeaseStateAssetContentV1))
