import { getQuery, observer } from '@zswl/admin'
import { Modal, Form, Select, Button } from '@zswl/components'
import { Input, message } from 'antd'
import { NoEnumFileTable } from '@/components/Table'
import sealForDocumentsApi from '@/api/overdue/sealForDocumentsApi'
import { useState } from 'react'

const { Item } = Form
const moduleType = 'DOC_PRINTING'
const Index = ({ modal, modalProps = {}, canEdit = true }) => {
  const [form] = Form.useForm()
  const [newId, setNewId] = useState(undefined)
  const initialValues = modal.getInitialValues() ?? {}
  const { isArchive, disabled, id } = initialValues
  const canEditItem = canEdit && !disabled
  const param = {
    mainId: id ?? newId,
    moduleType,
  }
  const isFormApproval = getQuery('typeId') == 'approval'
  const save = async (needMessage = true) => {
    const formStore = modal.getFormStore()
    await formStore.validateFields()
    const params = formStore.getFieldsFormatValue()
    const isAdd = !params.id
    if (isAdd) {
      const id = await sealForDocumentsApi.postPrintingAdd(params)
      formStore.setFieldsValue({ id })
      setNewId(id)
    } else {
      await sealForDocumentsApi.postPrintingSave(params)
    }
    if (needMessage) {
      message.success('保存成功')
    }
  }
  return (
    <Modal
      title={'文书用印'}
      width={800}
      store={modal}
      destroyOnClose
      okText="提交"
      footer={
        isArchive || !canEditItem
          ? null
          : [
              // !isFormApproval && <Button onClick={modal.close}>取消</Button>,
              canEditItem && (
                <Button type="primary" onClick={save}>
                  保存
                </Button>
              ),
              !isFormApproval && (
                <Button
                  type="primary"
                  onClick={async () => {
                    await save(false)
                    await modal.submit()
                  }}
                >
                  提交
                </Button>
              ),
            ]
      }
      {...modalProps}
    >
      <Form labelCol={{ span: 4 }} initialValues={initialValues}>
        <Item name="id" hidden>
          <Input disabled />
        </Item>
        <Item label="申请人" name="applyName" hidden={!isFormApproval}>
          <Input disabled />
        </Item>
        <Item label="申请部门" name="applyDeptName" hidden={!isFormApproval}>
          <Input disabled />
        </Item>
        <Item name={'id'} hidden />
        <Item
          label="用印类型"
          name="type"
          required
          hidden={isArchive}
          rules={[{ required: true, message: '请选择用印类型' }]}
        >
          <Select options={'printingType'} disabled={!canEditItem} />
        </Item>
        <Item label="用印原因" name="reason" hidden={isArchive}>
          <Input.TextArea rows={3} disabled={!canEditItem} />
        </Item>

        <NoEnumFileTable
          title={<div className="z-title">文书附件</div>}
          canEdit={canEditItem}
          needApproval={false}
          params={param}
          canEditItem={false}
          columns={[{ title: '文书名称', dataIndex: 'name' }]}
        />
      </Form>
    </Modal>
  )
}

export default observer(Index)
