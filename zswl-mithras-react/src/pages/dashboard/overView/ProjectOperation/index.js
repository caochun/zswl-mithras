import { useEffect, useMemo } from 'react'
import { Button } from '@zswl/components'
import { Skeleton } from 'antd'
import { RadioTabs } from '@/components'
import { UnorderedListOutlined } from '@ant-design/icons'
import { observer } from '@zswl/admin'
import { OverviewTitle as Title } from '@/components/Dashboard'
import DepartmentDrawer from './DepartmentDrawer'
import CardBlock from './CardBlock'
import Store from './Store'
import styles from './index.less'

const TabSkeleton = () => {
  const arr = new Array(8).fill(1)
  return (
    <div className={styles.rowWrap}>
      {arr.map((item, index) => {
        return (
          <Skeleton
            avatar
            paragraph={{ rows: 4 }}
            className={styles.row}
            key={index}
            style={{ padding: 10, borderRadius: 6 }}
          ></Skeleton>
        )
      })}
    </div>
  )
}

const TabContent = observer(({ store }) => {
  const { cardData, cardLoading } = store

  if (cardLoading) return <TabSkeleton></TabSkeleton>

  return (
    <div className={styles.rowWrap}>
      {cardData?.map((chunkItem) => {
        return (
          <div className={styles.row}>
            <CardBlock itemData={chunkItem} store={store}></CardBlock>
          </div>
        )
      })}
    </div>
  )
})

const Index = ({ title, dataDate }) => {
  const store = useMemo(() => {
    return new Store()
  }, [])

  const { getCardData, currentTab } = store

  useEffect(() => {
    getCardData()
  }, [currentTab])

  useEffect(() => {
    getCardData()
  }, [])

  return (
    <div>
      <Title
        title={title}
        extra={dataDate && <div className={styles.extra}>数据截止时间：{dataDate}</div>}
      ></Title>
      <div className={styles.content}>
        <RadioTabs
          defaultActiveKey="ALL"
          tabBarExtraContent={
            <Button icon={<UnorderedListOutlined />} onClick={store.departmentDrawer.open}>
              效率明细表
            </Button>
          }
          onChange={(value) => {
            store.setCurrentTab(value)
          }}
          items={[
            {
              label: '合计',
              key: 'ALL',
              children: <TabContent key="ALL" store={store} />,
            },
            {
              label: '公共事业类',
              key: 'PUBLIC',
              children: <TabContent key="PUBLIC" store={store} />,
            },
            {
              label: '产业类',
              key: 'INDUSTRY',
              children: <TabContent key="INDUSTRY" store={store} />,
            },
          ]}
        ></RadioTabs>
      </div>
      <DepartmentDrawer store={store} />
    </div>
  )
}

export default observer(Index)
