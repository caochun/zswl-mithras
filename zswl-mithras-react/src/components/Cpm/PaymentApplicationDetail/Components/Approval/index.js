import { Anchor } from 'antd'
import Attachment from './Components/Attachment'
import Journal from './Components/Journal'
import Operation from './Components/Operation'
import View from './Components/View'
import styles from './index.less'

const { Link } = Anchor
const Approval = () => {
  return (
    <div className={styles.wrap}>
      <div className={styles.anchorWrap}>
        <Anchor className={styles.anchor}>
          <Link href="#attachment" title={'审批附件'} />
          <Link href="#operation" title={'审批操作'} />
          <Link href="#journal" title={'审批日志'} />
          <Link href="#view" title={'审批视图'} />
        </Anchor>
      </div>
      <div className={styles.content}>
        <div id="attachment">
          <Attachment />
        </div>
        <div id="operation">
          <Operation />
        </div>
        <div id="journal">
          <Journal />
        </div>
        <div id="view">
          <View />
        </div>
      </div>
    </div>
  )
}
export default Approval
