import { useEffect, useState } from 'react'
import { observer, toJS } from '@zswl/admin'
import { Form, Modal, Select } from '@zswl/components'
import { Input, Radio, Cascader } from 'antd'
const { TextArea } = Input
const { Item } = Form
//新增地址
function CustomerAddressModal({ store }) {
  const { regionList, countryID } = store
  const [form] = Form.useForm()
  const [flag, setFlag] = useState(false)
  useEffect(() => {
    store.initRegionList()
  }, [])
  useEffect(() => {
    // 中国省市区必填
    if (countryID != '156') {
      setFlag(true)
    } else {
      setFlag(false)
    }
  }, [countryID])
  const countryListChange = (e) => {
    if (e != 156) {
      form.setFieldsValue({ area: '' })
      form.setFieldsValue({ regionCode: '' })
      setFlag(true)
    } else {
      // form.setFieldsValue({ regionCode: e })
      setFlag(false)
    }
  }
  const areaChange = (e) => {
    form.setFieldsValue({ regionCode: e[e.length - 1] })
  }
  return (
    <Modal title={'地址信息'} store={store.addressModal} okText={'确定'} width={480} destroyOnClose>
      <Form labelCol={{ span: 8 }} wrapperCol={{ span: 16 }} preserve={false} form={form}>
        <Item
          label={'地址类型'}
          name={'addressType'}
          rules={[
            {
              required: true,
              message: '请选择地址类型',
            },
          ]}
        >
          <Radio.Group>
            <Radio value={'REGISTRY_ADDRESS'}>注册地址</Radio>
            <Radio value={'WORK_ADDRESS'}>办公地址</Radio>
          </Radio.Group>
        </Item>
        <Item
          label={'国家/地区'}
          name={'country'}
          rules={[
            {
              required: true,
              message: '请选择国家/地区',
            },
          ]}
        >
          <Select options="countryList" onChange={countryListChange} />
        </Item>
        <Item
          label={'省/市/区县'}
          name={'area'}
          // allowClear={false}
          rules={[
            {
              required: !flag,
            },
          ]}
        >
          <Cascader
            onChange={areaChange}
            placeholder="请选择"
            options={regionList}
            loadData={store.loadRegionListChild}
            disabled={flag}
          />
        </Item>
        <Item
          label={'详细地址'}
          name={'detail'}
          rules={[
            {
              required: !flag,
            },
          ]}
        >
          <TextArea rows={4} />
        </Item>
        <Item
          label={'行政区划代码'}
          name={'regionCode'}
          rules={[
            {
              required: !flag,
            },
          ]}
        >
          <Input placeholder={'请输入'} />
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(CustomerAddressModal)
