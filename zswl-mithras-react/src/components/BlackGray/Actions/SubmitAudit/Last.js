import { Modal, Select, Form } from 'antd'
import { useEffect, useState } from 'react'
import commonAuditActionApi from '@/api/blackGray/commonAuditActionApi'

const { Item } = Form
function BlackGraySubmitAuditLast({ visible, onCancel, getParams, info, onFinish, loading }) {
  const [form] = Form.useForm()
  const [data, setData] = useState({})
  const { lastNodeSelectIds = [], currentActivityId, nodeInfo } = info || {}
  const options = lastNodeSelectIds
    .map((item) => {
      return nodeInfo[item]
    })
    .filter(Boolean)
  const { needSelectNextNode, nextActivityId, lastNodeAuditAccounts } = data
  let nextOptions = []
  if (needSelectNextNode) {
    nextOptions = nodeInfo[nextActivityId]?.selectUsers || []
  }
  const onValuesChange = async (value) => {
    if (value.selectActivityId) {
      const params = getParams()
      const res = await commonAuditActionApi.postLastNodeSelect({
        // 如果是多个就取第一个
        taskIds: params.taskIds,
        currentActivityId,
        selectActivityId: value.selectActivityId,
      })
      form.setFieldsValue({
        auditUser: undefined,
      })
      setData(res)
    }
  }
  useEffect(() => {
    if (visible && options.length) {
      const cur = options[0].activityId
      form.setFieldsValue({
        selectActivityId: cur,
      })
      onValuesChange({ selectActivityId: cur })
    }
  }, [visible])
  return (
    <Modal
      title={'提交审批'}
      destroyOnClose
      open={visible === 2}
      onCancel={onCancel}
      onOk={form.submit}
      okButtonProps={{ loading }}
    >
      <Form form={form} preserve={false} onValuesChange={onValuesChange} onFinish={onFinish}>
        <Item label={'最终审批节点'} name={'selectActivityId'} rules={[{ required: true }]}>
          <Select
            placeholder={'请选择最终审批岗位'}
            options={options}
            fieldNames={{ value: 'activityId', label: 'roleName' }}
          />
        </Item>
        <Item noStyle dependencies={['selectActivityId']}>
          {({ getFieldValue }) => {
            const activityId = getFieldValue('selectActivityId')
            const match = options.find((item) => item.activityId === activityId)
            return (
              <Item style={{ marginLeft: 100 }} label={'所属机构'}>
                {match?.orgName}
              </Item>
            )
          }}
        </Item>
        {needSelectNextNode && (
          <>
            <Item label={'下一位审批人'} name={'auditUser'} rules={[{ required: true }]}>
              <Select
                placeholder={'请选择下一位审批人'}
                options={nextOptions}
                fieldNames={{ label: 'userName', value: 'account' }}
              />
            </Item>
            <div style={{ marginLeft: 100 }}>
              <Item label={'所属机构'}>{nodeInfo[nextActivityId]?.orgName}</Item>
              <Item label={'所属角色'}>{nodeInfo[nextActivityId]?.roleName}</Item>
              {lastNodeAuditAccounts?.length > 0 && (
                <Item label={'最终最终审批人'}>{lastNodeAuditAccounts.join()}</Item>
              )}
            </div>
          </>
        )}
      </Form>
    </Modal>
  )
}
export default BlackGraySubmitAuditLast
