import { observer } from '@zswl/admin'
import styles from './RiskPoints.less'
import { useState } from 'react'
import cls from 'classnames'
import RiskTriggerModal from './RiskTriggerModal'

const colorList = [
  { color: '#d83b30', name: '重要风险' },
  { color: '#f2a83b', name: '次要风险' },
]

const areaMap = {
  6: `
    'E F C C'
    'A A B B'
    'A A B B'
    'A A D D'
  `,
  7: `
    'F G E E'
    'A A B C'
    'A A B C'
    'A A D D'
  `,
  8: `
    'E F G H'
    'A A B D'
    'A A B D'
    'A A C C'
  `,
  9: `
    'F G H I'
    'C C B E'
    'A A B E'
    'A A D D'
  `,
  10: `
    'G H I J'
    'A A B F'
    'C C B F'
    'D D E E'
  `,
  11: `
    'F G H I'
    'A A B C'
    'D D B C'
    'E E J K'
  `,
}

const handleData = (data) => {
  // 先排序 重要 次要 普通
  const sortField = {
    重要风险: { i: 0, color: '#d83b30' },
    次要风险: { i: 1, color: '#f2a83b' },
    undefined: { i: 2 },
  }
  const areaList = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J']

  let result = Object.keys(data).map((key) => {
    const item = {}
    const list = data[key]
    item.name = key
    item.list = list
    return item
  })
  return result
    .sort((a, b) => {
      return sortField[String(a.list?.[0]?.tagLevel)].i - sortField[String(b.list?.[0]?.tagLevel)].i
    })
    .map((item, index) => {
      const isRisk = !!item.list?.[0]?.tagLevel
      return {
        ...item,
        gridArea: areaList[index],
        background: sortField[String(item.list?.[0]?.tagLevel)].color,
        color: isRisk ? '#fff' : '#000',
        cursor: isRisk ? 'pointer' : 'default',
        isRisk,
      }
    })
}

const RiskPoints = ({ data, list, dataTime, store, getRiskScore }) => {
  const dataHandler = handleData(data)
  const [selected, setSelected] = useState(null)
  const showList = dataHandler.filter((item) => item.list.length)?.flatMap((item) => item.list)
  return (
    <div className={styles.points}>
      <div className={styles.dayRisk}>
        <div style={{ fontSize: 16, fontWeight: 600 }}>当日风险点</div>
      </div>
      <div style={{ display: 'flex' }}>
        <div className={styles.left}>
          <div className={styles.colors}>
            {colorList.map((color) => {
              return (
                <div key={color.color} className={styles.color}>
                  <div className={styles.colorPoint} style={{ background: color.color }} />
                  <span>{color.name}</span>
                </div>
              )
            })}
          </div>
          <div
            className={styles.riskContent}
            style={{
              gridTemplateAreas: areaMap[Math.max(dataHandler.length, 6)],
            }}
          >
            {dataHandler.map((item) => {
              return (
                <div
                  key={item.name}
                  className={cls(styles.item, {
                    [styles.selected]: selected === item.name,
                  })}
                  onClick={() => {
                    item.isRisk && setSelected((pre) => (pre === item.name ? null : item.name))
                  }}
                  style={{ ...item }}
                >
                  {item.name}
                </div>
              )
            })}
          </div>
        </div>
        <div className={styles.right}>
          <div
            className={styles.topDetail}
            onClick={() => {
              store.riskTriggerTableData = {
                table: list,
                date: dataTime,
              }
              store.isSingle = false
              store.riskTriggerModal.open(list)
            }}
          >
            查看详情
          </div>
          <div className={styles.colors}>
            {colorList.map((color) => {
              return (
                <div key={color.color} className={styles.color}>
                  <span></span>
                </div>
              )
            })}
          </div>
          <div className={styles.list}>
            {showList
              .filter((item) => !selected || item.middleaAcriptionOrg.includes(selected))
              ?.map((item, index) => (
                <div
                  key={index}
                  className={
                    item.tagLevel === '重要风险'
                      ? styles.showListItemMain
                      : styles.showListItemOther
                  }
                >
                  <div
                    onClick={() => {
                      store.riskTriggerTableData = {
                        table: list.filter(
                          (it) =>
                            it.middleRisk == item.middleaAcriptionOrg.split('-')[0] &&
                            it.tagLevel == item.tagLevel
                        ),
                        date: dataTime,
                      }
                      store.riskTriggerModal.open()
                    }}
                  >
                    <div className={styles.title}>{item?.middleaAcriptionOrg.split('-')[0]}</div>
                    <div className={styles.desc}>
                      {item?.tagName?.map((it, tdx) => {
                        return (
                          <div key={tdx}>
                            <span className={styles.descDot}>·</span>
                            {it}
                          </div>
                        )
                      })}
                    </div>
                  </div>
                </div>
              ))}
          </div>
        </div>
      </div>
      <RiskTriggerModal isSingle={store.isSingle} store={store} getRiskScore={getRiskScore} />
    </div>
  )
}

export default observer(RiskPoints)
