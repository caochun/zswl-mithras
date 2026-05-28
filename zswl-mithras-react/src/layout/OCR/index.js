import { observer } from '@zswl/admin'
import DataUpload from '@/components/DataUpload'
import { Radio, Space, Button, Input, Row, Col, Tooltip } from 'antd'
import { Form, Modal } from '@zswl/components'
import { InboxOutlined } from '@ant-design/icons'
import IconFont from '@/components/Icon'
import store from '../store'
import styles from '../style.less'

const { Item } = Form

const Index = () => {
  const [form] = Form.useForm()
  const { ocrResult, ocrAgain } = store

  return (
    <Modal
      store={store.createOcr}
      title={'OCR识别'}
      okText={'确定'}
      width={480}
      footer={ocrResult ? <></> : undefined}
      onCancel={store.handleCancel}
      onOk={store.handleOk}
      destroyOnClose
    >
      <div>
        {!ocrResult ? (
          <Form
            form={form}
            labelCol={{ span: 6 }}
            preserve={false}
            name="dynamic_form_item"
            initialValues={{
              type: 0,
              pdfIndex: [''],
            }}
          >
            <Item
              label={'识别文件类型'}
              name={'type'}
              rules={[{ required: true, message: '请选择文件类型' }]}
            >
              <Radio.Group>
                <Radio value={0}>图片</Radio>
                <Radio value={1}>PDF</Radio>
              </Radio.Group>
            </Item>
            <Item dependencies={['type']}>
              {({ getFieldValue }) => {
                const type = getFieldValue('type')
                const typeName = ['图片', 'PDF'][type]
                return (
                  <>
                    <Item
                      label={''}
                      //   wrapperCol={{ offset: 6 }}
                      name={type === 0 ? 'files' : 'file'}
                      rules={[{ required: true, message: '请上传' }]}
                    >
                      <DataUpload
                        key={type}
                        accept={type === 0 ? 'image/png, image/jpeg,image/gif' : '.pdf'}
                        maxCount={type === 0 ? 1000 : 1}
                      >
                        <div className={styles.ocrUpload}>
                          <p className="ant-upload-drag-icon">
                            <InboxOutlined style={{ fontSize: 64, color: '#BCBDC0' }} />
                          </p>
                          <p className="ant-upload-text">
                            点击或将<span className={styles.typeName}>{typeName}</span>
                            拖拽到这里上传
                          </p>
                        </div>
                      </DataUpload>
                    </Item>
                    {type === 1 && (
                      <Form.List name={'pdfIndex'}>
                        {(fields, { add, remove }) => {
                          return fields.map((filed, index) => {
                            return (
                              <Item
                                label={
                                  index === 0 ? (
                                    <>
                                      页码范围
                                      <Tooltip title="每一行页码输入,如'1'，'1-2'">
                                        <IconFont
                                          style={{ color: '#2558e6' }}
                                          type="icon-icon_info_filled"
                                        />
                                      </Tooltip>
                                    </>
                                  ) : (
                                    ''
                                  )
                                }
                                // wrapperCol={{ offset: index == 0 ? 0 : 5 }}
                                style={index === 0 ? {} : { paddingLeft: 95 }}
                                name={filed.name}
                                key={index}
                                required
                                rules={[{ required: true, message: '请输入页码范围' }]}
                              >
                                <Row gutter={10} align={'middle'}>
                                  <Col span={16}>
                                    <Input></Input>
                                  </Col>
                                  <Col span={8}>
                                    <Space align={'baseline'}>
                                      {index === 0 && (
                                        <a onClick={add}>
                                          <IconFont type="icon-icon_add" />
                                          添加页码
                                        </a>
                                      )}
                                      {index > 0 && (
                                        <a onClick={() => remove(filed.name)}>
                                          <IconFont type="icon-icon_delete" />
                                        </a>
                                      )}
                                    </Space>
                                  </Col>
                                </Row>
                              </Item>
                            )
                          })
                        }}
                      </Form.List>
                    )}
                  </>
                )
              }}
            </Item>
          </Form>
        ) : (
          <div className={styles.ocrBtn}>
            <Space>
              <Button
                type="primary"
                onClick={() => {
                  return store.ocrDown()
                }}
              >
                下载
              </Button>
              <Button type="primary" onClick={store.ocrPreview}>
                预览
              </Button>
              <Button onClick={ocrAgain}>再次识别</Button>
            </Space>
          </div>
        )}
      </div>
    </Modal>
  )
}

export default observer(Index)
