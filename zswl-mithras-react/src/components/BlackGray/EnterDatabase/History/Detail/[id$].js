import { Page, Button } from '@zswl/components'
import store from './store'
import { observer } from '@zswl/admin'
import { SubmitAuditAction, ApprovalRecordAction } from '../../../actions'
import LoginInfo from '../../../Manage/LoginInfo'
import EnterForm from '../../../Manage/EnterForm'
import EnterDesc from '../../../Manage/EnterDesc'

function Id({ params, path, query }) {
  const { view } = query
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
