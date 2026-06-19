import { observer } from '@zswl/admin'
import { Form, Modal, Select } from '@zswl/components'
import { Input } from 'antd'
import { rules } from '@/utils'

const { Item } = Form
function Index({ store }) {
  const codeComponent = ({ getFieldValue }) => {
    const domesticOrAbroad = getFieldValue('domesticOrAbroad')
    return domesticOrAbroad === 'DOMESTIC' ? (
      <Item
        label={'统一社会信用代码'}
        name={'uscCode'}
        rules={[
          { required: true },
          { pattern: /^[a-zA-Z0-9]{1,20}$/, message: '请输入正确的统一社会信用代码' },
        ]}
      >
        <Input placeholder={'请输入'} maxLength={18} autoComplete="off" />
      </Item>
    ) : (
      <Item label={'特殊机构代码'} name={'specialOrgCode'}>
        <Input placeholder={'请输入'} maxLength={18} autoComplete="off" />
      </Item>
    )
  }
  return (
    <Modal title={'客户创建'} store={store.createModal} okText={'确定'} width={600} destroyOnClose>
      <Form initialValues={{ clientType: 'CORPORATION' }} labelCol={{ span: 6 }} preserve={false}>
        <Item label={'客户名称'} name={'clientName'} rules={[{ required: true }]}>
          <Input placeholder={'请输入'} autoComplete="off" />
        </Item>
        <Item label={'客户分类'} name={'clientType'} rules={[{ required: true }]}>
          <Select options={'clientType'} />
        </Item>
        <Item noStyle dependencies={['clientType']}>
          {({ getFieldValue }) => {
            const clientType = getFieldValue('clientType')
            return clientType === 'NORMAL' ? (
              <Item noStyle>
                <Item label={'证件类型'} name={'certType'} rules={[{ required: true }]}>
                  <Select options={'certType'} />
                </Item>
                <Item
                  label={'证件号码'}
                  name={'certNumber'}
                  rules={[
                    { required: true },
                    { pattern: /^[a-zA-Z0-9]{1,20}$/, message: '请输入正确的证件号码' },
                  ]}
                >
                  <Input placeholder={'请输入'} />
                </Item>
              </Item>
            ) : (
              <>
                <Item
                  label={'机构类型'}
                  name={'domesticOrAbroad'}
                  rules={[{ required: true }]}
                  initialValue={'DOMESTIC'}
                >
                  <Select options={'domesticOrAbroad'} />
                </Item>
                <Item noStyle dependencies={['domesticOrAbroad']}>
                  {codeComponent}
                </Item>
              </>
            )
          }}
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(Index)
