import DetailLayout from '@/components/DetailLayout'
import { getQuery, observer } from '@zswl/admin'
import { Button, Modal, Page } from '@zswl/components'
import { message } from 'antd'
import { useMemo, useRef } from 'react'
import Asset from './Asset'
import BaseInfo from './BaseInfo'
import Data from './Data'
import Financing from './Financing'
import OtherAccount from './OtherAccount'
import Pledge from './Pledge'
import Product from './Product'
import Property from './Property'
import Repay from './Repay'
import Subscribe from './Subscribe'
import Api from './api'
import Store from './store'

function Index({ params: { id }, query = {} }) {
  const store = useMemo(() => new Store(), [])
  const detail = store.page.getData()
  const isFormApproval = getQuery('typeId') == 'approval'
  const repayRef = useRef()
  store.repayRef = repayRef

  const { obsolete, directFinancingType } = detail

  const disabled = query.canEditFlags !== undefined
    ? query.canEditFlags === 'false'
    : obsolete
  const syncInfo = () => {
    Modal.confirm({
      title: '确认将信息同步至还本付息吗？',
      onOk: async () => {
        await Api.syncBaseInfo({ id })
        message.success('同步成功')
      },
    })
  }
  return (
    <Page store={store} params={{ id }}>
      <DetailLayout
        moduleName="direct"
        title="直融详情"
        anchorList={[
          { label: '基本信息' },
          { label: '资产池信息' },
          { label: '关联合同明细' },
          { label: '投放资产明细' },
          { label: '产品明细' },
          { label: '认购明细' },
          { label: '融资方案' },
          { label: '实际还款计划' },
          { label: '对方收款账户' },
          { label: '资料清单' },
        ]}
        extra={
          !isFormApproval && (
            <Button disabled={disabled} onClick={syncInfo} type="primary">
              提交生效
            </Button>
          )
        }
      >
        <BaseInfo store={store} id={id} disabled={disabled} />
        <Asset id={id} disabled={disabled} />
        <Pledge id={id} canEdit={!disabled} baseStore={store} />
        <Property id={id} disabled={disabled} baseStore={store} />
        <Product id={id} disabled={disabled} store={store} />
        <Subscribe id={id} disabled={disabled} />
        <Financing id={id} disabled={disabled} store={store} />
        <Repay id={id} detail={detail} disabled={disabled} ref={repayRef} store={store} />
        <OtherAccount id={id} disabled={disabled} />
        <Data id={id} disabled={disabled} />
      </DetailLayout>
    </Page>
  )
}

export default observer(Index)
