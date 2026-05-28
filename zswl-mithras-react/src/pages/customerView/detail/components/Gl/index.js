import { getQuery, observer } from '@zswl/admin'
import { Anchor, Row, Col, Space, Divider } from 'antd'
import Creditinformation from './haders/index'
import ProjectContract from './Content/index'
import CreditRating from './CreditRating/index' // 信用评价
import styles from '../styles.less'
import { Page } from '@zswl/components'
import store from './store'
// import Bootm from './bootm'

const { Link } = Anchor
const pre = 'customerView-detail-'
const anchorIds = {
  sxxx: pre + 'sxxx',
  xmht: pre + 'xmht',
  xypj: pre + 'xypj',
}
function Index({ path, id }) {
  const { getRiskScore = false, getRisk } = store.detailData
  const { cRiskType } = getRisk || {}
  const { riskMap, riskList, dataTime } = cRiskType || {}
  const linkData = (type = 'default') => {
    const isShow = type === 'show'
    return (
      <>
        <div className={isShow && styles.link}>
          <Link href={`#${anchorIds.xypj}`} title="1.内评信息" />
        </div>
        <div className={isShow && styles.link}>
          <Link href={`#${anchorIds.sxxx}`} title="2.授信信息" />
        </div>
        <div className={isShow && styles.link}>
          <Link href={`#${anchorIds.xmht}`} title="3.项目及合同" />
        </div>
      </>
    )
  }
  return (
    <Page store={store.page} noStyle params={{ id: id }}>
      <Row gutter={20} wrap={false}>
        <Col>
          <Anchor offsetTop={150} onClick={(e) => e.preventDefault()}>
            {linkData()}{' '}
          </Anchor>
        </Col>
        <Col flex={1} className={styles.info_container}>
          {/* <Space size={8}>{linkData('show')}</Space> */}
          <CreditRating store={store} id={anchorIds.xypj} />
          <Creditinformation store={store} id={anchorIds.sxxx} />
          <ProjectContract store={store} id={anchorIds.xmht} clientId={id} />
          {/* <Bootm
            store={store}
            data={riskMap || {}}
            list={riskList || []}
            dataTime={dataTime}
            id={anchorIds.xypj}
          ></Bootm> */}
        </Col>
      </Row>
    </Page>
  )
}

export default observer(Index)
