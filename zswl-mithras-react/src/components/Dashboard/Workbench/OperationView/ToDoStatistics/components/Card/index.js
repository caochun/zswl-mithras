import { useState, useRef } from 'react'
import { observer } from '@zswl/admin'
import { chunk } from 'lodash'
import { Carousel } from 'antd'
import cls from 'classnames'
import CardItem from '../CardItem'
import IconFont from '@/components/Icon'
import styles from './index.less'

const Index = ({ list = [], cardItemStyle = {}, store }) => {
  const carouselRef = useRef(null)
  const [currentSlide, setCurrentSlide] = useState(1)
  const chunks = chunk(list, 3)

  const handleNext = () => {
    if (currentSlide >= chunks.length) {
      return
    }
    carouselRef.current.next()
    setCurrentSlide((prevSlide) => prevSlide + 1)
  }

  const handlePrev = () => {
    if (currentSlide === 1) {
      return
    }
    carouselRef.current.prev()
    setCurrentSlide((prevSlide) => prevSlide - 1)
  }

  return (
    <>
      <Carousel ref={carouselRef}>
        {chunks.map((chunksItem, chunksIndex) => {
          return (
            <div key={chunksIndex}>
              {chunksItem.map((item, index) => {
                return (
                  <div key={index}>
                    <CardItem data={item} style={cardItemStyle} store={store}></CardItem>
                  </div>
                )
              })}
            </div>
          )
        })}
      </Carousel>
      {chunks.length > 1 && (
        <div className={styles.action}>
          <IconFont
            type="icon-circle-left"
            className={cls(styles.icon, currentSlide === 1 && styles.disabled)}
            onClick={handlePrev}
          />
          <div className={styles.countWrap}>
            <span className={styles.currentPage}>{currentSlide}</span>/
            <span className={styles.totalPage}>{chunks?.length}</span>
          </div>
          <IconFont
            type="icon-circle-right"
            className={cls(styles.icon, currentSlide === chunks.length && styles.disabled)}
            onClick={handleNext}
          />
        </div>
      )}
    </>
  )
}

export default observer(Index)
