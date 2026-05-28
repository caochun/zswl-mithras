import { observer, getQuery } from '@zswl/admin'
import { Button, Page, Form } from '@zswl/components'
import { useEffect, useMemo } from 'react'
import Store from './store'
import FormCard from './FormCard'
import BaseInfo from './BaseInfo'
import { Space } from 'antd'
import CalcModal from './CalcModal'

const Detail = ({ params: { id }, query: { bizType, newProject, canEditFlags = 'true' } }) => {
  const store = useMemo(() => {
    return new Store({ id })
  }, [id])
  // 是否审批流页面
  const isFormApproval = getQuery('typeId') == 'approval'

  const detail = store.page.getData()
  const { year } = detail
  return (
    <Page
      store={store}
      params={{ id, newProject: newProject === 'true', isFormApproval }}
      header={null}
    >
      <div className="z-flex-jsb" style={{ padding: '6px 0' }}>
        <div className="title">风控策略</div>
        <Button type="primary" onClick={store.tryCalc}>
          试计算
        </Button>
      </div>
      <BaseInfo store={store} detail={detail} />
      <div className="z-flex-jsb" style={{ alignItems: 'center', padding: '6px 0' }}>
        <div className="title">
          {
            '说明：取财经数据表指标,根据分值区间min~max,≥max得满分,≤min得0分,中间线性计算分值,根据权重换算。'
          }
        </div>
        <Space></Space>
      </div>
      <Form store={store.form}>
        <FormCard id={id} store={store} year={year} />
      </Form>
      <CalcModal modalStore={store.calcModal} id={id} year={year} />
    </Page>
  )
}

export default observer(Detail)
