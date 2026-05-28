import { observer, getQuery } from '@zswl/admin'
import { Button, Form, Page } from '@zswl/components'
import { useEffect, useMemo } from 'react'
import Store from './store'
import BaseInfo from './BaseInfo'
import Report from './Report'
import { CurrentSteps } from '@/components'
import RatingAdjustment from './RatingAdjustment'
import { Space } from 'antd'
import EditScore from './EditScore'

const Detail = ({ params: { id }, query: { bizType, newProject, canEditFlags = 'true' } }) => {
  const isFormApproval = getQuery('typeId') == 'approval'
  const store = useMemo(() => {
    return new Store()
  }, [])
  // 是否审批流页面
  const { customerDetail, paramInfo, baseInfoDetail, initialValues } = store.page.getData() ?? {}
  // 按钮权限控制，审批流后端控制 + 是否主办人
  const canEditFlagsFormAuth = canEditFlags === 'true'
  const canEditStatus = !['UNDER_APPROVAL', 'APPROVAL_PASS'].includes(baseInfoDetail?.processStatus)
  const auth = isFormApproval ? canEditFlagsFormAuth : canEditStatus
  const commonProps = {}

  const handleAdjust = (groupName, value) => {
    if (value) return
    if (groupName === '房地产调整项') {
      store.form.setFieldsValue({
        Initial_value_real_estate: 0,
        real_estate_type: 'D',
        real_estate_location: 'D',
        the_location_real_estate: 'C',
      })
    }
    if (groupName === '股权调整项') {
      store.form.setFieldsValue({
        equity_market_value: 0,
        monetization_conditions: 'E',
        pledge_ratio_major_shareholders: 'A',
      })
    }
  }
  const steps = [
    {
      title: '项目信息',
      content: <BaseInfo detail={customerDetail} />,
    },
    {
      title: '评价问卷',
      content: (
        <div>
          <EditScore paramInfo={paramInfo} auth={auth} title={'评估基准'} />
          <EditScore
            paramInfo={paramInfo}
            auth={auth}
            title={'增信措施'}
            form={store.form}
            handleAdjust={handleAdjust}
          />
          <RatingAdjustment store={store} paramInfo={paramInfo} auth={auth} />
        </div>
      ),
    },

    {
      title: '评级报告',
      content: <Report store={store} baseInfoDetail={baseInfoDetail} />,
      isPreview: true,
    },
  ].filter(Boolean)

  const onNext = async (current) => {
    if (current === 0) {
    }
    if (current === 1) {
      await store.save(true)
    }
  }
  return (
    <Page
      store={store}
      params={{ id, newProject: newProject === 'true', isFormApproval }}
      header={{
        arrow: false,
        title: (
          <Space>
            <span>
              项目: <span style={{ fontWeight: 800 }}>{customerDetail?.projName}</span>
            </span>
          </Space>
        ),
      }}
    >
      {isFormApproval && !canEditFlagsFormAuth ? (
        <Form store={store.form}>
          <Report store={store} />
        </Form>
      ) : (
        <Form store={store.form} layout="vertical" initialValues={initialValues}>
          <CurrentSteps
            steps={steps}
            onCancel={store.onCancel}
            onNext={onNext}
            canClick={!auth}
            onSubmit={!isFormApproval && store.submitApproval}
            offsetTop={isFormApproval ? 0 : 55}
            nextName={(current) => (current === 1 ? '确认完成评级' : '下一步')}
            submitText={'提交'}
            extra={(current) => {
              if (current === 1) {
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
