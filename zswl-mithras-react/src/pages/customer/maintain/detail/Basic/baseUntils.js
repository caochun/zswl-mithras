import styles from './index.less'
import {
  CommerceColums,
  shareholderTypeList,
  relationshipTypeList,
  currencyTypeList,
  continuousStatusList,
  orgScaleTypeList,
} from '../general'
import { Tooltip } from 'antd'
const changeTY = (t, value, key) => {
  return (
    <Tooltip title={value} placement="topLeft">
      <span
        style={{
          color:
            t.changedType == 'MODIFY' && t.changeFields.includes(key)
              ? 'red'
              : 'rgba(0, 0, 0, 0.85)',
        }}
      >
        {value}
      </span>
    </Tooltip>
  )
}
const renderSpan = (obj) => {
  if (obj.name == 'bizScope') {
    return (
      <Tooltip placement="topLeft" title={obj.value}>
        <span className={styles.commerceInfoChild}>{obj.value.slice(0, 10) + '......'}</span>
      </Tooltip>
    )
  } else if (typeof obj.value == 'boolean') {
    return <span className={styles.commerceInfoChild}>{obj.value ? '是' : '否'}</span>
  } else if (obj.name == 'industryType') {
    return <span className={styles.commerceInfoChild}>{obj.nameTitle}</span>
  } else if (obj.name == 'registerCurrencyType' || obj.name == 'realCurrencyType') {
    return <span className={styles.commerceInfoChild}>{currencyTypeList[obj.value]}</span>
  } else if (obj.name == 'continuousStatus') {
    return <span className={styles.commerceInfoChild}>{continuousStatusList[obj.value]}</span>
  } else if (obj.name == 'orgScale') {
    return <span className={styles.commerceInfoChild}>{orgScaleTypeList[obj.value]}</span>
  } else if (obj.name == 'realCapital' || obj.name == 'registerCapital') {
    return <span className={styles.commerceInfoChild}>{obj.value / 10000}</span>
  } else {
    return <span className={styles.commerceInfoChild}>{obj.value}</span>
  }
}
export { changeTY, renderSpan }
