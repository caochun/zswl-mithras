import { Input, Typography, Radio, Button, Upload } from 'antd'
import styles from './style.less'
import { observer, history } from '@zswl/admin'
import { Form } from '@zswl/components'
import { UploadOutlined } from '@ant-design/icons'
const TextArea = Input.TextArea
const rules = [{ required: true }]
const FeedbackSection = ({ store }) => {
  const haderContentView = [
    {
      title: '是否处置',
      name: 'isDisposed',
      rules,
      element: (
        <Radio.Group>
          <Radio value="1">处置</Radio>
          <Radio value="0">关闭</Radio>
        </Radio.Group>
      ),
    },

    {
      title: '风险情况说明',
      name: 'riskDescription',
      rules,
      element: <TextArea rows={2} maxLength={500} />,
    },
    {
      title: '附件',
      name: 'attachments',
      element: (
        <>
          <Upload maxCount={1} accept=".pdf,.doc,.docx" beforeUpload={() => false}>
            <Button icon={<UploadOutlined />}>添加文件</Button>
          </Upload>
          <Typography.Text type="secondary" className={styles.uploadHint}>
            支持文件类型为 pdf/doc/docx，单个文件不超过 15M。
          </Typography.Text>
        </>
      ),
    },
    { name: 'id', hidden: true },
  ]
  return (
    <div className={styles.feedbackBox}>
      <div className={styles.haders}>
        <Typography.Text
          style={{
            fontSize: '16px',
            fontWeight: 'bold',
          }}
        >
          处置反馈详情
        </Typography.Text>
      </div>
      <div className={styles.formContent}>
        <Form
          column={1}
          layout="horizontal"
          labelCol={{ span: 2 }}
          className={styles.feedbackForm}
          store={store.form}
          items={haderContentView}
        >
          {/* 操作按钮 */}
          <div className={styles.feedbackButtons}>
            <Button
              onClick={() => {
                history.goBack()
              }}
              type="default"
              className={styles.cancelButton}
            >
              取消
            </Button>
            <Button
              onClick={() => {
                history.goBack()
                store.DetailSubmit()
              }}
              type="primary"
              htmlType="submit"
              className={styles.submitButton}
            >
              提交
            </Button>
          </div>
        </Form>
      </div>
    </div>
  )
}

export default observer(FeedbackSection)
