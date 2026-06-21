import PublicInformation from '../PublicInformation/CpmPaymentApplicationPublicInformation'
import { InfoCircleOutlined } from '@ant-design/icons'
import { observer } from '@zswl/admin'
import { Button, Modal } from '@zswl/components'

const CpmPaymentApplicationPublicCheckModal = ({ modal, id, submit, canEdit }) => {
  const { customerList = [] } = modal?.getInitialValues() ?? {}
  return (
    <div>
      <Modal
        title="提示"
        store={modal}
        footer={[
          <PublicInformation
            paymentId={id}
            buttonProps={{ type: 'primary' }}
            canEditFlag={canEdit}
          />,
          <Button
            onClick={async () => {
              await submit()
              modal.close()
            }}
          >
            继续提交
          </Button>,
        ]}
      >
        <div>
          <InfoCircleOutlined style={{ marginRight: 12, color: 'red' }} />
          存在以下交易结构中的客户公开信息尚未维护，是否继续提交流程？
        </div>
        <div style={{ padding: 30 }}>
          <div>客户名称:</div>
          {customerList.map((v) => (
            <div key={v} style={{ color: 'red' }}>
              {v}
            </div>
          ))}
        </div>
      </Modal>
    </div>
  )
}

export default observer(CpmPaymentApplicationPublicCheckModal)
