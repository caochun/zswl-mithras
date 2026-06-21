import { Modal, Button } from '@zswl/components'
import { observer } from '@zswl/admin'
import { Space } from 'antd'

const BusinessInfoCheckTipsConfirm = ({ store }) => {
  return (
    <Modal
      store={store.tipsConfirmModal}
      title="工商信息校验"
      width={400}
      content={33344}
      footer={
        <Space>
          <Button
            type="primary"
            onClick={() => {
              store.tipsConfirmModal.close()
              store.compareModal.open()
            }}
          >
            查看详情
          </Button>
          <Button onClick={store.tipsConfirmModal.close}>取消</Button>
          {store.submitFn && (
            <Button type="primary" onClick={store.submitFn}>
              继续提交
            </Button>
          )}
        </Space>
      }
    >
      <span style={{ color: 'red' }}>{store.tipsConfirmContent}</span>
    </Modal>
  )
}

export default observer(BusinessInfoCheckTipsConfirm)
