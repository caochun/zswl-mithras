import { observer } from '@zswl/admin'
import { Anchor, Row, Col } from 'antd'
import Base from './Base/index'
import Summary from './Summary/index' // 信息概要
import UnresolvedCredit from './UnresolvedCredit/index' // 未结清信贷
import CreditAgreement from './CreditAgreement/index' // 授信协议
import RepaymentResponsibility from './RepaymentResponsibility/index' // 还款责任信息
import ClosedCredit from './ClosedCredit/index' // 已结清信贷
import DebtHistory from './DebtHistory/index' // 负债历史
// import styles from '../styles.less'
import styles from '../styles.less'
import Dow from './imgs/dow.png'
import store from './store'
const { Link } = Anchor

const pre = 'customerView-detail-'
const anchorIds = {
  jbxx: pre + 'jbxx', // 基本信息
  xgzy: pre + 'xgzy', // 信息概要
  wjqxd: pre + 'wjqxd', // 未结清信贷及授信信息概要
  sxxy: pre + 'sxxy', // 授信协议汇总
  hkzr: pre + 'hkzr', // 相关还款责任信息概要
  yjqxd: pre + 'yjqxd', // 已结清信贷信息概要
  fzls: pre + 'fzls', // 负债历史
}

function Index() {
  const linkData = () => (
    <>
      <Link href={`#${anchorIds.jbxx}`} title="1.基本信息" />
      <Link href={`#${anchorIds.xgzy}`} title="2.信息概要" />
      <Link href={`#${anchorIds.wjqxd}`} title="3.未结清信贷及授信信息概要" />
      <Link href={`#${anchorIds.sxxy}`} title="4.授信协议汇总" />
      <Link href={`#${anchorIds.hkzr}`} title="5.相关还款责任信息概要" />
      <Link href={`#${anchorIds.yjqxd}`} title="6.已结清信贷信息概要" />
      <Link href={`#${anchorIds.fzls}`} title="7.负债历史" />
    </>
  )

  return (
    <Row gutter={20} wrap={false}>
      <Col>
        <Anchor onClick={(e) => e.preventDefault()}>{linkData()}</Anchor>
      </Col>
      <Col flex={1} className={styles.info_container}>
        <Base id={anchorIds.jbxx} store={store.Base} />
        <Summary id={anchorIds.xgzy} store={store.Summary} />
        <UnresolvedCredit id={anchorIds.wjqxd} store={store.UnresolvedCredit} />
        {/* //授信协议汇总信息 */}
        <CreditAgreement id={anchorIds.sxxy} store={store.CreditAgreement} />
        <RepaymentResponsibility id={anchorIds.hkzr} store={store.RepaymentResponsibility} />
        <ClosedCredit id={anchorIds.yjqxd} store={store.ClosedCredit} />
        <DebtHistory id={anchorIds.fzls} store={store.DebtHistory} />
      </Col>

      <div className={styles.fixedBox}>
        <img src={Dow}></img>
        <div>附件下载</div>
      </div>
    </Row>
  )
}

export default observer(Index)
