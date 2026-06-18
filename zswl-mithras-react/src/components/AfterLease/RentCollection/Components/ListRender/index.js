import { getQuery, observer } from '@zswl/admin'
import { Drawer } from '@zswl/components'
import Project from '../Project'
import ProjectDetail from '../ProjectDetail'
import Term from '../Term'
import TermDetail from '../../TermDetail'
import styles from '../../index.less'
import { useEffect, useMemo, useState } from 'react'
import CheckCard from '@/components/CheckCard'

const ListRender = ({ dataSource, penaltyInterest, groupChange, groupValue }) => {
  const store = {
    $termDetailDrawer: Drawer.useStore(),
    $projectDetailDrawer: Drawer.useStore(),
  }
  const collectionId = getQuery('collectionId')

  useEffect(() => {
    if (collectionId) {
      setActiveType({
        type: 'term',
        collectionId,
        defaultActiveKey: '2',
      })
      store.$termDetailDrawer.open()
    }
  }, [collectionId])
  const [activeType, setActiveType] = useState()
  const currentDetail = useMemo(() => {
    const detailList = {
      project: (
        <ProjectDetail
          baseStore={store}
          isProcess={false}
          {...activeType}
          query={{ canEditFlags: 'false' }}
        />
      ),
      term: <TermDetail baseStore={store} query={{ canEditFlags: 'false' }} {...activeType} />,
    }
    return detailList[activeType?.type]
  }, [activeType])

  return (
    <>
      <CheckCard.Group
        onChange={groupChange}
        multiple
        disabled={!penaltyInterest}
        value={groupValue}
        cache
      >
        <div className={styles.content}>
          {dataSource?.map((item, index) => {
            return (
              <div className={styles.row} key={index}>
                <Project
                  data={item}
                  onClick={() => {
                    setActiveType({ type: 'project', ...item })
                    store.$projectDetailDrawer.open()
                  }}
                />
                <div className={styles.termWrap}>
                  <div className={styles.termList}>
                    {item.collectionCardList.map((t, i) => {
                      const disabled = t.unCollectionPenaltyInterest <= 0
                      if (penaltyInterest) {
                        return (
                          <CheckCard
                            value={t.collectionId}
                            disabled={disabled}
                            style={{ marginLeft: 10 }}
                          >
                            <Term data={t} key={i} />
                          </CheckCard>
                        )
                      } else
                        return (
                          <Term
                            data={t}
                            key={i}
                            style={{ cursor: 'pointer' }}
                            onClick={() => {
                              if (!penaltyInterest) {
                                setActiveType({ type: 'term', ...t })
                                store.$termDetailDrawer.open()
                              }
                            }}
                          />
                        )
                    })}
                  </div>
                </div>
              </div>
            )
          })}
        </div>
      </CheckCard.Group>
      {currentDetail}
    </>
  )
}

export default observer(ListRender)
