import customerRatApi from '@/api/customer/customerRat/customerRatApi'
import DataUpload from '@/components/DataUpload'
import { observer } from '@zswl/admin'
import { Button, Form, Input, Modal, ModalStore, Select } from '@zswl/components'
import { useMemo } from 'react'
import uploadFile from '../RatingUpload/uploadFile'
const CustomerRatDetailOverturnModal = ({ uploadParams, afterSubmit }) => {
  const modal = useMemo(
    () =>
      new ModalStore({
        onFinish: async (values) => {
          await customerRatApi.postClientAdjust({
            onlyCheck: true,
            id: uploadParams.mainId,
            ...values,
          })
          await customerRatApi.postClientAdjust({ id: uploadParams.mainId, ...values })
          await uploadFile(values.files, uploadParams)
          await afterSubmit?.(values)
          modal.close()
        },
      }),
    []
  )
  const handleOverturn = () => {
    modal.open()
  }
  return (
    <>
      <Button type="primary" onClick={handleOverturn}>
        评级调整
      </Button>
      <Modal title="评级调整" store={modal} okText={'确认提交'}>
        <Form>
          <Form.Item label="评级调整说明" name={'adjustOpinion'} required>
            <Input.TextArea rows={3} />
          </Form.Item>
          <Form.Item
            label="补充说明资料"
            name="files"
            rules={[{ required: true, message: '请上传文件' }]}
          >
            <DataUpload options={'ratingLevelEnum'}></DataUpload>
          </Form.Item>
          <Form.Item label="审查结果" name="finalScore" required>
            <Select options={'ratingLevelEnum'}></Select>
          </Form.Item>
        </Form>
      </Modal>
    </>
  )
}

export default observer(CustomerRatDetailOverturnModal)
