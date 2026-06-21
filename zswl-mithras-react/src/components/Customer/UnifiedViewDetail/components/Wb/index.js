import { observer } from '@zswl/admin'
import { Anchor, Row, Col } from 'antd'
import styles from '../styles.less'
import { Page, Table } from '@zswl/components'
import { saveServer } from '@/utils'
import store from '../Gl/store'

const { Link } = Anchor
const pre = 'customerView-detail-'
const anchorIds = {
  // zxjl: pre + 'zxjl',
  wbpj: pre + 'wbpj',
}
function CustomerUnifiedViewExternalRating({ path, id }) {
  const linkData = (type = 'default') => {
    const isShow = type === 'show'
    return (
      <>
        {/* <div className={isShow && styles.link}>
          <Link href={`#${anchorIds.zxjl}`} title="1.征信记录" />
        </div> */}
        <div className={isShow && styles.link}>
          <Link href={`#${anchorIds.wbpj}`} title="1.外部评级" />
        </div>
      </>
    )
  }
  return (
    <Page noStyle params={{ id: id }}>
      <Row gutter={20} wrap={false}>
        <Col>
          <Anchor offsetTop={150} onClick={(e) => e.preventDefault()}>
            {linkData()}{' '}
          </Anchor>
        </Col>
        <Col flex={1} className={styles.info_container}>
            {/* <div id={anchorIds.zxjl} >暂不开发</div> */}
            <div id={anchorIds.wbpj} >
                <Table
                    columnsFilter={'Gl_CreditRating_1'}
                    onFilter={(key,val) => saveServer('Gl_CreditRating_1',val)}
                    resizable
                    scroll={{ y: 200, x: 100 }}
                    columns={[
                      { title: '评级机构', dataIndex: 'orgName', key: 'orgName' },
                      { title: '评级时间', dataIndex: 'rateDate', key: 'rateDate' },
                      { title: '评级结果', dataIndex: 'result', key: 'result' },
                      { title: '评级状态', dataIndex: 'ratingOutlook', key: 'ratingOutlook' },
                    ]}
                    store={store.externalStore}
                    pagination={false}
                />
            </div>
        </Col>
      </Row>
    </Page>
  )
}

export default observer(CustomerUnifiedViewExternalRating)
