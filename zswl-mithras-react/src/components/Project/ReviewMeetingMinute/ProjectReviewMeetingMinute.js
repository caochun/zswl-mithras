import React, { useEffect, useMemo, useState, useRef } from 'react'
import { Tag, Card, Button, Input } from 'antd'
import styles from './index.less'
import { observer, ErrorBoundary } from '@zswl/admin'
import ToolsModal from './Components/ToolsModal'
import MaterialList from './Components/MaterialList'
import { DetailLayout } from '@/components/Layout'
import RentalPlan from './Components/RentalPlan'
import IndexItem from './Components/RentalPlan/DescriptionsItem'
import ReviewValidityPeriod from './Components/ReviewValidityPeriod'
import VotingResultsOfReviewMeeting from './Components/VotingResultsOfReviewMeeting'
import ApprovalConditions from './Components/ApprovalConditions'
import CreditStructure from './Components/CreditStructure'
import Remark from './Components/Remark'
import { Form, App } from '@zswl/components'
import Store from './store'

const Index = ({
  detailData,
  projReviewType = 'PROJ_REVIEW_BASE',
  contentType = 'page',
  moduleName,
}) => {
  const options = App.getData().optionsType
  const layoutModuleName =
    moduleName || window.location.pathname.split('/detail')?.[0]?.split('/')?.join('')
  const componentDisabled = false
  const { projName, businessKey, processInstanceId, taskActivityId } = detailData

  const isProjModify = ['ProjReviewModifyFlow'].includes(detailData.modelKey)
  const [isVisible, setIsVisible] = useState(false)
  const store = useMemo(() => {
    return new Store()
  }, [])
  const {
    init,
    form,
    baseInfoShowValue,
    setBaseInfoShowValue,
    onSave,
    cancelFlow,
    onSubmit,
    onInit,
  } = store || {}
  const anchorList = [
    { label: '租赁方案' },
    { label: '信用结构' },
    { label: '批复条件' },
    { label: '项目变更批复条件', isHide: !isProjModify },
    { label: '评审会议表决结果' },
    { label: '评审有效期' },
    { label: '租金概算表' },
    { label: '备注栏目' },
  ]
  const editDataList = (type) => {
    if (type === 'edit') {
      // 开启编辑
      setBaseInfoShowValue(true)
    } else if (type === 'cancel') {
      // 取消
      cancelFlow()
    } else if (type === 'saveData') {
      // 保存
      onSave(businessKey, processInstanceId)
    } else if (type === 'submit') {
      // 提交
      onSubmit(businessKey, processInstanceId)
    }
  }
  const extra = [
    <div
      className={`${isVisible && contentType === 'page' ? styles.TopF : styles.TopA} ${
        styles[contentType]
      }`}
    >
      {taskActivityId && taskActivityId === 'userTask_jurySecretaryCollect' && (
        <>
          {baseInfoShowValue ? (
            <>
              <Button size={'large'} onClick={() => editDataList('cancel')}>
                取消
              </Button>
              <Button size={'large'} onClick={() => editDataList('saveData')}>
                保存
              </Button>
            </>
          ) : (
            <Button size={'large'} onClick={() => editDataList('edit')}>
              编辑
            </Button>
          )}

          <Button size={'large'} type="primary" onClick={() => editDataList('submit')}>
            提交
          </Button>
        </>
      )}
    </div>,
  ]

  useEffect(() => {
    if (contentType !== 'page') {
      return
    }
    const container = document.getElementById('z-app-layout-container')
    if (container) {
      const handleScroll = () => {
        const scrollTop = container.scrollTop
        setIsVisible(scrollTop > 400)
      }
      container.addEventListener('scroll', handleScroll)
      return () => {
        container.removeEventListener('scroll', handleScroll)
      }
    }
  }, [])
  useEffect(() => {
    onInit({
      projReviewId: businessKey,
      projFlowId: processInstanceId,
      isEffect: processInstanceId ? 0 : 1,
      projReviewType: projReviewType,
    })
  }, [])
  return (
    <ErrorBoundary fallback={<Tag>渲染出错了</Tag>}>
      {init ? (
        <>
          <Form
            className={isVisible ? styles.tabHeadF : styles.tabHeadA}
            store={form}
            initialValues={{
              rentalStartMethod: options.rentalStartMethodEnum[0].value,
              preLeasePeriodFlag: 1,
              // earnestMoneyRatio:10000
              managementRequirement:{
                limitRequirement:'满足风险策略及现行准入制度关于区域限额及客户集中度管控要求。'
              }
            }}
          >
            <div>
              <div className={styles.textTop}>
                <div className={styles.title}>浙江浙商融资租赁有限公司评审委员会会议纪要</div>
                <div className={styles.line}></div>
              </div>

              <div className={styles.code}>
                编号：[{init.reportIssuanceYear}]评审字第[
                <Form.Item
                  name="meetMinuteSequence"
                  className={styles.meetMinuteCode}
                  rules={[{ required: true, message: '请输入!' }]}
                >
                  <Input disabled={!baseInfoShowValue} placeholder="请输入" maxLength={2500} />
                </Form.Item>
                ]号
              </div>
            </div>

            <div className={contentType === 'popo' ? styles.containerPopo : ''}>
              <DetailLayout
                anchorList={anchorList}
                title={'项目名称：' + init.projName}
                moduleName={layoutModuleName}
                anchorSwitch
                extra={extra}
                disabled={componentDisabled}
                form={form}
                className={styles.container}
              >
                <Card title="租赁方案">
                  <RentalPlan
                    form={form}
                    showValue={!baseInfoShowValue}
                    detail={init}
                    businessKey={businessKey}
                  />
                  <IndexItem
                    form={form}
                    showValue={!baseInfoShowValue}
                    detail={init}
                    businessKey={businessKey}
                  />
                </Card>
                <Card title="信用结构">
                  <CreditStructure
                    form={form}
                    showValue={!baseInfoShowValue}
                    detail={init}
                    businessKey={businessKey}
                  />
                </Card>
                <Card title="批复条件">
                  <ApprovalConditions
                    form={form}
                    projName={projName}
                    detailData={detailData}
                    store={store}
                    showValue={!baseInfoShowValue}
                    detail={init}
                    businessKey={businessKey}
                  />
                </Card>
                <Card title="项目变更批复条件">
                  <Form.Item
                    name={'approvalConditionsProjectChange'}
                    required
                    rules={[{ required: true, message: '请输入!' }]}
                  >
                    <Input.TextArea
                      disabled={!baseInfoShowValue}
                      autoSize={{ minRows: 4, maxRows: 20 }}
                      placeholder={baseInfoShowValue && '请输入补充说明'}
                    />
                  </Form.Item>
                </Card>
                <Card title="评审会议表决结果">
                  <VotingResultsOfReviewMeeting
                    form={form}
                    detail={init}
                    showValue={!baseInfoShowValue}
                    isProjModify={isProjModify}
                  />
                </Card>
                <Card title="评审有效期">
                  <ReviewValidityPeriod detail={init} form={form} showValue={!baseInfoShowValue} />
                </Card>
                <Card title="租金概算表">
                  <ToolsModal
                    form={form}
                    showValue={!baseInfoShowValue}
                    taskActivityId={taskActivityId}
                    detail={init}
                    store={store}
                    projReviewType={projReviewType}
                    params={{
                      meetMinuteId: init?.id,
                      processInstanceId: processInstanceId,
                      projFlowId: processInstanceId,
                      projReviewId: businessKey,
                    }}
                  />
                </Card>
                <Card title="备注栏目" className={styles.bzlm}>
                  <Remark showValue={!baseInfoShowValue} detail={init}></Remark>
                  <MaterialList
                    taskActivityId={taskActivityId}
                    detail={init}
                    id={businessKey}
                    store={store}
                  />
                </Card>
              </DetailLayout>
            </div>
          </Form>
        </>
      ) : (
        <div className={styles.noTop}>暂无内容</div>
      )}
    </ErrorBoundary>
  )
}

export default observer(Index)
