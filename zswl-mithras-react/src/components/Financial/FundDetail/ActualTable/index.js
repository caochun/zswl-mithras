import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import ActualTable from '@/components/Financial/FundActualTable'
import styles from './index.less'
import { compareTableData } from '@/utils'

function Index({
  financingId,
  businessVersion,
  canEdit,
  baseInfoData,
  isOtherChange,
  detail,
  isFormApproval,
  allModuleData = null,
  baseStore,
}) {
  const SENCE = isOtherChange ? 'CHANGE_OTHER' : 'NEW'

  // 获取基础信息模块
  const getBaseInfoData = useMemo(() => {
    if (!allModuleData) return baseInfoData
    const modeleKey = 'BASE_INFO'
    if (isFormApproval) {
      const { newDetail } = compareTableData(allModuleData[modeleKey])
      return newDetail[0]
    }
    return allModuleData[modeleKey][0]
  }, [allModuleData])

  return (
    <div className={styles.page}>
      <div className={styles.header}>
        <div className={styles.title}>{`实际还款计划`}</div>
      </div>
      <ActualTable
        canEdit={canEdit}
        isFormApproval={isFormApproval}
        sourceData={detail}
        businessVersion={businessVersion}
        scene={SENCE}
        financingId={financingId}
        baseInfoData={getBaseInfoData}
        showImportBtn={canEdit}
        isOtherChange={isOtherChange}
        allModuleData={null} // 版本日志
        baseStore={baseStore}
      ></ActualTable>
    </div>
  )
}

export default observer(Index)
