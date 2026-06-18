import { Page, Form, Descriptions, Select, Button, Upload, App, Table } from '@zswl/components'
import store from './store'
import { observer } from '@zswl/admin'
import {
  SubmitAuditAction,
  ApprovalRecordAction,
  ApprovalOperation,
} from '@/components/BlackGray/BlackGrayEntries'
import { Card, Checkbox } from 'antd'
import { getDescColumns } from '@/utils'
import { BlackGrayColumns as ALl_COLUMNS } from '@/components/BlackGray/BlackGrayEntries'

function Id({ params, path, query }) {
  const { view } = query

  const columns = getDescColumns(ALl_COLUMNS, [
    '企业名称',
    '统一社会信用代码',
    '业务类型',
    '黑灰标识',
    { title: '申请原因', rename: '入库原因' },
    '业务规模（万元）',
    '入库时间',
    { title: '申请原因描述', rename: '入库原因说明' },
    { title: '申请原因附件', rename: '附件' },
  ])
  const detail = store.page.getData()
  const { auditTaskId, auditStatus } = detail
  const { account } = App.getData().user
  const canAudit = [1, 2].includes(auditStatus)
  //  && detail.currentOperator === account
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
              params={{ modelKey: 'BLACK_GRAY_WAREHOUSE', taskIds: [auditTaskId] }}
              // onSubmit={store.submit}
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
      <Descriptions
        dataSource={detail}
        items={columns}
        bordered
        labelStyle={{ width: '160px' }}
        contentStyle={{ width: 230 }}
      ></Descriptions>
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
