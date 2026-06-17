import { Page, Form, Descriptions, Select, Button, Upload, App, Table } from '@zswl/components'
import store from './store'
import { observer } from '@zswl/admin'
import { SubmitAuditAction, ApprovalRecordAction } from '@/components/RiskActions'
import LoginInfo from '@/components/BlackGray/Manage/LoginInfo'
import EnterForm from '@/components/BlackGray/Manage/EnterForm'
import EnterDesc from '@/components/BlackGray/Manage/EnterDesc'

function Id({ params, path, query }) {
  const { view } = query
  const canAudit = params.id

  const detail = store.page.getData()
  return (
    <Page
      current="详情"
      params={params}
      store={store}
      header={{
        extra: [
          !view && (
            <Button.Save onClick={store.save} key="save">
              保存
            </Button.Save>
          ),
          !view && (
            <SubmitAuditAction
              form={store.form}
              key="submit"
              params={{ modelKey: 'BLACK_GRAY_WAREHOUSE', taskIds: [] }}
              onSubmit={store.submit}
            />
          ),
          !view && (
            <ApprovalRecordAction
              key="history"
              params={{
                // taskId: taskIds,
                bizCode: 'blackGrayFlow',
                bizId: params.id,
              }}
            />
          ),
        ],
      }}
    >
      {view ? <EnterDesc detail={detail} /> : <EnterForm initialValues={detail} store={store} />}

      <LoginInfo />
    </Page>
  )
}

export default observer(Id)
