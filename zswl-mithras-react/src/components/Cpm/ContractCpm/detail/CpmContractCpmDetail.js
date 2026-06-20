import { Tabs, Spin, Tooltip } from 'antd'
import { useEffect, useMemo, useState } from 'react'
import { Page, App } from '@zswl/components'
import store from './store'
import ContractDownPayment from '../ContractDownPayment/CpmContractDownPayment'
import { observer, getQuery } from '@zswl/admin'
import styles from './index.less'
import Api from '@/api/cpm/contractCpmApi'
import { UserOutlined } from '@ant-design/icons'

const { TabPane } = Tabs

function CpmContractCpmDetail({ params: { id }, query: { projCode, rentActualCode } }) {
  store.setContractD(id)
  const [cDetail, setcDetail] = useState({})
  const [loading, setLoading] = useState(false)
  useEffect(() => {
    return () => {
      App.resetStore(store)
    }
  }, [])
  const contractDetail = async (contractId) => {
    setLoading(true)
    const data = await Api.contractDetail({ contractId })
    setcDetail(data)
    setLoading(false)
  }

  useEffect(() => {
    contractDetail(id)
  }, [id])
  return (
    <Page store={store} current="详情" header={null} params={{ rentActualCode }}>
      <div className={styles.contractDownPayment}>
        <div className={styles.contractNameContainer}>
          <div className={styles.contractNameWrap}>
            {cDetail.projName} <span className={styles.contractNum}>({cDetail.projCode})</span>
            <span className={styles.contractName}>
              <Tooltip
                title={
                  <span>
                    {cDetail.bizDept}-{cDetail.projSponsorUser}
                  </span>
                }
              >
                <UserOutlined />
              </Tooltip>
            </span>
          </div>
          {/* <div className={styles.contractName}></div> */}
        </div>

        <div>
          <Tabs>
            {/* <TabPane tab="客户信息" key="1"></TabPane>
            <TabPane tab="项目立项" key="2"></TabPane>
            <TabPane tab="项目评审" key="4"></TabPane>
            <TabPane tab="项目合同" key="5"></TabPane> */}
            <TabPane tab="合同收付款" key="6">
              <ContractDownPayment id={id} contractDetailData={cDetail} />
            </TabPane>
            {/* <TabPane tab="项目资产" key="7"></TabPane>
            <TabPane tab="项目财务" key="8"></TabPane>
            <TabPane tab="项目风险" key="9"></TabPane> */}
          </Tabs>
        </div>
      </div>
    </Page>
  )
}
export default observer(CpmContractCpmDetail)
