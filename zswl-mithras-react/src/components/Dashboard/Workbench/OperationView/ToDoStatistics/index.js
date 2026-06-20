import { useMemo, useState, useRef, useEffect } from 'react'
import { observer } from '@zswl/admin'
import { Empty, Spin } from 'antd'
import CardPanelFieldsFilter from '../../../CardPanelFieldsFilter'
import Card from './components/Card'
import Title from '../../../Title'
import Store from './Store'
import styles from './index.less'

const EmptyComp = () => {
  return <Empty style={{ marginTop: 20 }}></Empty>
}

const Index = () => {
  const store = useMemo(() => {
    return new Store()
  }, [])

  const {
    arrivedForProcessing,
    arrivedForProcessingLoading,
    willArrivedProcessing,
    willArrivedProcessingLoading,
  } = store

  useEffect(() => {
    store.getArrivedForProcessing()
    store.getWillArrivedForProcessing()
  }, [])

  return (
    <>
      <CardPanelFieldsFilter title={'待办统计'}>
        <div className={styles.cardWrap}>
          <div className={styles.cardItem}>
            <Title title={'已到达待处理'} iconType={'icon-jieqing'}></Title>
            <Spin spinning={arrivedForProcessingLoading} active>
              {arrivedForProcessing.length > 0 ? (
                <Card list={arrivedForProcessing} store={store}></Card>
              ) : (
                <EmptyComp />
              )}
            </Spin>
          </div>
          <div className={styles.cardItem}>
            <Title title={'将到达'} iconType={'icon-yuqixiangmu'}></Title>
            <Spin spinning={willArrivedProcessingLoading} active>
              {willArrivedProcessing.length > 0 ? (
                <Card
                  store={store}
                  list={willArrivedProcessing}
                  cardItemStyle={{
                    background:
                      'linear-gradient(360deg, rgba(235, 250, 250, 0.25) 0%, #EBFAFA 100%)',
                  }}
                ></Card>
              ) : (
                <EmptyComp />
              )}
            </Spin>
          </div>
        </div>
      </CardPanelFieldsFilter>
    </>
  )
}

export default observer(Index)
