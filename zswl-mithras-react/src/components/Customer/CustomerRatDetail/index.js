import { observer, getQuery } from '@zswl/admin'
import { Button, Form, Page } from '@zswl/components'
import { useEffect, useMemo } from 'react'
import Store from './store'
import BaseInfo from './BaseInfo'
import QualitativeScore from './QualitativeScore'
import QuantitativeScore from './QuantitativeScore'
import Report from './Report'
import CurrentSteps from '@/components/CurrentSteps'
import RatingAdjustment from './RatingAdjustment'
import { Space } from 'antd'
import OverturnModal from './OverturnModal'
import { getUserInfo } from '@/utils'

const Detail = ({ params: { id }, query: { canEditFlags = 'true', canApproval, taskId, model } }) => {
  const isApproval = getQuery('curTab') === 'approval'
  const canApprovalStatus = canApproval === 'true' && !isApproval
  const isFormApproval = getQuery('typeId') == 'approval'
  const isPre = getQuery('tab') === 'prepare'
  console.log('isPre: ', (isPre || !isFormApproval) );

  const store = useMemo(() => {
    return new Store({ model })
  }, [])
  // 是否审批流页面
  const { customerDetail, paramInfo, baseInfoDetail, initialValues } = store.page.getData() ?? {}
  // 按钮权限控制，审批流后端控制 + 是否主办人
  const userInfo = getUserInfo()
  const isHost = baseInfoDetail?.createBy === userInfo.id
  const canEditFlagsFormAuth = canEditFlags === 'true' && isHost
  const isZX = ['client_qxj_service', 'client_djs_service'].includes(baseInfoDetail?.modelCode)
  const canEditStatus = !['UNDER_APPROVAL', 'APPROVAL_PASS'].includes(baseInfoDetail?.processStatus)
  const canEdit = isFormApproval ? canEditFlagsFormAuth : canEditStatus && isHost
  const commonProps = {}
  const steps = [
    {
      title: '客户信息',
      content: <BaseInfo detail={{ ...baseInfoDetail, ...customerDetail }} />,
    },
    {
      title: '评价问卷',
      content: (
        <div>
          {
            model !== 'client_hymx' &&
            <QuantitativeScore
              paramInfo={paramInfo}
              id={id}
              store={store}
              initialValues={initialValues}
              auth={canEdit}
              isZX={isZX}
            />
          }
          <QualitativeScore initialValues={initialValues} store={store} paramInfo={paramInfo} auth={canEdit} isFirst={model !== 'client_hymx'} />
        </div>
      ),
    },
    {
      title: '评价调整项',
      content: <RatingAdjustment store={store} paramInfo={paramInfo} auth={canEdit} />,
    },
    {
      title: '评级报告',
      content: (
        <Report
          store={store}
          baseInfoDetail={baseInfoDetail}
          auth={canEdit}
          canApproval={canApprovalStatus}
          isZX={isZX}
          model={model}
        />
      ),
      isPreview: true,
    },
  ].filter(Boolean)

  const onNext = async (current) => {
    if (current === 0) {
    }
    if (current === 2) {
      await store.save(true)
    }
  }
  const defaultCurrent = canEditFlags === 'true' ? 0 : 3

  return (
    <Page
      store={store}
      params={{ id, isFormApproval, taskId }}
      header={{
        arrow: false,
        onBack: () => { },
        title: (
          <Space>
            <span>
              客户: <span style={{ fontWeight: 800 }}>{baseInfoDetail?.clientName}</span>
            </span>
            <span>
              评级模型为: <span style={{ fontWeight: 800 }}>{baseInfoDetail?.modelName}</span>
            </span>
          </Space>
        ),
      }}
    >
      {isFormApproval && !canEditFlagsFormAuth ? (
        <Form store={store.form}>
          <Report store={store} canApproval={canApprovalStatus} isZX={isZX} model={model} />
        </Form>
      ) : (
        <Form store={store.form} layout="vertical" initialValues={initialValues}>
          <CurrentSteps
            steps={steps}
            onCancel={store.onCancel}
            submitText={'提交审核'}
            defaultCurrent={defaultCurrent}
            onNext={onNext}
            nextName={(current) => (current === 2 ? '确认完成评级' : '下一步')}
            canClick={!canEdit}
            offsetTop={0}
            onSubmit={(isPre || !isFormApproval) ? () => store.submitApproval() : undefined}
            extra={(current) => {
              if (current === steps.length - 1) {
                return (
                  <OverturnModal
                    uploadParams={{
                      moduleType: 'RATING_CLIENT',
                      mainId: id,
                      materialsType: 'RATING_CLIENT_SUPPLEMENT_FILE',
                    }}
                    afterSubmit={(params) => store.submitApproval(params)}
                  />
                )
              }
              if (current === 2) {
                return [<Button onClick={() => store.save(false)}>保存草稿</Button>]
              }
            }}
          />
        </Form>
      )}
    </Page>
  )
}

export default observer(Detail)
