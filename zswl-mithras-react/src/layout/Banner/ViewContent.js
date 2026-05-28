import { observer } from '@zswl/admin'
import { Carousel, Space, Skeleton, Row, Col } from 'antd'
import styles from './index.less'

const Index = ({ store }) => {
  const { bannerDetail, bannerDetailLoading } = store
  return (
    <div className={styles.viewContent}>
      {bannerDetailLoading ? (
        <Space direction="vertical" style={{ width: '100%' }}>
          <Skeleton.Image active />
          <Skeleton active paragraph={{ rows: 8 }} block />
        </Space>
      ) : (
        <>
          {bannerDetail?.images?.length > 0 ? (
            <div className={styles.imgWrap}>
              <Carousel autoplay>
                {bannerDetail?.images?.map((item) => {
                  return (
                    <div className={styles.img} key={item.id}>
                      <img src={item.preUrl}></img>
                    </div>
                  )
                })}
              </Carousel>
            </div>
          ) : null}
          <div className={styles.title}>{bannerDetail?.title}</div>
          <Row>
            <Col span={12} className={styles.time}>
              创建时间：{bannerDetail?.createTime || '-'}
            </Col>
            <Col span={12} className={styles.time}>
              更新时间：{bannerDetail?.updateTime || '-'}
            </Col>
            <Col span={12} className={styles.time}>
              有效期：{bannerDetail?.expirationFrom} ~ {bannerDetail?.expirationTo}
            </Col>
          </Row>
          <div className={styles.content}>{bannerDetail?.content}</div>
        </>
      )}
    </div>
  )
}

export default observer(Index)
