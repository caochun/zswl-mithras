import { observer, getQuery } from '@zswl/admin'
import { Page } from '@zswl/components'
import { useMemo } from 'react'
import Store from './store'
import BaseInfo from './BaseInfo'
import { NoEnumFileTable } from '@/components/Table'
import Api from '@/api/financial/creditManage'
import DetailLayout from '@/components/DetailLayout'
import UserDetail from './UserDetail'
const Detail = ({ params: { id }, query: { bizType, newProject, canEditFlags = 'true' } }) => {
  const store = useMemo(() => {
    return new Store({ id })
  }, [id])
  // 是否审批流页面
  const isFormApproval = getQuery('typeId') == 'approval'
  const baseInfoDetail = store.page.getData()
  // 按钮权限控制，审批流后端控制 + 是否主办人
  const canEditFlagsFormAuth = canEditFlags === 'true'

  const columns = [{ title: '资料名称', dataIndex: 'name' }]
  const params = {
    moduleType: 'FUND_CREDIT',
    mainId: id,
  }
  const anchorList = [{ label: '基本信息' }, { label: '资料清单' }]
  return (
    <Page
      store={store}
      params={{ id, newProject: newProject === 'true', isFormApproval }}
      header={null}
    >
      <DetailLayout anchorList={anchorList} title={'授信详情'} extra={null} moduleName="credit">
        <div id="baseInfo">
          {!isFormApproval ? (
            <BaseInfo
              newProject={newProject}
              detail={baseInfoDetail.detail}
              saveData={store.postProjectBaseInfoModify}
              canEdit={canEditFlagsFormAuth}
            />
          ) : (
            <div>
              <BaseInfo
                detail={baseInfoDetail.newDetail}
                isLog={baseInfoDetail.isLog}
                canEdit={canEditFlagsFormAuth}
              />
            </div>
          )}
        </div>
        <UserDetail id={id} />
        <NoEnumFileTable
          title={'资料清单'}
          params={params}
          uploadApi={({ file }) => Api.postFileUpload({ belongId: store.id, file })}
          canEdit={canEditFlagsFormAuth}
          columns={columns}
          functionCodeList={{
            fileList: 'fundCreditFileListNew',
            download: 'newFundCreditFileDownload',
            batchDownload: 'fundCreditFileBatchDownload',
          }}
        />
      </DetailLayout>
    </Page>
  )
}

export default observer(Detail)
