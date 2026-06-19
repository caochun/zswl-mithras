import { observer } from '@zswl/admin'
import { useRef, forwardRef, useImperativeHandle, useState, useMemo } from 'react'
import { Modal, Button, Form, Select } from '@zswl/components'
import { Space, message } from 'antd'
import leaseData from './leaseData.json'
import { DynamicDesc } from '@/components/Table'
import _ from 'lodash'
import { rules } from '@/utils'
import flowList from '@/api/process/flowTaskApi'

const Index = (props, ref) => {
  const [form] = Form.useForm()
  const descRef = useRef()
  const [nodes, setNodes] = useState([])

  const { submit, detail = {} } = props
  const { taskActivityId, modelKey } = detail

  // 租赁物变更、创建 的运营经理 岗位需要特殊的表单
  const lease_operationManagement =
    ['LeaseModifyFlow', 'LeaseCreateFlow'].includes(modelKey) &&
    taskActivityId === 'operationManagement'

  const modal = Modal.useStore({
    onOpen: async ({ processInstanceId, taskId }) => {
      const res = await flowList.getReturnableNodes({ processInstanceId, taskId })
      setNodes(res)
      return {}
    },
    onFinish: async () => {
      modal.close()
    },
  })

  const formEditContent = useMemo(() => {
    if (lease_operationManagement) {
      return (
        <DynamicDesc
          ref={descRef}
          title={' '}
          contentData={leaseData}
          canEdit={false}
          initEdit={true}
        />
      )
    }
  }, [modelKey, taskActivityId])

  const saveData = async () => {
    let groupContentStr = []
    if (lease_operationManagement) {
      const data = await descRef.current?.getData()
      const validContentArr = data?.filter((item) => !!item.content)
      const groupContentArr = _.groupBy(validContentArr, 'templateTitle')
      if (validContentArr?.length === 0) {
        message.info('至少选择一种退回原因')
        return
      }
      Object.keys(groupContentArr).map((key) => {
        let contentStr = `<div>${key}</div>`
        groupContentArr[key].map((item, index) => {
          contentStr += `<div>&nbsp;&nbsp;${index + 1}. ${item.templateContentInputLabel}${
            item.templateContentInputLabel ? `：` : ''
          }${item.content}</div>`
        })
        groupContentStr.push(contentStr)
      })
    }
    const formData = await modal.submit()
    submit?.({ groupContentStr, ...formData })
  }

  useImperativeHandle(ref, () => ({
    modal,
  }))

  return (
    <Modal
      width={800}
      store={modal}
      title="选择填写节点"
      getContainer={() => document.body}
      footer={
        <Space>
          <Button onClick={modal.close}>取消</Button>
          <Button type="primary" onClick={saveData}>
            提交
          </Button>
        </Space>
      }
    >
      <Form labelCol={{ span: 6 }} form={form}>
        {formEditContent}
        <Form.Item label="退回至哪个节点" name="activityId" rules={[rules.required('请选择')]}>
          <Select
            options={nodes}
            fieldNames={{
              label: 'taskNodeName',
              value: 'taskActivityId',
            }}
          />
        </Form.Item>
        <Form.Item label="退回类型" name="backType" rules={[rules.required('请选择')]}>
          <Select
            options={[
              {
                label: '逐级审批',
                value: 1,
              },
              {
                label: '直达本节点',
                value: 2,
              },
            ]}
          />
        </Form.Item>
      </Form>
    </Modal>
  )
}

export default observer(forwardRef(Index))
