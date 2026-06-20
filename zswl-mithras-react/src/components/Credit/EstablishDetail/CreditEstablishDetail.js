import { Button } from 'antd'
import { observer, getQuery } from '@zswl/admin'
import { Page } from '@zswl/components'
import { useMemo } from 'react'
import DataStore from './store'
import BaseInfo from './BaseInfo'
import Data from './Data'
import Report from './Report'
import { ApprovalAction as Approval } from '@/components/Actions'
import { ApprovalDetail } from '@/components/Table'
import DetailLayout from '@/components/DetailLayout'
import CreditModal from '../CreditSearchModal/CreditReportSearchModal'

const Index = ({
  params: { id },
  query: { newProject, canEditFlags = 'true', businessVersion },
}) => {
  const newStore = useMemo(() => {
    return new DataStore()
  }, [])
  // 是否审批流页面
  const isFormApproval = getQuery('typeId') == 'approval'
  const baseInfoDetail = newStore.page.getData()
  // 按钮权限控制，审批流后端控制 + 是否主办人
  const canEditFlagsFormAuth = canEditFlags === 'true' && baseInfoDetail.newDetail?.isBizDept

  const approvalParams = {
    moduleType: 'GROUP_CREDIT_ESTABLISH',
    mainId: id,
    remarkType: 'MODIFY',
  }

  const anchorList = [
    {
      label: '变更说明',
      isHide: baseInfoDetail?.approvalDetail?.remarkJsonList?.length === 0,
    },
    {
      label: '基本信息',
    },
    {
      label: '立项报告',
    },
    {
      label: '资料清单',
    },
  ]
  const extra = [
    <CreditModal params={{ projectId: id, bizSource: 'GROUP_CREDIT_ESTABLISH' }} />,
    <Button type="link" onClick={() => newStore.goRat()}>
      客户评级
    </Button>,
    <Button style={{ marginRight: 8 }} onClick={() => newStore.changeLog(id)}>
      版本日志
    </Button>,
    canEditFlagsFormAuth && (
      <Approval
        params={approvalParams}
        isEffect={baseInfoDetail.newDetail?.groupCreditEstablishStatus === 'TAKE_EFFECT'}
        beforeClick={() => newStore.submitApproval(id, true)}
        onClick={(extParams) => {
          newStore.submitApproval(id, false, extParams)
        }}
      />
    ),
  ]
  return (
    <Page
      store={newStore}
      params={{
        id,
        newProject: newProject === 'true',
        isFormApproval,
        businessVersion,
        approvalParams,
      }}
      header={null}
    >
      <DetailLayout
        anchorList={anchorList}
        title={'授信立项明细'}
        extra={!isFormApproval ? extra : null}
        moduleName="creditEstablish"
      >
        <ApprovalDetail data={baseInfoDetail.approvalDetail} params={approvalParams} />
        <div>
          {!isFormApproval ? (
            <BaseInfo
              newProject={newProject}
              detail={{ ...baseInfoDetail.newDetail, ...newStore.newDetail }}
              saveData={newStore.postProjectBaseInfoModify}
              updateInfo={newStore.updateInfo}
              canEdit={canEditFlagsFormAuth}
            />
          ) : (
            <div>
              <BaseInfo
                detail={{ ...baseInfoDetail.newDetail, ...newStore.newDetail }}
                isLog={baseInfoDetail.isLog}
                saveData={newStore.postProjectBaseInfoModify}
                updateInfo={newStore.updateInfo}
                canEdit={canEditFlagsFormAuth}
              />
            </div>
          )}
        </div>
        <Report id={id} canEdit={canEditFlagsFormAuth} businessVersion={businessVersion} />
        <Data id={id} canEdit={canEditFlagsFormAuth} businessVersion={businessVersion} />
      </DetailLayout>
    </Page>
  )
}

export default observer(Index)
