import { observer } from '@zswl/admin'
import { Form, Modal, Select } from '@zswl/components'
import { Input } from 'antd'
import { rules } from '@/utils'

const { Item } = Form
const selectData = [
  { label: '是', value: true },
  { label: '否', value: false },
]
//新增联系人弹窗
function Index({ store }) {
  return (
    <Modal
      title={'联系人信息'}
      store={store.contactModal}
      okText={'确定'}
      width={480}
      destroyOnClose
    >
      <Form labelCol={{ span: 9 }} wrapperCol={{ span: 14 }} preserve={false}>
        <Item label={'是否主联系人'} name={'main'}>
          <Select options={selectData} />
        </Item>
        <Item label={'职务'} name={'position'} rules={[rules.required()]}>
          <Input placeholder={'请输入'} />
        </Item>
        <Item label={'姓名'} name={'name'} rules={[rules.required()]}>
          <Input placeholder={'请输入'} />
        </Item>
        <Item label={'性别'} name={'gender'}>
          <Select options="genderType" />
        </Item>
        <Item label={'电话'} name={'telephone'} rules={[rules.required(), rules.phoneCode()]}>
          <Input placeholder={'请输入'} />
        </Item>
        <Item label={'座机'} name={'landlineTelephone'}>
          <Input placeholder={'请输入'} />
        </Item>
        <Item label={'邮箱(用于发票传递)'} name={'mail'} rules={[rules.required()]}>
          <Input placeholder={'请输入'} />
        </Item>
        <Item label={'证件类型'} name={'certType'}>
          <Select options="certType" />
        </Item>
        <Item label={'证件号码'} name={'certNumber'}>
          <Input placeholder={'请输入'} />
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(Index)
