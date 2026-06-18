import IconFont from '@/components/Icon'
import { DownOutlined, UpOutlined } from '@ant-design/icons'
import { history, observer } from '@zswl/admin'
import { Col, Collapse, Progress, Row, Tooltip } from 'antd'
import styles from '../../index.less'
import BaseModule from '../BaseModule'
import store from '../../store'
import { amountFormat, hasValue } from '@/utils'
import { App } from '@zswl/components'
import { LifeCycleNoData as NoData } from '@/components/LifeCycle/LifeCycleEntries'
const Panel = Collapse.Panel
const Contract = () => {
  const { contract } = store.page.getData()
  console.log(contract)
  return (
    <div className={styles.contractWrap}>
      <div className={styles.title}>{'项目合同'}</div>
      {contract ? (
        <div className={styles.contractContent}>
          <Collapse
            ghost
            defaultActiveKey={contract?.map((item, index) => index)}
            expandIcon={(panelProps) => {
              console.log(panelProps)
              const { isActive } = panelProps
              return (
                <div className={styles.expandIcon}>
                  {isActive ? (
                    <>
                      <UpOutlined />
                      收起
                    </>
                  ) : (
                    <>
                      <DownOutlined />
                      展开
                    </>
                  )}
                </div>
              )
            }}
            expandIconPosition="end"
          >
            {contract?.map((item, index) => {
              const {
                contractCode,
                createTime,
                approveTime,
                creditAmount,
                bizType,
                processStatus,
                processType,
                currentNode,
                paymentCard,
                contractStatus,
                contractId,
                updateType,
              } = item
              return (
                <Panel
                  header={
                    <BaseModule
                      onClick={() => {
                        history.push(`/contract/list/detail/${contractId}?canEditFlags=true`)
                      }}
                      content={
                        <div className={styles.contractItem}>
                          <div className={styles.contractTitle}>
                            <div className={styles.bizType}>{bizType}</div>
                            {contractCode}
                            {updateType && <div className={styles.zq}>{updateType}</div>}
                          </div>
                          <div className={styles.establishmentContent}>
                            <div className={styles.item} style={{ width: 200, textAlign: 'left' }}>
                              <IconFont type="icon-shenpiguanli-copy" />
                              {createTime} ～ {approveTime}
                            </div>
                            <div className={styles.item}>
                              <IconFont type="icon-fukuanguanli" />
                              {amountFormat(creditAmount / 10000)}
                            </div>
                            <div className={styles.item}>
                              <IconFont type="icon-xiangmulixiang" />
                              合同状态：
                              {contractStatus}
                            </div>
                          </div>
                        </div>
                      }
                      processStatus={processStatus}
                      processType={processType}
                      currentNode={currentNode}
                    />
                  }
                  showArrow={!!paymentCard}
                  collapsible={paymentCard ? true : 'disabled'}
                  key={index}
                >
                  {paymentCard && (
                    <div className={styles.contentBox}>
                      {paymentCard?.map((T, i) => {
                        const {
                          paymentCode,
                          applyPaymentAmount,
                          writeOffStatus,
                          receiptCode,
                          processStatus: paymentProcessStatus,
                          processType: paymentProcessType,
                          currentNode: paymentCurrentNode,
                          paymentId,
                          collectionCard = {},
                        } = T
                        const {
                          collectionRate,
                          collectionPhase,
                          totalPhase,
                          receivedPrincipal,
                          receivedInterest,
                          receivedPenaltyInterest,
                          penaltyInterest,
                          contractId: collectionCardContractId,
                          projectCode,
                          rentActualCode,
                          totalAmount,
                          receivedAmount,
                        } = collectionCard || {}
                        return (
                          <Row gutter={16} key={i}>
                            <Col span={8}>
                              <BaseModule
                                // onClick={() => {
                                //   history.push(
                                //     `/cpm/paymentApplication/detail/${paymentId}?canEditFlag=true`
                                //   )
                                // }}
                                content={
                                  <div className={styles.loan}>
                                    <div className={styles.label}>放款情况</div>
                                    <div className={styles.content}>{paymentCode}</div>
                                    <div className={styles.money}>
                                      <IconFont style={{ marginRight: 4 }} type="icon-icon_pay" />
                                      {amountFormat(applyPaymentAmount / 10000)}元
                                    </div>
                                    <div className={styles.status}>
                                      {
                                        App.matchOption('paymentWriteOffStatus', writeOffStatus)
                                          .label
                                      }
                                    </div>
                                  </div>
                                }
                                processStatus={paymentProcessStatus}
                                processType={paymentProcessType}
                                currentNode={paymentCurrentNode}
                                isSmell
                              />
                            </Col>
                            <Col span={16}>
                              <div
                                className={styles.collection}
                                onClick={() => {
                                  collectionCard &&
                                    history.push(
                                      `/cpm/contractCpm/detail/${collectionCardContractId}?rentActualCode=${receiptCode}`
                                    )
                                }}
                              >
                                {collectionCard ? (
                                  <Row gutter={24}>
                                    <Col span={8}>
                                      <div className={styles.left}>
                                        <div className={styles.label}>回款情况</div>
                                        <div className={styles.progress}>
                                          <div className={styles.text}>
                                            <span>已收款</span>
                                            <span>{collectionRate * 100 || 0}%</span>
                                          </div>
                                          <Tooltip
                                            title={
                                              <>
                                                <div>
                                                  <span>已收金额</span>
                                                  <span>
                                                    {hasValue(receivedAmount)
                                                      ? amountFormat(receivedAmount / 10000)
                                                      : '-'}
                                                  </span>
                                                </div>
                                                <div>
                                                  <span>应收金额</span>
                                                  <span>
                                                    {hasValue(totalAmount)
                                                      ? amountFormat(totalAmount / 10000)
                                                      : '-'}
                                                  </span>
                                                </div>
                                              </>
                                            }
                                          >
                                            <Progress
                                              percent={collectionRate * 100}
                                              showInfo={false}
                                            />
                                          </Tooltip>
                                        </div>
                                      </div>
                                    </Col>
                                    <Col span={16}>
                                      <div className={styles.right}>
                                        <Row gutter={16} style={{ marginBottom: 13 }}>
                                          <Col span={12}>
                                            <div className={styles.label}>已收期数/总期数</div>
                                            <div className={styles.content}>
                                              {`${collectionPhase || '-'}/${totalPhase || '-'}`}
                                            </div>
                                          </Col>
                                          <Col span={12}>
                                            <div className={styles.label}>已收罚息/产生罚息</div>
                                            <div className={styles.content}>
                                              {receivedPenaltyInterest
                                                ? amountFormat(receivedPenaltyInterest / 10000)
                                                : '-'}
                                              /
                                              {penaltyInterest
                                                ? amountFormat(penaltyInterest / 10000)
                                                : '-'}
                                            </div>
                                          </Col>
                                        </Row>
                                        <Row gutter={16}>
                                          <Col span={12}>
                                            <div className={styles.label}>已收本金</div>
                                            <div className={styles.content}>
                                              {receivedPrincipal
                                                ? amountFormat(receivedPrincipal / 10000)
                                                : '-'}
                                            </div>
                                          </Col>
                                          <Col span={12}>
                                            <div className={styles.label}>已收利息</div>
                                            <div className={styles.content}>
                                              {receivedInterest
                                                ? amountFormat(receivedInterest / 10000)
                                                : '-'}
                                            </div>
                                          </Col>
                                        </Row>
                                      </div>
                                    </Col>
                                  </Row>
                                ) : (
                                  <div className={styles.noDataWrap}>
                                    <div className={styles.emptyTitle}>回款情况</div>
                                    <NoData />
                                  </div>
                                )}
                              </div>
                            </Col>
                          </Row>
                        )
                      })}
                    </div>
                  )}
                </Panel>
              )
            })}
          </Collapse>
        </div>
      ) : (
        <NoData />
      )}
    </div>
  )
}

export default observer(Contract)
