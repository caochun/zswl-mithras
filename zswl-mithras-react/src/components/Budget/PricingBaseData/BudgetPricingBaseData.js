import { Page } from '@zswl/components'
import { observer } from '@zswl/admin'
import { Card, Col, Row } from 'antd'
import LPRTable from './LPRTable'
import TenNationalDebt from './TenNationalDebt'
import OneShibor from './OneShibor'
import Financing from './Financing'
import Guarantee from './Guarantee'

function BudgetPricingBaseData({ path }) {
  return (
    <Page>
      <Row gutter={12}>
        <Col span={12}>
          <TenNationalDebt />
        </Col>
        <Col span={12}>
          <OneShibor />
        </Col>
      </Row>
      <LPRTable />
      <Financing />
      <Guarantee />
    </Page>
  )
}

export default observer(BudgetPricingBaseData)
