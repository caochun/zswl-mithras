import IconFont from '@/components/Icon'
import styles from '../../index.less'
import BaseModule from '../BaseModule'
import store from '../../store'
import { history, observer } from '@zswl/admin'
import { App } from '@zswl/components'
import { amountFormat, hasValue } from '@/utils'
import { Col, Row, Tooltip } from 'antd'
import NoData from '../../../../NoData'
const Review = () => {
  const { projReview } = store.page.getData()

  return (
    <Row gutter={16}>
      <Col span={12}>
        <Module title="业务定价" data={projReview?.projReviewPricing} />
      </Col>
      <Col span={12}>
        <Module title="项目评审" data={projReview?.projreview} />
      </Col>
    </Row>
  )
}
const Module = ({ title, data }) => {
  const {
    processStatus,
    createTime,
    approveTime,
    applyCreditAmount,
    processType,
    currentNode,
    conditional,
    projreviewId,
    rateType,
    ratePercent,
    leaseMonthCount,
    planStart,
    planEnd,
  } = data || {}
  return (
    <BaseModule
      style={{ marginBottom: 16 }}
      onClick={() => {
        projreviewId && history.push(`/project/review/detail/${projreviewId}?canEditFlag=true`)
      }}
      content={
        <>
          <div className={styles.title}>
            <div className={styles.left}>
              <div>{title}</div>
              {(createTime || approveTime) && (
                <div style={{ marginLeft: 10 }}>
                  <IconFont type="icon-shenpiguanli-copy" />
                  {createTime || '-'} ～ {approveTime || '-'}
                </div>
              )}
            </div>
          </div>
          {!data ? (
            <NoData />
          ) : (
            <div className={styles.establishmentContent}>
              <div className={styles.item}>
                <IconFont type="icon-fukuanguanli" />
                {applyCreditAmount ? amountFormat(applyCreditAmount / 10000) : '-'}
              </div>
              {title === '业务定价' && (
                <div className={styles.item}>
                  <IconFont type="icon-icon_rate" />
                  <span>{App.matchOption('rateType', rateType).label}：</span>
                  <span>{ratePercent ? amountFormat(ratePercent / 10000) + '%' : '-'}</span>
                </div>
              )}

              {title === '项目评审' && (planStart || planEnd) && (
                <div className={styles.item}>
                  <IconFont type="icon-shenpiguanli-copy" />
                  <span>{hasValue(leaseMonthCount) ? `${leaseMonthCount}个月` : '-'}</span>
                  <span>{`(${planStart || '-'} ~ ${planEnd || '-'})`}</span>
                </div>
              )}
            </div>
          )}
          {conditional ? <span className={styles.conditional}>有条件同意</span> : null}
        </>
      }
      processStatus={processStatus}
      processType={processType}
      currentNode={currentNode}
    />
  )
}

export default observer(Review)
