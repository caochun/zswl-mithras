import { Button, Modal, Form, Select } from '@zswl/components'
import { SelectOutlined } from '@ant-design/icons'
import { useState } from 'react'
import { history } from '@zswl/admin'
import { message } from 'antd'
import commonAuditActionApi from '@/api/blackGray/commonAuditActionApi'

/**
 * @param {integer} taskId 审批流任务id，针对已经有审批流的情况
 * @param {function} props.onSuccess 用于提交成功后列表刷新
 * @returns
 */

function BlackGrayTransmitAction({ taskId, onSuccess, ...rest }) {
  const [transmitForm] = Form.useForm()
  const [list, setList] = useState([])
  const modalStore = Modal.useStore({
    onOpen: () => {
      getDetails()
      getSelectList()
    },
  })

  //转派给
  const getSelectList = async () => {
    const res = await commonAuditActionApi.getCurrentNodeAuditUsers({ taskId })
    setList(res)
  }

  //审批节点,审批角色
  const getDetails = async () => {
    const { nodeName, roleName } = await commonAuditActionApi.getCurrentNodeInfo({ taskId })
    transmitForm.setFieldValue('tagName', nodeName)
    transmitForm.setFieldValue('roleName', roleName)
  }

  const onSubmit = async () => {
    const { newDealUser, suggest } = transmitForm.getFieldsValue()
    if (!!newDealUser) {
      const data = {
        taskId,
        newDealUser, //转派后新处理人
        suggest, // 转派备注
      }
      await commonAuditActionApi.postTransfer(data)
      message.success('转派成功')
      modalStore.close()
      onSuccess?.()
      history.goBack()
    } else {
      message.info('请选择转派人！')
    }
  }

  return (
    <>
      <Button icon={<SelectOutlined />} onClick={modalStore.open} {...rest}>
        转派
      </Button>
      <Modal store={modalStore} title="转派" onOk={onSubmit} destroyOnClose>
        <Form
          form={transmitForm}
          labelCol={{ span: 4 }}
          items={[
            {
              title: '审批节点',
              dataIndex: 'tagName',
              element: {
                disabled: true,
              },
            },
            {
              title: '审批角色',
              dataIndex: 'roleName',
              element: {
                disabled: true,
              },
            },
            {
              title: '转派给',
              dataIndex: 'newDealUser',
              element: (
                <Select
                  style={{ width: '100%' }}
                  options={list}
                  fieldNames={{
                    label: 'userName',
                    value: 'account',
                  }}
                />
              ),
              rules: [{ required: true, message: '请选择用户' }],
            },
            { title: '备注', dataIndex: 'suggest', element: 'textArea' },
          ]}
        ></Form>
      </Modal>
    </>
  )
}

export default BlackGrayTransmitAction
