// 反馈数据
import React from 'react'
import { Card, Form, Input, Button } from 'antd'
import styles from './styles.less'

const Feedback = () => {
  const [form] = Form.useForm()

  const onFinish = (values) => {
    console.log('Feedback submitted:', values)
  }

  return (
    <div className={styles['feedback-container']}>
      <Card title="反馈信息">
        <Form form={form} layout="vertical" onFinish={onFinish}>
          <Form.Item
            name="content"
            label="反馈内容"
            rules={[{ required: true, message: '请输入反馈内容' }]}
          >
            <Input.TextArea rows={4} />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit">
              提交反馈
            </Button>
          </Form.Item>
        </Form>
      </Card>
    </div>
  )
}

export default Feedback
