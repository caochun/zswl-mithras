import { history, observer } from '@zswl/admin'
import styles from '../../index.less'
import store from '../../store'
import NoData from '@/components/LifeCycle/NoData'
import BaseModule from '../BaseModule'
import { message } from 'antd'
const AfterLeaseCheck = () => {
  const { afterLeaseCheck } = store.page.getData()
  const { checkPlanList, externalQueryList } = afterLeaseCheck || {}
  return (
    <div className={styles.moduleWrap} style={{ marginBottom: 16 }}>
      <div className={styles.title}>租后检查</div>
      {(checkPlanList && checkPlanList.length > 0) ||
      (externalQueryList && externalQueryList.length > 0) ? (
        <div className={styles.checkWrap}>
          <div className={styles.checkContent}>
            <div className={styles.subTitle} style={{ marginTop: 0 }}>
              租后检查计划
            </div>
            <div className={styles.checkPlan}>
              {checkPlanList && checkPlanList.length > 0 ? (
                checkPlanList?.map((item, index) => {
                  const {
                    processStatus,
                    processType,
                    currentNode,
                    checkPlanProjectId,
                    planName,
                    planTime,
                    canJump,
                  } = item
                  console.log(checkPlanProjectId)
                  return (
                    <BaseModule
                      onClick={() => {
                        if (canJump) {
                          checkPlanProjectId &&
                            canJump &&
                            history.push(`/afterLease/checkPlan/template/${checkPlanProjectId}`)
                        } else {
                          message.warn('当前状态不支持查看项目报告详情')
                        }
                      }}
                      content={
                        <div className={styles.item}>
                          <div className={styles.itemTitle}>{planName}</div>
                          <div className={styles.itemContent}>{planTime}</div>
                        </div>
                      }
                      isSmell
                      key={index}
                      style={{ marginRight: 16 }}
                      processStatus={processStatus}
                      processType={processType}
                      currentNode={currentNode}
                    />
                  )
                })
              ) : (
                <NoData />
              )}
            </div>
            <div className={styles.subTitle}>外部信息查询</div>
            <div className={styles.external}>
              {externalQueryList && externalQueryList.length > 0 ? (
                externalQueryList.map((item, index) => {
                  const { processStatus, processType, currentNode, queryId, queryMonth, planTime } =
                    item
                  return (
                    <BaseModule
                      onClick={() => {
                        queryId && history.push(`/afterLease/checkPlan/externalDetail/${queryId}`)
                      }}
                      isSmell
                      content={
                        <div className={styles.item}>
                          <div className={styles.itemTitle}>{queryMonth || '-'}</div>
                          <div className={styles.itemContent}>外部信息查询报告</div>
                        </div>
                      }
                      key={index}
                      style={{ marginRight: 16 }}
                      processStatus={processStatus}
                      processType={processType}
                      currentNode={currentNode}
                    />
                  )
                })
              ) : (
                <div style={{ width: '100%' }}>
                  <NoData />
                </div>
              )}
            </div>
          </div>
        </div>
      ) : (
        <NoData />
      )}
    </div>
  )
}

export default observer(AfterLeaseCheck)
