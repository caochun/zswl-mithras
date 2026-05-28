import { Table } from '@zswl/components'
import styles from './styles.less'
import internalRating from '/public/assets/risk/客户统一视图概览/internalRating.png'
import { observer } from '@zswl/admin'
import { saveServer } from '@/utils'

const CreditRating = ({ store, id }) => {
  // 表格列配置
  const internalColumns = [
    { title: '评级时间', dataIndex: 'effectTime', key: 'effectTime', width: 180 },
    { title: '评级结果', dataIndex: 'score', key: 'score' },
  ]

  const externalColumns = [
    { title: '评级机构', dataIndex: 'orgName', key: 'orgName' },
    { title: '评级时间', dataIndex: 'rateDate', key: 'rateDate' },
    { title: '评级结果', dataIndex: 'result', key: 'result' },
  ]

  return (
    <>
      <div id={id} className={styles['title']}>
        {/* 信用评级 */}内评信息
      </div>
      <div className={styles.container}>
        {/* 左侧评级卡片 */}
        <div className={styles.leftSection}>
          <div className={styles.ratingContent}>
            <img className={styles.ratingIcon} src={internalRating}></img>
            <div className={styles.ratingGrade}>{store.score}</div>
            <div className={styles.ratingLabel}>内评评级</div>
            <div className={styles.updateDate}>更新时间：{store.effectTime}</div>
          </div>
          <div className={styles.internalTable}>
            <div className={styles.tableTitle}>历史评级</div>
            <Table
              columnsFilter={'Gl_CreditRating_1'}
              onFilter={(key, val) => saveServer('Gl_CreditRating_1', val)}
              resizable
              scroll={{ y: 100, x: 100 }}
              columns={internalColumns}
              store={store.internalRating}
              pagination={false}
            />
          </div>
        </div>

        {/* 右侧外部评级表格 */}
        {/* <div className={styles.rightSection}>
          <div className={styles.tableTitle}>外部评级</div>
          <Table
            columnsFilter={'Gl_CreditRating_2'}
            onFilter={(key, val) => saveServer('Gl_CreditRating_2', val)}
            resizable
            scroll={{ y: 120, x: 100 }}
            store={store.externalStore}
            columns={externalColumns}
            pagination={false}
          />
        </div> */}
      </div>
    </>
  )
}

export default observer(CreditRating)
