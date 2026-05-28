import { Drawer, Page } from '@zswl/components'
import { Tabs } from 'antd'
import { getQuery } from '@zswl/admin'
import BaseInfo from './BaseInfo'
import Deduction from './Deduction'
import OverdueCollection from './OverdueCollection'
import OverdueSummary from './OverdueSummary'
import { useEffect, useMemo } from 'react'
import Store from './store'
const ProjectDetail = ({
  contractId,
  isProcess,
  baseStore,
  paymentCode,
  query: { canEditFlags = 'true', businessVersion, reduceId },
}) => {
  const isFormApproval = getQuery('typeId') == 'approval'
  const store = useMemo(() => {
    return new Store({ businessVersion, reduceId, isFormApproval, contractId, isProcess })
  }, [businessVersion, reduceId, isFormApproval, contractId, isProcess])
  const canEditFlagsFormAuth = canEditFlags === 'true'

  const Content = (
    <Tabs defaultActiveKey="1" destroyInactiveTabPane>
      <Tabs.TabPane tab="合同基本信息" key="1">
        <BaseInfo contractId={contractId} businessVersion={businessVersion} />
      </Tabs.TabPane>
      <Tabs.TabPane tab="回款情况汇总" key="2">
        <OverdueSummary store={store} businessVersion={businessVersion} baseStore={baseStore} />
      </Tabs.TabPane>
      <Tabs.TabPane tab="逾期催收" key="3">
        <OverdueCollection
          canEdit={canEditFlagsFormAuth}
          store={store}
          businessVersion={businessVersion}
        />
      </Tabs.TabPane>
      <Tabs.TabPane tab="罚息减免" key="4">
        <Deduction store={store} businessVersion={businessVersion} />
      </Tabs.TabPane>
    </Tabs>
  )

  useEffect(() => {
    store.init({
      contractId,
      isProcess,
      businessVersion,
      paymentCode,
    })
  }, [contractId, isProcess, businessVersion, paymentCode])
  return (
    <div>
      {isProcess ? (
        Content
      ) : (
        <Drawer
          destroyOnClose
          title={'借据卡'}
          width={1000}
          placement="right"
          extra={null}
          store={baseStore.$projectDetailDrawer}
        >
          {Content}
        </Drawer>
      )}
    </div>
  )
}

export default ProjectDetail
