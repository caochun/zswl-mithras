import { Page, Form } from '@zswl/components'
import store from './store'
import { observer } from '@zswl/admin'
import {
  SubmitAuditAction,
  ApprovalRecordAction,
  ApprovalOperation,
} from '../../../actions'
import EnterDesc from '../../../Manage/EnterDesc'
import { Card } from 'antd'

function BlackGrayBreakThroughApprovalDetail({ params, path, query }) {
  const { view } = query
  const detail = store.page.getData()
  const { auditTaskId } = detail
  const canAudit = !view && [1].includes(detail.auditStatus)
  // const canAudit = true
  return (
    <Page
      current="详情"
      params={params}
      store={store}
      header={{
        extra: [
          canAudit && (
            <SubmitAuditAction
              form={store.form}
              key="submit"
              params={{ modelKey: 'BLACK_GRAY_BUSINESS_BREAK', taskIds: [auditTaskId] }}
            />
          ),
          <ApprovalRecordAction
            key="history"
            params={{
              taskId: [auditTaskId],
              bizCode: 'blackGrayFlow',
              bizId: params.id,
            }}
          />,
        ],
      }}
    >
      <EnterDesc detail={detail} type="break" />

      <Form store={store.form} cache={false} initialValues={detail}>
        {canAudit && (
          <Card title="操作意见" type="inner" style={{ marginTop: '12px' }}>
            <ApprovalOperation operation form={store.form} style={{ margin: '0 auto' }} />
          </Card>
        )}
      </Form>
    </Page>
  )
}

export default observer(BlackGrayBreakThroughApprovalDetail)
