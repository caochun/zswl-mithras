import { observer, getQuery } from '@zswl/admin'
import { Page, Button, Modal } from '@zswl/components'
import DetailTitle from '../DetailTitle'
import Approval from './Components/Approval'
import CashFlowInfo from './Components/CashFlowInfo'
import Contract from './Components/Contract'
import RecordList from './Components/RecordList'
import styles from './index.less'
import Store from './store'
import { useMemo } from 'react'
import CollectionDate from './Components/CollectionDate'
import SupplementInfo from './Components/SupplementInfo'
import PaymentApplyInfo from './Components/PaymentApplyInfo'
import CollectionConfirm from './Components/CollectionConfirm'
import PaymentApply from './Components/PaymentApply'
import { ExclamationCircleOutlined } from '@ant-design/icons'
import { Space } from 'antd'
import Report from './Report'

const PaymentWriteOffDetail = ({
  params: { id },
  query: {
    canEditFlag = 'true',
    taskActivityId,
    processInstanceId,
    dynamicFormKeyList = [],
    taskStatus,
    businessVersion,
  },
}) => {
  const isFormApproval = getQuery('typeId') == 'approval'
  const canEdit = canEditFlag === 'true'

  const store = useMemo(() => {
    return new Store()
  }, [])

  const pagePage = store.page.getData()
  const { isFinishPutFinal, ftpAssessInfo } = pagePage
  const isZhiZu = pagePage.leaseTypeCode === 'zhi_zu'
  const collectionEdit = dynamicFormKeyList.includes('payment_setCollectionInfo')
  const paymentId = store.actualDetail.paymentId

  return (
    <Page params={{ paymentId: id, isFormApproval, businessVersion }} store={store}>
      <div className={styles.wrap}>
        <DetailTitle
          title={pagePage.paymentCode}
          status={pagePage.writeOffStatus}
          matchKey="paymentWriteOffStatus"
        />

        {!isFormApproval && (
          <Space className={styles.header}>
            <Button
              type="primary"
              onClick={store.advanceSubmit}
              access="paymentreviewinadvancedsubmit"
            >
              运营提前审核
            </Button>
            {!isFinishPutFinal && (
              <Button type="primary" onClick={store.submit} access="paymentactualdetailsubmit">
                提交审批
              </Button>
            )}
          </Space>
        )}
        {isFormApproval && taskActivityId === 'userTask_cashier' && (
          <div className={styles.header}>
            <Button type="primary" onClick={store.pushBank}>
              银企直连推送
            </Button>
          </div>
        )}

        <Contract store={store} />
        <CashFlowInfo store={store} />
        <Approval store={store} />
        <RecordList
          store={store}
          isFormApproval={isFormApproval}
          canEdit={canEdit}
          pagePage={pagePage}
          taskActivityId={taskActivityId}
        />
      </div>
      <CollectionDate store={store} />
      <PaymentApplyInfo store={store} detail={pagePage} paymentId={paymentId} />
      <SupplementInfo detail={pagePage} />
      <CollectionConfirm store={store} detail={pagePage} paymentId={id} canEdit={collectionEdit} />
      {ftpAssessInfo && (
        <PaymentApply
          id={id}
          taskActivityId={taskActivityId}
          taskStatus={taskStatus}
          detail={ftpAssessInfo}
          store={store}
        />
      )}
      <Report mainId={id} businessVersion={businessVersion} canEdit={canEdit} />
      <Modal
        store={store.confirmModal}
        footer={
          <Space>
            <Button onClick={store.confirmCancel}>否</Button>
            <Button type="primary" onClick={store.confirmOk}>
              是
            </Button>
          </Space>
        }
        title={
          <div>
            <ExclamationCircleOutlined
              style={{ color: '#ffa640', marginRight: 12, fontSize: 20 }}
            />
            系统提示
          </div>
        }
      >
        <div>本次投放之后，该笔付款申请单是否结束投放？</div>
      </Modal>
    </Page>
  )
}
export default observer(PaymentWriteOffDetail)
