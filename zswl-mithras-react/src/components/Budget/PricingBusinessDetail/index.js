import { Space } from 'antd'
import { observer, getQuery, http } from '@zswl/admin'
import { Page, Button, App, Form } from '@zswl/components'
import { useMemo, useRef, useState } from 'react'
import Store from './store'
import MonthFTP, { MonthlyDeduction, MonthlyDeductionSupport } from './MonthFTP'
import newFtpBaseInfoApi from '@/api/budget/pricing/ftp/newFtpBaseInfoApi'
import styles from './index.less'
import { ExportAction as Export } from '@/components/Actions'
import { isFinancialOfficer } from '@/utils'
import CurrentSteps from '@/components/CurrentSteps'
import BaseSet from './BaseSet'
import DetailLayout from '@/components/DetailLayout'

export const FTPContext = React.createContext({})

const Index = ({
  params: { id },
  query: { newProject, canEditFlags = 'true', businessVersion },
}) => {
  const store = useMemo(() => {
    return new Store()
  }, [])
  // 是否审批流页面
  const isFormApproval = getQuery('typeId') == 'approval'
  const { page, form, submitDisabled, setSubmitDisabled, descData, getDesc } = store
  const { detail } = page.getData()
  // 按钮权限控制，审批流后端控制 + 是否主办人
  const hasValuation = isFinancialOfficer()
  // const auth = canEditFlags === 'true' && hasValuation
  const isApprovalStatus = ['NEW_APPROVAL_PASS', 'NEW_UNDER_APPROVAL'].includes(
    detail?.ftpProcessStatus
  )
  const auth = hasValuation && (isFormApproval ? canEditFlags === 'true' : !isApprovalStatus)

  const month = detail?.month
  const isV3 = detail?.ftpBusinessVersion === 'V3'
  const extra = useMemo(() => {
    return (
      !isFormApproval && (
        <Space>
          <Button onClick={() => store.changeLog(id, month)} key="log">
            版本日志
          </Button>
          <Export onClick={() => newFtpBaseInfoApi.postPricingExport({ mainId: id })} />
        </Space>
      )
    )
  }, [auth, id, isFormApproval, store, month, submitDisabled])

  const descChange = async (e, dataIndex) => {
    const findId = descData.find((v) => v.descType === dataIndex).id
    setSubmitDisabled(true)
    await newFtpBaseInfoApi
      .postInfoDescmodify({ id: findId, descContent: e.target.value })
      .finally((v) => setSubmitDisabled(false))
  }

  const anchorList = [
    { label: '维护基础数据' },
    { label: '确认计价标准' },
    { label: `确认 FTP 定价` },
  ]
  const commonProps = {
    mainId: id,
    businessVersion,
    canEdit: auth,
    isV3,
  }
  const monthlyDeductionRef = useRef()
  const onNext = async (current) => {
    if (current === 0 && !store.calculateDeductionFlag) {
      return await http.post('/new/ftp/monthly/deduction/add', { mainId: id }).then(async (res) => {
        await getDesc({ id })
        store.calculateDeductionFlag = 1
      })
    }
    if (current === 1 && !store.calculateGuidanceFlag) {
      return await http.post('/new/ftp/monthly/guidance/add', { mainId: id }).then(async (res) => {
        await getDesc({ id })
        store.calculateGuidanceFlag = 1
      })
    }
  }
  const steps = [
    {
      title: '维护基础数据',
      content: <BaseSet {...commonProps} />,
    },
    {
      title: '确认计价标准',
      content: (
        <div>
          <MonthlyDeduction
            {...commonProps}
            descChange={descChange}
            setSubmitDisabled={setSubmitDisabled}
            ref={monthlyDeductionRef}
          />
          <MonthlyDeductionSupport {...commonProps} />
        </div>
      ),
      isPreview: !store.calculateDeductionFlag,
    },
    {
      title: '确认 FTP 定价',
      content: (
        <DetailLayout
          anchorList={[{ label: '确认 FTP 定价' }, { label: `补充说明` }]}
          offsetTop={120}
        >
          <MonthFTP
            {...commonProps}
            descChange={descChange}
            detail={detail}
            setSubmitDisabled={setSubmitDisabled}
          />
        </DetailLayout>
      ),
      isPreview: !store.calculateGuidanceFlag,
    },
    auth && {
      title: '预览并提交',
      content: (
        <DetailLayout anchorList={anchorList} offsetTop={120}>
          <BaseSet mainId={id} businessVersion={businessVersion} canEdit={false} />
          <MonthlyDeduction
            mainId={id}
            canEdit={false}
            businessVersion={businessVersion}
            isV3={isV3}
          />
          <MonthFTP
            canEdit={false}
            detail={detail}
            mainId={id}
            businessVersion={businessVersion}
            isV3={isV3}
          />
        </DetailLayout>
      ),
      isPreview: true,
    },
  ].filter(Boolean)
  return (
    <Page
      store={store}
      params={{
        id,
        newProject: newProject === 'true',
        isFormApproval: !auth,
        businessVersion,
        hasValuation,
      }}
      style={{
        background: '#fff',
      }}
      header={null}
      noStyle
      extra={extra}
    >
      <FTPContext.Provider value={{ mainId: id, businessVersion, detail }}>
        <Form store={form} className={styles.wrap}>
          <CurrentSteps
            steps={steps}
            onCancel={store.onCancel}
            onNext={onNext}
            canClick={!auth}
            onSubmit={!isFormApproval && store.submitApproval}
            disabled={submitDisabled}
            offsetTop={isFormApproval ? 0 : 55}
          />
        </Form>
      </FTPContext.Provider>
    </Page>
  )
}

export default observer(Index)
