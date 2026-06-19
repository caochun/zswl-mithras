import { observer } from '@zswl/admin'
import { Form, Modal, Select } from '@zswl/components'
import store from './store'
import { Input, DatePicker, Radio, Space, Button } from 'antd'
import { OrgRole } from './components'
import OrgJob from './components/OrgJob'

const { Item } = Form
function Index() {
  const { orgList, editModal } = store
  const initValues = editModal.getInitialValues()
  return (
    <Modal
      propsBy={(data) => {
        return { title: `${data ? '编辑' : '创建'}用户` }
      }}
      store={store.editModal}
      okText={'确定'}
      width={800}
      destroyOnClose
    >
      {(data) => {
        return (
          <Form labelCol={{ span: 4 }} preserve={false} initialValues={{ gender: 1 }}>
            <Item label={'登录账号'} name={'account'} rules={[{ required: true }]}>
              <Input disabled={!!data} />
            </Item>
            <Item label={'用户名'} name={'userName'} rules={[{ required: true }]}>
              <Input />
            </Item>
            {initValues && (
              <Item label={'用户主数据编码'} name={'mainCode'}>
                <Input
                  suffix={
                    <Button onClick={() => store.dataSync()} type="primary" size="small">
                      同步
                    </Button>
                  }
                />
              </Item>
            )}

            <Form.List name={'orgJobs'}>
              {(fields, { add, remove }) => {
                const render = fields.map((filed, index) => {
                  return (
                    <Item
                      label={'机构/岗位'}
                      name={filed.name}
                      key={filed.name}
                      required
                      rules={[
                        {
                          validator(r, v) {
                            if (v?.jobs?.length && v?.org) {
                              return Promise.resolve()
                            }
                            return Promise.reject('请完整选择机构/岗位')
                          },
                        },
                      ]}
                    >
                      <OrgJob
                        orgList={orgList}
                        extra={
                          <Space align={'baseline'}>
                            <a onClick={add}>添加</a>
                            {<a onClick={() => remove(filed.name)}>删除</a>}
                          </Space>
                        }
                      />
                    </Item>
                  )
                })

                return (
                  <div>
                    {render}
                    {fields.length === 0 && (
                      <Item label="机构/岗位" required>
                        <Button type="link" onClick={add}>
                          新增
                        </Button>
                      </Item>
                    )}
                  </div>
                )
              }}
            </Form.List>
            <Form.List name={'orgRoles'}>
              {(fields, { add, remove }) => {
                const render = fields.map((filed, index) => {
                  return (
                    <Item
                      label={'机构/角色'}
                      name={filed.name}
                      key={filed.name}
                      // key={index}
                      required
                      rules={[
                        {
                          validator(r, v) {
                            if (v?.roles?.length && v?.org) {
                              return Promise.resolve()
                            }
                            return Promise.reject('请完整选择机构/角色')
                          },
                        },
                      ]}
                    >
                      <OrgRole
                        orgList={orgList}
                        extra={
                          <Space align={'baseline'}>
                            <a onClick={add}>添加</a>
                            {<a onClick={() => remove(filed.name)}>删除</a>}
                          </Space>
                        }
                      />
                    </Item>
                  )
                })
                return (
                  <div>
                    {render}
                    {fields.length === 0 && (
                      <Item label="机构/角色" required>
                        <Button type="link" onClick={add}>
                          新增
                        </Button>
                      </Item>
                    )}
                  </div>
                )
              }}
            </Form.List>
            <Item label={'过期时间'} name={'expiration'} rules={[{ required: true }]}>
              <DatePicker style={{ width: 220 }} />
            </Item>
            <Item label={'性别'} name={'gender'}>
              <Radio.Group>
                <Radio value={0}>保密</Radio>
                <Radio value={1}>男</Radio>
                <Radio value={2}>女</Radio>
              </Radio.Group>
            </Item>
            <Item label={'手机号'} name={'phone'} rules={[{ required: true }]}>
              <Input />
            </Item>
            <Item label={'邮箱'} name={'email'} rules={[{ required: true }, { type: 'email', message: '邮箱格式不正确' }]}>
              <Input />
            </Item>
          </Form>
        )
      }}
    </Modal>
  )
}

export default observer(Index)
