import { Form, Modal } from '@zswl/components'
import { observer } from '@zswl/admin'
import { rules } from '@/utils'
import { DatePicker, Input, Space, Checkbox, Row, Col } from 'antd'
import { FormAmount } from '@/components/Form'
import moment from 'moment'

const { Item } = Form

function disabledDate(current) {
  // 今天之前的日期
  return current < moment().startOf('day')
}

const EditModal = ({ store, IS_PAYMENT }) => {
  return (
    <Modal
      width={600}
      destroyOnClose
      store={store}
      propsBy={(data) => {
        return {
          title: data ? '编辑' : '新增',
        }
      }}
    >
      <Form labelCol={{ span: 6 }} preserve={false}>
        <Item name="billCode" label="票据号" rules={[rules.required()]}>
          <Input></Input>
        </Item>
        <Item label="票据面额" name="billAmount" rules={[rules.required()]}>
          <FormAmount addonAfter="元" />
        </Item>
        <Item
          label="票据到期日"
          name="billExpireDate"
          rules={[rules.required('请选择')]}
          transform={(val) => {
            return moment(val).format('YYYY-MM-DD')
          }}
        >
          <DatePicker disabledDate={disabledDate} />
        </Item>
        {!IS_PAYMENT && (
          <Item label="票据买入价" required>
            <Space>
              <Item noStyle dependencies={['billBuyRateType']}>
                {({ getFieldValue }) => {
                  const value = getFieldValue('billBuyRateType')
                  return (
                    <Item name="billBuyRate" rules={!value && [rules.required()].filter(Boolean)}>
                      <FormAmount addonAfter="%" disabled={value} />
                    </Item>
                  )
                }}
              </Item>

              <Item name="billBuyRateType" valuePropName="checked">
                <Checkbox>同项目FTP</Checkbox>
              </Item>
            </Space>
          </Item>
        )}

        <Item name="id" hidden>
          <Input></Input>
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(EditModal)
