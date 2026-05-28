import { useEffect } from 'react'
import { observer } from '@zswl/admin'
import { Form, Modal, Select } from '@zswl/components'
import { Input } from 'antd'

const { Item } = Form

//新增地址
function Index({ id, store }) {
  const { spouseList, spouseData } = store
  const [form] = Form.useForm()
  useEffect(() => {
    if (Object.keys(spouseData).length > 0) {
      form.setFieldsValue({
        certType: spouseData.certType,
        certNumber: spouseData.certNumber,
      })
    }
  }, [spouseData])
  //配偶模糊查询
  return (
    <Modal title={'配偶信息'} store={store.spouseModal} okText={'确定'} width={480} destroyOnClose>
      <Form labelCol={{ span: 8 }} wrapperCol={{ span: 16 }} preserve={false} form={form}>
        <Item label={'配偶姓名'} name={'spouseName'}>
          <Select
            options={spouseList}
            onSearch={(e) => store.spouseSelect(e)}
            onSelect={(e) => store.changeSpouse(e)}
          />
        </Item>
        <Item label={'证件类型'} name={'certType'}>
          <Select options="certType" disabled />
        </Item>
        <Item label={'证件号码'} name={'certNumber'}>
          <Input placeholder={'请输入'} disabled />
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(Index)
