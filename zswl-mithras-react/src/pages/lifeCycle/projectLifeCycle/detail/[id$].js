import { observer } from '@zswl/admin'
import { Page } from '@zswl/components'
import { Col, Row } from 'antd'
import AfterLeaseCheck from './Components/AfterLeaseCheck'
import Analysis from './Components/Analysis'
import Client from './Components/Client'
import Contract from './Components/Contract'
import Establishment from './Components/Establishment'
import Management from './Components/Management'
import PostManagement from './Components/PostManagement'
import RentCollection from './Components/RentCollection'
import Review from './Components/Review'
import Warning from './Components/Warning'
import RiskStrategy from './Components/RiskStrategy'
import styles from './index.less'
import store from './store'

const ProjectLifeCycleDetail = ({ query: { establishId, reviewId } }) => {
  return (
    <Page
      style={{ backgroundColor: '#F2F3F5', padding: 0 }}
      params={{ establishId, reviewId }}
      store={store}
      current="详情"
      header={null}
    >
      <RiskStrategy store={store}></RiskStrategy>
      <Client />
      <div className={styles.contentWrap}>
        <Row gutter={16}>
          <Col span={17}>
            {/* {dataType !== 'GROUP_CREDIT_REVIEW' && <Establishment />} */}
            <Establishment />
            <Review />
            <Contract />
            <AfterLeaseCheck />
            <RentCollection />
          </Col>
          <Col span={7}>
            <Management />
            <Warning store={store} />
            <Analysis />
          </Col>
        </Row>
      </div>
    </Page>
  )
}
export default observer(ProjectLifeCycleDetail)
