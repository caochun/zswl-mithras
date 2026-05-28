import { observer } from '@zswl/admin'
import { Drawer, Button } from '@zswl/components'
import Policy from '@/pages/cpm/paymentApplication/detail/Components/Policy'
import { Space } from 'antd'

const Index = ({ store }) => {
  const { handleDrawerConfirm, handleDrawerClose, policyRecord } = store

  return (
    <Drawer
      maskClosable={false}
      store={store.createDrawer}
      width={1000}
      destroyOnClose
      extra={
        <Space>
          <Button onClick={handleDrawerClose}>取消</Button>
          <Button type="primary" onClick={handleDrawerConfirm}>
            确定
          </Button>
        </Space>
      }
      title="保单信息"
      onClose={handleDrawerClose}
    >
      <Policy
        mainId={policyRecord?.paymentId}
        paramsAsPolicy={{ pageSource: 'policy', policyRecord }}
        callBack={store.createDrawer.close}
      />
    </Drawer>
  )
}

export default observer(Index)
