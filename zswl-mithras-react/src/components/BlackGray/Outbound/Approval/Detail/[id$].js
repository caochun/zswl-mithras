import { Page, Form, App } from '@zswl/components'
import store from './store'
import { observer } from '@zswl/admin'
import {
  SubmitAuditAction,
  ApprovalRecordAction,
  ApprovalOperation,
} from '../../../actions'
import EnterDesc from '../../../Manage/EnterDesc'
import { Card } from 'antd'

function Id({ params, path, query }) {
  const { view } = query
  const detail = store.page.getData()
  const { auditStatus, auditTaskId, currentOperator } = detail
  const { account } = App.getData().user
  const canAudit = [1].includes(auditStatus) && currentOperator === account
  return (
    <Page
      current="详情"
      params={{ ...params, auditTaskId }}
      store={store}
      header={{
        extra: [
          canAudit && (
            <SubmitAuditAction
              form={store.form}
              key="submit"
              params={{ modelKey: 'BLACK_GRAY_MANUAL_OUTBOUND', taskIds: [auditTaskId] }}
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
      <EnterDesc detail={detail} type="outbound" source="approval" />

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

export default observer(Id)
