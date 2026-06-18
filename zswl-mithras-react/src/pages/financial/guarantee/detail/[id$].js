import { observer, getQuery } from '@zswl/admin'
import { Button, Page } from '@zswl/components'
import { useEffect, useMemo, useRef } from 'react'
import Store from './store'
import GuaranteeInfo from './GuaranteeInfo'
import AsyncModal from './AsyncModal'
import { EditDescription } from '@/components/Table'
import columns from '../InfoColumn'
import DetailLayout from '@/components/DetailLayout'
import UserDetail from './UserDetail'

const Detail = ({ params: { id }, query: { bizType, newProject, canEditFlags = 'true' } }) => {
  const baseInfoRef = useRef()
  const store = useMemo(() => {
    return new Store()
  }, [])

  const asyncThePage = async () => {
    await store.asyncThePage(id)
    baseInfoRef.current?.setBaseEdit?.(true)
  }
  const isEyeChange = (val) => {
    store.isEyeChange(val)
    const value = !val.isEye ? val.data : store.baseData[val.field]
    baseInfoRef.current.form.setFieldValue(val.field, value)
  }
  // 是否审批流页面
  const isFormApproval = getQuery('typeId') == 'approval'
  const baseInfoDetail = store.page.getData()
  // 按钮权限控制，审批流后端控制 + 是否主办人
  const canEditFlagsFormAuth = canEditFlags === 'true'

  const anchorList = [{ label: '基本信息' }, { label: '担保信息' }, { label: '使用详情' }]

  const extra = [
    <Button onClick={asyncThePage} loading={store.syncLoading} access="fundguaranteeagencysync">
      同步当前页
    </Button>,
  ]

  return (
    <Page
      store={store}
      params={{ id, newProject: newProject === 'true', isFormApproval }}
      header={null}
    >
      <DetailLayout anchorList={anchorList} title={'担保详情'} extra={extra} moduleName="guarantee">
        <div>
          {!isFormApproval ? (
            <EditDescription
              detail={baseInfoDetail.detail}
              saveData={store.postProjectBaseInfoModify}
              canEdit={canEditFlagsFormAuth}
              columns={columns}
              ref={baseInfoRef}
            />
          ) : (
            <EditDescription
              detail={baseInfoDetail.newDetail}
              isLog={baseInfoDetail.isLog}
              canEdit={canEditFlagsFormAuth}
            />
          )}
        </div>
        <GuaranteeInfo id={id} />
        <UserDetail id={id} />
      </DetailLayout>
      <AsyncModal
        visible={store.visible}
        // onOk={store.syncFinish}
        isEyeChange={isEyeChange}
        onCancel={store.syncCancel}
        tableStore={store.asyncTable}
      />
    </Page>
  )
}

export default observer(Detail)
