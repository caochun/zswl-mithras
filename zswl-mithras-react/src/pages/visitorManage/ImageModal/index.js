import { observer } from '@zswl/admin'
import { Button, Modal } from '@zswl/components'
import styles from './index.less'
import { Image, Carousel } from 'antd'
import { useState, useRef, useEffect } from 'react'
import { LeftOutlined, RightOutlined } from '@ant-design/icons'
import { downFile } from '@/utils'
import Api from '@/api/visitorManage/visitorManageApi'

const Index = ({ store }) => {
  const [currentIndex, setCurrentIndex] = useState(0)
  const carouselRef = useRef(null)

  // 每次 Modal 打开时重置到第一张图片
  useEffect(() => {
    if (store.visible) {
      setCurrentIndex(0)
      // 确保轮播图也跳转到第一张
      setTimeout(() => {
        carouselRef.current?.goTo(0)
      }, 0)
    }
  }, [store.visible])

  const handleThumbnailClick = (index) => {
    setCurrentIndex(index)
    carouselRef.current?.goTo(index)
  }

  const handlePrev = () => {
    carouselRef.current?.prev()
  }

  const handleNext = () => {
    carouselRef.current?.next()
  }
  const exportImage = async (record) => {
    // console.log('record: ', record)
    const url = await Api.downloadVisitRecordFiles({
      visitRecordIds: [record?.id],
      // visitRecordIds: [1057],
    })
    downFile(url)
  }

  return (
    <Modal
      footer={
        <Button className={styles.footer} type="primary" onClick={() => store.close()}>
          关闭
        </Button>
      }
      title={'查看图片'}
      store={store}
      okText={'关闭'}
      destroyOnClose
      width={800}
    >
      {(data) => {
        return (
          <div className={styles.layout}>
            <div className={styles.header}>
              <Button
                access="apppcvisitfiledownload"
                type="primary"
                onClick={() => exportImage(data)}
              >
                导出
              </Button>
            </div>
            <div className={styles.content}>
              <div className={styles.carouselContainer}>
                <Button
                  className={styles.navButton}
                  icon={<LeftOutlined />}
                  onClick={handlePrev}
                  disabled={currentIndex === 0}
                />

                <Carousel ref={carouselRef} dots={false} afterChange={setCurrentIndex}>
                  {data?.list?.map((item) => (
                    <div key={item.id} className={styles.carouselItem}>
                      <Image
                        src={item?.previewUrl}
                        alt={item?.filename}
                        className={styles.mainImage}
                      />
                    </div>
                  ))}
                </Carousel>

                <Button
                  className={styles.navButton}
                  icon={<RightOutlined />}
                  onClick={handleNext}
                  disabled={currentIndex === data?.list?.length - 1}
                />

                <div className={styles.thumbnailContainer}>
                  {data?.list?.map((item, index) => (
                    <div
                      key={item.id}
                      className={`${styles.thumbnail} ${
                        index === currentIndex ? styles.active : ''
                      }`}
                      onClick={() => handleThumbnailClick(index)}
                    >
                      <img src={item?.previewUrl} alt={item?.filename} />
                    </div>
                  ))}
                </div>
              </div>
            </div>
          </div>
        )
      }}
    </Modal>
  )
}

export default observer(Index)
