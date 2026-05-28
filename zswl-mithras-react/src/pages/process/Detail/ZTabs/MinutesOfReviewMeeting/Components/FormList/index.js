import { Col, Form, Input, Row, Space } from 'antd'
import IconFont from '@/components/Icon'

const FormList = ({ name, showValue }) => {
  return (
    <Form.List name={name}>
      {(fields, { add, remove }) => (
        <>
          {fields.map((field, index) => (
            <Row gutter={10} align={'middle'} key={field.key} style={{ marginBottom: 10 }}>
              <Col span={16}>
                <Form.Item name={field.name}>
                  <Input placeholder="请输入" maxLength={2500} />
                </Form.Item>
              </Col>
              <Col span={8}>
                <Space align={'baseline'}>
                  {index === 0 && (
                    <IconFont
                      type="icon-icon_add"
                      onClick={() => add('')}
                      style={{ color: '#0984e3' }}
                    />
                  )}
                  {fields.length > 1 && (
                    <IconFont
                      type="icon-icon_delete"
                      onClick={() => remove(field.name)}
                      style={{ color: '#0984e3' }}
                    />
                  )}
                </Space>
              </Col>
            </Row>
          ))}
        </>
      )}
    </Form.List>
  )
}

export default FormList
