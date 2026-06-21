import { observer } from '@zswl/admin'
import { Anchor, Row, Col } from 'antd'
import CreditInformation from './CreditInformation'
import ProjectContract from './Content'
import CreditRating from './CreditRating' // 信用评价
import styles from '../styles.less'
import { Page } from '@zswl/components'
import store from './store'

const { Link } = Anchor
const pre = 'customerView-detail-'
const anchorIds = {
  sxxx: pre + 'sxxx',
  xmht: pre + 'xmht',
  xypj: pre + 'xypj',
}
function Index({ path, id }) {
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
          <CreditRating store={store} id={anchorIds.xypj} />
          <CreditInformation store={store} id={anchorIds.sxxx} />
          <ProjectContract store={store} id={anchorIds.xmht} clientId={id} />
        </Col>
      </Row>
    </Page>
  )
}

export default observer(Index)
