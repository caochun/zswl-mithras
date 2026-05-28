import { Tabs } from 'antd'
import { useEffect, useMemo } from 'react'
import { Page, App } from '@zswl/components'
import store from './store'
import { observer, getQuery, toJS } from '@zswl/admin'
import styles from './index.less'
import BelongContract from '../components/BelongContract'
import MarginInfo from '../components/MarginInfo'
import PaymentRecords from '../PaymentRecords'
import RefundRecords from '../RefundRecords'
import VerificationRecords from '../VerificationRecords'
const { TabPane } = Tabs

function Index({ params: { id }, query: { contractId } }) {
  store.setClientId(id)
  const { marginDetailData } = store
  console.log(toJS(marginDetailData), 888)
  useEffect(() => {
    return () => {
      App.resetStore(store)
    }
  }, [])

  useEffect(() => {
    store.marginDetail(id)
  }, [id])
  const onChange = (key) => {
    console.log(key)
  }
  return (
    <Page store={store} current="详情" header={null}>
      <div className={styles.marginDetil}>
        <div>
          <div className={styles.marginWrap}>
            <div className={styles.marginNum}>编号:{marginDetailData?.marginCode}</div>
            {/* <div className={styles.marginStatus}>合同状态：起租</div> */}
          </div>
          <BelongContract contractInfo={marginDetailData?.contractInfo} />
          <MarginInfo
            backAmount={marginDetailData?.backAmount}
            canBackAmount={marginDetailData?.canBackAmount}
            planMarginDate={marginDetailData?.planMarginDate}
            deductAmount={marginDetailData?.deductAmount}
            marginAmount={marginDetailData?.marginAmount}
            planMarginAmount={marginDetailData?.planMarginAmount}
            totalReceivableAmount={marginDetailData?.totalReceivableAmount}
            notReceivableAmount={marginDetailData?.notReceivableAmount}
          />
          <Tabs onChange={onChange}>
            <TabPane tab="收款记录" key="1">
              <PaymentRecords id={id} callback={() => store.marginDetail(id)} />
            </TabPane>
            <TabPane tab="退款记录" key="2">
              <RefundRecords
                id={id}
                contractId={marginDetailData?.contractInfo?.contractId}
                callback={() => store.marginDetail(id)}
              />
            </TabPane>
            {/* <TabPane tab="核销记录" key="3">
              <VerificationRecords />
            </TabPane> */}
          </Tabs>
        </div>
      </div>
    </Page>
  )
}
export default observer(Index)
