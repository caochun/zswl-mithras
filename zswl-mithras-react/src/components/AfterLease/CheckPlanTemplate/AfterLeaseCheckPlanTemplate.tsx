import { observer, getQuery, history } from '@zswl/admin'
import { Empty, Radio, Space, Tag, Tooltip } from 'antd'
import Store from './store'
import { TEMPLATE_LIST } from './enum'
import style from './style.less'
import TemplateModal from './TemplateModal'
import DynamicForm from './DynamicForm'
import NoPublicTable from './components/NoPublicTable'
import { Button, Page } from '@zswl/components'
import { useCallback, useEffect, useMemo, useState } from 'react'
import FinancialSituation from './components/FinancialSituation'
import GuarantorFinancial from './components/GuarantorFinancial'
import { isAssetJon, userIsProjSponsor } from '@/utils'
import Api from '@/api/afterLease/checkPlanTemplateApi'
import { TrackEventModal as TrackModal } from '@/components/TrackEvent/TrackEventModalEntries'

const AfterLeaseCheckPlanTemplate = (props) => {
  const store = useMemo(() => {
    return new Store({})
  }, [])
  const { params, query, canEditFlag } = props

  const [processInstanceId, setProcessInstanceId] = useState()

  const { id } = params
  const { active, activeTag, baseInfo } = store
  const { reportTemplateType, clientId, curAssigneeIds, approvalStatus, checkWay, sponsorId } =
    baseInfo ?? {}
  const { canEditFlags, businessVersion, taskActivityId } = query ?? {}
  // 是否审批流页面
  const isFormApproval = getQuery('typeId') == 'approval'
  const isPass = !['APPROVAL_PASS'].includes(approvalStatus)

  const canEdit = () => {
    if (['UNDER_APPROVAL'].includes(approvalStatus)) {
      //  审批审批中：项目经理节点也可以编辑
      return ['project_manager', 'userTask_assetManager'].includes(taskActivityId)
    }
    return !['APPROVAL_PASS'].includes(approvalStatus)
  }
  // 检查形式：已结清无需检查，也无需编辑
  const canEditFlagsFormAuth = useMemo(() => {
    return (
      canEditFlags === 'true' ||
      (canEdit() && userIsProjSponsor(sponsorId) && checkWay !== 'WITHOUT_CHECK')
    )
  }, [approvalStatus, sponsorId, checkWay])

  useEffect(() => {
    id && store.idChange({ id, businessVersion })
  }, [id, businessVersion])

  const getCheckplanPreselect = async () => {
    const result = await Api.checkplanPreselect({
      planClientId: id,
    })
    setProcessInstanceId(result.processInstanceId)
  }

  useEffect(() => {
    getCheckplanPreselect()
  }, [id])

  useEffect(() => {
    store.connectNet()
  }, [])

  const Content = useMemo(() => {
    if (activeTag === undefined || activeTag === null) return <Empty />
    const Dynamic = (
      <DynamicForm
        id={id}
        active={active}
        canEdit={canEditFlagsFormAuth}
        canImport={canEdit()}
        reportTemplateType={reportTemplateType}
        key="NON_PUBLIC"
        businessVersion={businessVersion}
      />
    )
    const clientList = activeTag === 4 ? baseInfo?.lesseeList : baseInfo?.guarantorList
    const GurantorFinancialCom = (
      <GuarantorFinancial
        clientList={clientList}
        key="lesseeList"
        id={id}
        activeTag={activeTag}
        canEdit={canEditFlagsFormAuth}
        businessVersion={businessVersion}
        isFormApproval={isFormApproval}
        canEditFlags={canEditFlags}
        canImport={canEdit()}
      />
    )
    const TAG_COMPONENTS = [
      Dynamic,
      <NoPublicTable
        id={id}
        canEdit={canEditFlagsFormAuth}
        key="NON_PUBLIC_TABLE"
        businessVersion={businessVersion}
      />,
      Dynamic,
      <FinancialSituation
        id={id}
        canEdit={canEditFlagsFormAuth}
        key="FinancialSituation"
        businessVersion={businessVersion}
      />,
      GurantorFinancialCom,
      GurantorFinancialCom,
      Dynamic,
      Dynamic,
      Dynamic,
    ]
    return TAG_COMPONENTS[activeTag]
  }, [activeTag, active, id, canEditFlagsFormAuth, businessVersion, reportTemplateType])
  return (
    <Page store={store} noStyle style={{ background: 'rgb(238, 240, 243)' }} header={null}>
      <div className={style.content}>
        <div className={style.topBox} style={{ marginBottom: 16 }}>
          <div>
            <Radio.Group value={activeTag} onChange={store.tagChange}>
              <Space>
                {TEMPLATE_LIST[active]?.children.map(({ value, name }) => (
                  <div style={{ display: 'flex', alignItems: 'center' }} key={value}>
                    <Radio.Button value={value} key={value} className={style.buttonClamp}>
                      <Tooltip title={name}>{name}</Tooltip>
                    </Radio.Button>
                  </div>
                ))}
              </Space>
            </Radio.Group>
          </div>
          <Space>
            <Button
              type="link"
              onClick={() =>
                history.push(
                  `/afterLease/checkPlan/singleViewRisk${
                    baseInfo.clientName ? `?customerName=${baseInfo.clientName}` : ''
                  }&showBreadcrumb=true`
                )
              }
            >
              客户风险单一视图
            </Button>
            <TrackModal
              params={{ clientId, curAssigneeIds, bizSource: 'AFTER_LEASE', bizId: id }}
            />
            {processInstanceId && (
              <Button
                type="primary"
                onClick={() => {
                  history.push(`/process/query/detail/snapshoot/${processInstanceId}?flag=info`)
                }}
              >
                审批快照
              </Button>
            )}
            {!isFormApproval && (
              <Button onClick={() => store.submitModal.open()} disabled={!isAssetJon()}>
                更换模板
              </Button>
            )}
            {!isPass && (
              <Button
                onClick={() => {
                  return store.downloadReport()
                }}
              >
                下载报告
              </Button>
            )}
            {!isFormApproval && (
              <Button
                type="primary"
                onClick={() => store.submit()}
                disabled={!canEditFlagsFormAuth}
              >
                提交审批
              </Button>
            )}
          </Space>
        </div>

        <div className={style.tableBox}>{Content}</div>
        <TemplateModal store={store} />
      </div>
    </Page>
  )
}

export default observer(AfterLeaseCheckPlanTemplate)
