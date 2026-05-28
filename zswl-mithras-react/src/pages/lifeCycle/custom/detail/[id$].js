import { observer } from '@zswl/admin'
import { Page } from '@zswl/components'
import { Col, Row } from 'antd'
import store from './store'
import BaseInfo from './BaseInfo'
import ProjectDetail from './ProjectDetail'
import RiskAssistant from './RiskAssistant'
import DebitDetail from './DebitDetail'

const Index = ({ params: { id }, query: { dataType } }) => {
  const detail = store.page.getData()
  return (
    <Page
      style={{ backgroundColor: '#F2F3F5', padding: 0, margin: 0 }}
      params={{ clientId: id, dataType }}
      store={store}
      current="详情"
      header={null}
    >
      <BaseInfo detail={detail} />
      <Row gutter={12} style={{ marginTop: 12 }}>
        <Col span={17}>
          <ProjectDetail id={id} detail={detail} />
        </Col>
        <Col span={7}>
          <DebitDetail id={id} />
        </Col>
      </Row>
      <RiskAssistant style={{ marginTop: 12 }} id={id} clientName={detail.clientName} />
    </Page>
  )
}
export default observer(Index)
