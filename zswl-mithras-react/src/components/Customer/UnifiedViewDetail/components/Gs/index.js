import { observer } from '@zswl/admin'
import { Anchor, Row, Col, Space, Divider } from 'antd'
import Base from './Base/index'
import PrincipalShareholder from './PrincipalShareholder/index'
import BondInformation from './BondInformation/index' // 债券信息
import styles from '../styles.less'
import store from './store'
const { Link } = Anchor

const pre = 'customerView-detail-'
const anchorIds = {
  gsxx: pre + 'gsxx',
  zygd: pre + 'zygd',
  zjxx: pre + 'zjxx',
}
function Index() {
  const linkData = (type = 'default') => {
    const isShow = type === 'show'
    return (
      <>
        <div className={isShow && styles.link}>
          <Link href={`#${anchorIds.gsxx}`} title="1.工商信息" />
        </div>
        <div className={isShow && styles.link}>
          <Link href={`#${anchorIds.zygd}`} title="2.主要股东" />
        </div>
        <div className={isShow && styles.link}>
          <Link href={`#${anchorIds.zjxx}`} title=" 3.债券信息" />
        </div>
      </>
    )
  }
  return (
    <Row gutter={20} wrap={false}>
      <Col>
        <Anchor offsetTop={150} onClick={(e) => e.preventDefault()}>
          {linkData()}
        </Anchor>
      </Col>
      <Col flex={1} className={styles.info_container}>
        <Base store={store} id={anchorIds.gsxx} />
        <PrincipalShareholder store={store} id={anchorIds.zygd} />
        <BondInformation store={store} id={anchorIds.zjxx} />
      </Col>
    </Row>
  )
}

export default observer(Index)
