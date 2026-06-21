import { useMemo, useEffect } from 'react'
import { observer } from '@zswl/admin'
import { Empty, Radio, Space, Tooltip, Spin } from 'antd'
import style from './index.less'
import Store from './Store'
import BasicInfo from './BasicInfo'
import InternalOperationInfo from './InternalOperationInfo'
import { FilingMaterialProvider } from './Context'

const FilingMaterialsApply = ({ params }) => {
  const { id, startUserId, taskActivityId, curTaskActivityIds, startUserName, processStatus, processInstanceId } = params
  const store = useMemo(() => new Store(), [id])
  const { activeTab, currentTabData, tabs, loading, enumType, curTaskDefKey } = store
  useEffect(() => {
    if (!id) {
      return
    }
    store.getTabs(id)
    store.getOperationsDirDict(id)
    store.getCurTaskDefKey(processInstanceId)
  }, [id, processInstanceId])

  useEffect(() => {
    if (!activeTab || Object.keys(enumType).length === 0) {
      return
    }
    if (activeTab === 'BASIC_MATERIALS') {
      store.getBasicMaterial({ id, tabCode: activeTab })
    } else {
      store.getNonBasicMaterial({ id, tabCode: activeTab })
    }
  }, [activeTab, enumType])

  const comp = {
    BASIC_MATERIALS: <BasicInfo id={id} data={currentTabData} />,
    INNER_OPERATION_MATERIALS: <InternalOperationInfo id={id} data={currentTabData} />,
    CONTRACT_MATERIALS: <InternalOperationInfo id={id} data={currentTabData} />,
    LEASEHOLD_MATERIALS: <InternalOperationInfo id={id} data={currentTabData} />,
    COLLATERALIZATION_MATERIALS: <InternalOperationInfo id={id} data={currentTabData} />,
  }
  const Content = useMemo(() => {
    if (activeTab === undefined || activeTab === null) return <Empty />
    if (loading) {
      return <Spin />
    }
    return comp[activeTab]
  }, [activeTab, id, currentTabData, loading])

  return (
    <FilingMaterialProvider
      value={{
        id,
        activeTab,
        enumType,
        startUserId,
        taskActivityId,
        curTaskActivityIds,
        curTaskDefKey,
        referenceData: currentTabData?.[0]?.filingMaterialsGroupQueryRSPList || currentTabData?.[0]?.projMaterialsListListRSP,
        startUserName,
        processStatus,
        refreshTable: store.refreshTable,
      }}
    >
      <div className={style.content}>
        <div style={{ marginBottom: 16 }}>
          <Radio.Group value={activeTab} onChange={store.tagChange}>
            <Space>
              {tabs.map(({ tabCode, tabName }) => (
                <div style={{ display: 'flex', alignItems: 'center' }} key={tabCode}>
                  <Radio.Button value={tabCode} key={tabCode} className={style.buttonClamp}>
                    <Tooltip title={tabName}>{tabName}</Tooltip>
                  </Radio.Button>
                </div>
              ))}
            </Space>
          </Radio.Group>
        </div>
        {Content}
      </div>
    </FilingMaterialProvider>
  )
}

export default observer(FilingMaterialsApply)
