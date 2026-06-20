import { Modal, Button, ModalStore } from '@zswl/components'
import ApprovalHistory from '../ApprovalHistory/ProcessApprovalHistory'
import BpmnFlowChart from '../BpmnFlowChart'
import { Card } from 'antd'

const ProcessInfoModal = ({ store, processInstanceId }) => {
  return (
    <Modal store={store} width={1200} title="流程信息" footer={null}>
      <Card title="审批历史" size="small">
        <ApprovalHistory processInstanceId={processInstanceId}></ApprovalHistory>
      </Card>
      <div style={{ height: 20 }}></div>
      <Card title="审批流程图" size="small">
        <BpmnFlowChart processInstanceId={processInstanceId} height={400}></BpmnFlowChart>
      </Card>
    </Modal>
  )
}

const Index = ({ processInstanceId, ...rest }) => {
  const modalStore = new ModalStore({})
  return (
    <>
      <Button type="primary" {...rest} onClick={modalStore.open}>
        查看流程
      </Button>
      <ProcessInfoModal store={modalStore} processInstanceId={processInstanceId}></ProcessInfoModal>
    </>
  )
}

export default Index
