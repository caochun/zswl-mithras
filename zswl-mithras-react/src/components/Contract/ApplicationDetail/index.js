import { useEffect, useMemo, useRef } from 'react'
import { observer, getQuery, history } from '@zswl/admin'
import { Page, Button, Form, Modal } from '@zswl/components'
import { Input, InputNumber } from 'antd'
import DetailLayout from '@/components/DetailLayout'
import BaseInfo from '../BaseInfo'
import GaiSuanZuJin from './GaiSuanZuJin'
import ShiJiZuJin from './ShiJiZuJin'
import ChengZuRen from './ChengZuRen' // 租赁、转租赁场景 - ZL、ZZ
import HuiKuan from './HuiKuan' // 保理、债权转让场景- BL、ZR
import ShouKuan from './ShouKuan'
import BaoJia from '../Detail/BaoJia'
import { Context } from '../Detail/Context'
import ZuLinWu from '../Detail/LeaseItemList'
import DanBao from './DanBao'
import ZhiYa from './ZhiYa'
import DiYa from './DiYa'
import HeTong from '../ContractText'
import ZiLiao from '../ContractMaterials'
import ContractTextType from './ContractTextType'
import CheckRemark from './CheckRemark'
import CheckMaterial from '../ChangeMaterials'
import { bizTypeMapText } from '../bizTypeConfig'
import Store from './store'
import FormIrr from '@/components/FormIrr'
import { ApprovalDetail } from '@/components/Table'
import { ApprovalAction as Approval } from '@/components/Actions'
import { BusinessInfoCheck } from '@/components/BusinessInfoCheck/BusinessInfoCheckEntries'
import LeaseZiLiao from '../LeaseMaterials'
import { TrackingModal as TrackModal } from '@/components/TrackEvent/TrackingEntries'
import { EvaluationAgency } from '@/components/EvaluationAgency/EvaluationAgencyEntries'
import { ProjectReviewMeetingModal as MeetingModal } from '@/components/Project/ReviewMeetingEntries'
import { jumpZhongDeng } from '@/utils'

const Index = ({
  params: { id },
  query: {
    businessVersion,
    formChangeOther = 'false',
    canEditFlags = 'true',
    taskActivityId,
    processInstanceId,
    modelKey,
    taskStatus,
  },
  isNewLayout,
}) => {
  // 是否审批流页面
  const isFormApproval = getQuery('typeId') == 'approval'
  const store = useMemo(() => {
    return new Store({ isFormApproval })
  }, [isFormApproval])

  const { bizType, contractStatus, contractProcessStatus, leaseTypes } = store
  const baseInfoDetail = store.page.getData()
  const { isProjSponsor } = baseInfoDetail
  const { contractCode, curAssigneeIds, projReviewId } =
    (isFormApproval ? baseInfoDetail.newDetail : baseInfoDetail.detail) ?? {}

  // 合同状态 、审批流状态 能编辑的状态
  const canEditFlagAsStatus =
    !['INVALID'].includes(contractStatus) &&
    ['NEW_UNCOMMIT', 'NEW_COMMIT', 'NEW_CANCEL'].includes(contractProcessStatus)

  // 变更类型-其它，会传入此参数（formChangeOther=‘true’）,【基本信息】和【实际租金表】模块均不可编辑
  const isFormChangeType = formChangeOther === 'true'

  // 按钮权限控制
  const canEditFlagsFormAuth = isFormApproval ? canEditFlags === 'true' : canEditFlagAsStatus

  const auth = isFormChangeType
    ? canEditFlags === 'true' && isProjSponsor
    : canEditFlagsFormAuth && isProjSponsor

  // 开放运营经办岗位在合同创建和合同变更流程中以下模块的编辑权限，包括：承租人模块、担保人模块和未合同相关材料模块。
  const yuYingJingBanCanEdit = taskStatus === '1' && taskActivityId === 'userTask_yunYingGuanLi'

  // 页面不同场景，获取bizType
  const BL_ZR = bizType === 'BL' || bizType === 'ZR'

  const approvalParams = {
    moduleType: 'CONTRACT',
    mainId: id,
    remarkType: 'MODIFY',
  }
  const ref = useRef()

  const goProcess = () => {
    const search = JSON.stringify({
      projName: isFormApproval
        ? baseInfoDetail.newDetail?.projName
        : baseInfoDetail.detail?.projName,
    })
    if (isFormApproval) {
      window.open(`/process/query?search=${search}`)
    } else {
      history.push(`/process/query?search=${search}`)
    }
  }

  const anchorList = [
    {
      label: '变更说明',
      isHide: !(isFormChangeType && baseInfoDetail.approvalDetail?.remarkJsonList?.length > 0),
    },
    { label: '基本信息' },
    { label: '报价方案' },
    { label: `概算${bizTypeMapText[bizType]?.rentTitle}` },
    { label: `实际${bizTypeMapText[bizType]?.rentTitle}` },
    { label: BL_ZR ? '债权人/债务人' : '承租人' },
    { label: bizType === 'BL' ? '保理回款账户' : '回款账户', isHide: !BL_ZR },
    { label: BL_ZR ? '卖方收款账户' : '收款账户' },
    { label: '租赁物清单', isHide: BL_ZR },
    { label: '评估机构', isHide: BL_ZR },
    { label: '担保措施' },
    { label: '抵押措施' },
    { label: '质押措施' },
    { label: '合同文本类型' },
    { label: '合同相关材料', isHide: isFormApproval && isNewLayout },
    { label: '资料清单', isHide: isFormApproval && isNewLayout },
  ].filter(Boolean)

  const hasMeeting = isFormApproval
    ? ['ContractCreateFlow', 'ContractModifyFlow'].includes(modelKey)
    : true

  const extra = [
    hasMeeting && <MeetingModal id={projReviewId} />,
    <Button onClick={goProcess} type="link">
      查询历史流程
    </Button>,
    <Button onClick={() => jumpZhongDeng({})} type="link">
      中登网查询
    </Button>,
    <TrackModal params={{ contractCode, curAssigneeIds, bizSource: 'CONTRACT', bizId: id }} />,
    <BusinessInfoCheck
      contractId={id}
      taskStatus={taskStatus}
      flowId={processInstanceId}
      modelKey={modelKey}
      taskActivityId={taskActivityId}
    />,
    // {/* 审批流页、其它变更 不可查看 */}
    !isFormApproval && !isFormChangeType && (
      <Button onClick={() => store.changeLog(id)}>版本日志</Button>
    ),
    // {/* 其它变更、有权限 才可操作 */}
    !isFormApproval && isFormChangeType && isProjSponsor && (
      <Button onClick={() => store.cancelFlow()}>取消操作</Button>
    ),
    // {/* 有权限才给操作 */}
    !isFormApproval && auth && (
      <Context.Consumer>
        {(context) => (
          <Approval
            params={approvalParams}
            isEffect={isFormChangeType}
            // loading={store.approvalLoading}
            beforeClick={() => {
              return store.submitApproval({
                id,
                type: isFormChangeType,
                onlyCheck: true,
                baoJiaRef: context?.ref?.current,
              })
            }}
            onClick={(remarkAddREQ) => {
              store.submitApproval({
                id,
                type: isFormChangeType,
                onlyCheck: false,
                extParams: remarkAddREQ,
                baoJiaRef: context?.ref?.current,
              })
            }}
          />
        )}
      </Context.Consumer>
    ),
  ]

  const changeCanEdit = isFormApproval && canEditFlags === 'true'
  return (
    <Page
      store={store}
      header={null}
      params={{ contractId: id, businessVersion, isFormApproval, approvalParams }}
    >
      <Context.Provider value={{ ref }}>
        <DetailLayout
          anchorList={anchorList}
          title={isFormChangeType ? '合同其他变更' : '合同详情'}
          extra={extra}
          moduleName="contract"
        >
          <ApprovalDetail
            data={baseInfoDetail.approvalDetail}
            params={approvalParams}
            canEdit={changeCanEdit}
          />
          <div>
            {isFormApproval ? (
              <BaseInfo
                detail={baseInfoDetail.newDetail}
                canEditFlag={!isFormChangeType && auth}
                baseStore={store}
                compareData={Object.keys(baseInfoDetail.isLog || {})}
              />
            ) : (
              <BaseInfo
                detail={baseInfoDetail.detail}
                canEditFlag={!isFormChangeType && auth}
                baseStore={store}
              />
            )}
          </div>
          <BaoJia
            canEditFlag={auth}
            baseStore={store}
            contractId={id}
            bizType={bizType}
            businessVersion={businessVersion}
            isFormApproval={isFormApproval}
          />
          <div>
            <GaiSuanZuJin
              contractStatus={contractStatus}
              baseStore={store}
              canEditFlag={auth}
              formChangeOther={formChangeOther}
            />
            <Form store={store.baseForm}>
              <Form.Item
                style={{ width: 200 }}
                name="pricingIrrPercent"
                label={<div className="z-sub-title">定价IRR</div>}
                rules={[{ required: true }]}
                canEdit={false}
              >
                <InputNumber addonAfter="%" disabled step="0.01"></InputNumber>
              </Form.Item>
              <FormIrr.Item
                isChange={store.irrIsChange}
                name="irr"
                label={<div className="z-sub-title">概算IRR</div>}
                rules={[{ required: true }]}
                onBlur={store.irrChange}
                handleOpen={store.handleOpen}
                canEdit={auth}
              />
            </Form>
          </div>
          <ShiJiZuJin baseStore={store} canEditFlag={auth} />
          <ChengZuRen baseStore={store} canEditFlag={auth || yuYingJingBanCanEdit} />
          <HuiKuan baseStore={store} canEditFlag={auth} />
          <ShouKuan baseStore={store} canEditFlag={auth} />
          <ZuLinWu
            baseStore={store}
            canEditFlag={isFormChangeType ? canEditFlags === 'true' : canEditFlagsFormAuth}
            flowId={processInstanceId}
            taskStatus={taskStatus}
            taskActivityId={taskActivityId}
          />
          <EvaluationAgency
            id={id}
            canEdit={false}
            notLease
            functionCodeList={{
              detail: 'contractLedgerAppraisalDetail',
              list: 'contractEvaluationAgencyList',
              fileList: 'contractEvaluationAgencyFileList',
              download: 'contractEvaluationAgencyFileDownload',
              batchDown: 'contractEvaluationAgencyFileBatchDownload',
            }}
          />
          <DanBao
            baseStore={store}
            canEditFlag={auth || yuYingJingBanCanEdit}
            yuYingJingBanCanEdit={yuYingJingBanCanEdit}
          />
          <DiYa baseStore={store} canEditFlag={auth} />
          <ZhiYa baseStore={store} canEditFlag={auth} />
          <ContractTextType id={id} canEditFlag={auth} taskActivityId={taskActivityId} />
          <div>
            <HeTong
              title="合同相关材料"
              isFormChangeType={isFormChangeType}
              canEditFlag={auth}
              yuYingJingBanCanEdit={yuYingJingBanCanEdit}
              id={id}
              taskActivityId={taskActivityId}
              modelKey={modelKey}
              businessVersion={businessVersion}
              baseStore={store}
              taskStatus={taskStatus}
            />
            {isFormChangeType && (
              <CheckMaterial
                id={id}
                canEdit={auth}
                businessVersion={businessVersion}
                taskStatus={taskStatus}
                taskActivityId={taskActivityId}
              />
            )}
          </div>
          <div>
            <ZiLiao
              canEditFlag={auth}
              id={id}
              businessVersion={businessVersion}
              title={'资料清单'}
            />
            {leaseTypes === 'hui_zu' && bizType === 'ZL' && (
              <LeaseZiLiao id={id} businessVersion={businessVersion} />
            )}
          </div>
        </DetailLayout>
        <Modal title="合同提交备注说明" store={store.remarkModal} okText="继续提交">
          <Form>
            <Form.Item name="remark">
              <Input.TextArea maxLength={2500} />
            </Form.Item>
          </Form>
        </Modal>
      </Context.Provider>
    </Page>
  )
}

export default observer(Index)
