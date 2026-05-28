import { observer } from '@zswl/admin'
import { Form, Modal, Select } from '@zswl/components'
import { Input, InputNumber } from 'antd'
import Amount from '@/components/Amount'
const { Item } = Form
const selectData = [
  { label: '是', value: true },
  { label: '否', value: false },
]
//新增联系人弹窗
function Index({ store }) {
  return (
    <Modal
      title={'股东信息'}
      store={store.shareholdersModal}
      okText={'确定'}
      width={480}
      destroyOnClose
    >
      <Form labelCol={{ span: 8 }} wrapperCol={{ span: 16 }} preserve={false}>
        <Item
          label={'股东类型'}
          name={'shareholderType'}
          rules={[
            {
              required: true,
            },
          ]}
        >
          <Select options="shareholderType" />
        </Item>
        <Item
          label={'股东名称'}
          name={'shareholderName'}
          rules={[
            {
              required: true,
            },
          ]}
        >
          <Input placeholder={'请输入'} />
        </Item>
        <Item label={'认缴金额(万元)'} name={'paidTotal'}>
          <Amount>
            <InputNumber style={{ width: '100%' }} />
          </Amount>
        </Item>
        <Item label={'实缴金额(万元)'} name={'actualPaidTotal'}>
          <Amount>
            <InputNumber style={{ width: '100%' }} />
          </Amount>
        </Item>
        <Item label={'认缴出资方式'} name={'capitalWay'}>
          <Input placeholder={'请输入'} />
        </Item>
        <Item
          label={'认缴出资占比(%)'}
          name={'capitalPercent'}
          rules={[
            {
              required: true,
            },
          ]}
        >
          <Amount>
            <InputNumber style={{ width: '100%' }} />
          </Amount>
        </Item>
        <Item label={'是否实际控制人'} name={'realController'}>
          <Select options={selectData} />
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(Index)
