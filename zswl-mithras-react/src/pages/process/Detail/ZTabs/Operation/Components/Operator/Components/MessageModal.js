import { observer } from '@zswl/admin'
import { useRef } from 'react'
import { Input, Space } from 'antd'
import { Modal, Button, Form } from '@zswl/components'
import { ProjectFinancialReportStatistics as FinancialReportStatistics } from '@/components/Project/ProjectEntries'

// 风控经理:评审流程提交时检验客户管理模块财务报表录入是否完整
const Index = (props) => {
  const { store, id } = props

  return (
    <Modal title="提示" store={store.messageModal}>
      <Form>
        <Form.Item name={'message'} label="审批意见">
          <Input.TextArea />
        </Form.Item>
      </Form>
    </Modal>
  )
}
Index.methods = {}

export default observer(Index)
