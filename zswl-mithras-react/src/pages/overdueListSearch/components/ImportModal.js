import { Modal, DatePicker, Upload, Form } from '@zswl/components'
import { useState } from 'react'
import styles from '../index.less'

const ImportModal = ({ store }) => {
  const [form] = Form.useForm()

  const uploadProps = {
    accept: '.pdf',
    beforeUpload: (file) => {
      return false // 阻止自动上传
    },
  }

  return (
    <Modal title="逾期名单导入" destroyOnClose width={500} store={store}>
      <Form form={form}>
        <div className={styles.modalContent}>
          <Form.Item
            name="busiDate"
            rules={[{ required: true, message: '请选择日期' }]}
            label="名单截至"
            transform={(val) => ({ busiDate: val && moment(val).format('YYYY-MM-DD') })}
          >
            <DatePicker />
          </Form.Item>

          <div className={styles.tipText}>
            注：如已存在该日期的名单，本次导入会覆盖相同截至日期名单。
          </div>

          <Form.Item name="file" rules={[{ required: true, message: '请上传附件' }]} label="附件">
            <Upload.Dragger {...uploadProps} className={styles.uploader}>
              <div className={styles.uploaderContent}>
                <div className={styles.uploadIcon}>⬆️</div>
                <div>点击或拖动文件到此处上传</div>
                <div className={styles.uploadTip}>支持PDF类型的文件</div>
              </div>
            </Upload.Dragger>
          </Form.Item>
        </div>
      </Form>
    </Modal>
  )
}

export default ImportModal
