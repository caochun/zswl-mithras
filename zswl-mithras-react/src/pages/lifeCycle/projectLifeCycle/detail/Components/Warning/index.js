import { observer } from '@zswl/admin'
import { useEffect, useState } from 'react'
import { LifeCycleRiskStrategyCard as Card } from '@/components/LifeCycle/LifeCycleEntries'
import styles from '../../index.less'
import cardStyles from './card.less'
import Api from '@/api/risk/publicMonitor'

const Warning = ({ store }) => {
  const [riskCount, setRiskCount] = useState()
  const [clientId, setClientId] = useState()
  const { forceUpdateId } = store

  const getRiskList = async () => {
    const res = await Api.postMonitorList({ page: 1, pageSize: 10, clientId })
    setRiskCount(res.total)
  }

  useEffect(() => {
    if (clientId) {
      getRiskList()
    }
  }, [clientId])

  useEffect(() => {
    if (clientId) {
      store.getClientInfo(clientId)
    }
  }, [clientId, store])

  useEffect(() => {
    if (store) {
      const baseData = store.page.getData()
      const res = baseData.clientInfos?.find(
        (item) => item.clientCategory === '承租人' || item.clientCategory === '债权人'
      )?.clientId
      setClientId(res)
    }
  }, [store])
  return (
    <div className={styles.moduleWrap} style={{ marginBottom: 16 }}>
      <div className={styles.title}>
        风险策略助手
        <a style={{ fontSize: 14 }} onClick={store.$riskStrategy.open}>
          查看更多
        </a>
      </div>
      <div className={cardStyles.cardWrap}>
        <div className={cardStyles.cardWrap_header}>
          <div className={cardStyles.cardWrap_header_img}>
            <img src={'/public/assets/image/risk.png'} />
          </div>
          <div className={cardStyles.cardWrap_header_info}>
            <div className={cardStyles.cardWrap_header_info_r}>
              <span className={cardStyles.cardWrap_header_info_r_count}>{riskCount ?? '0'}</span>项
            </div>
            <div className={cardStyles.cardWrap_header_info_r_text}>监测到产生的舆情风险</div>
          </div>
        </div>
        <Card
          styles={cardStyles}
          baseStore={store}
          forceUpdateId={forceUpdateId}
          clientId={clientId}
        ></Card>
      </div>
    </div>
  )
}

export default observer(Warning)
