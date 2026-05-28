import { observer } from '@zswl/admin'
import { Page, Button, Form, Select } from '@zswl/components'
import store from './store'
import { Space, Input } from 'antd'

const { Item } = Form
function Index({ params }) {
  const data = store.page.getData()
  return (
    <Page current={params.id ? '编辑' : '新增'} params={params} store={store}>
      <Form store={store.form} labelCol={{ span: 4 }} initialValues={data}>
        <Item label={'姓名'} name={'name'} rules={[{ required: true }]}>
          <Input />
        </Item>
        <Item label={'年龄'} name={'age'} rules={[{ required: true }]}>
          <Input />
        </Item>
        <Item label={'状态'} name={'status'} rules={[{ required: true }]}>
          <Select options={'status'} />
        </Item>
      </Form>
      <Space style={{ marginTop: 20 }}>
        <Button type={'primary'} onClick={store.submit}>
          提交
        </Button>
        <Button goBack>返回</Button>
      </Space>
    </Page>
  )
}

export default observer(Index)
