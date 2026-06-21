import { Modal, Form } from '@zswl/components'
import { FormAmount } from '@/components/Form'
import { Input, DatePicker } from 'antd'
import { observer } from '@zswl/admin'
import moment from 'moment'
import mathjs from '@/utils/math'
import { hasValue } from '@/utils'

const { Item } = Form

const getInitValues = () => {
  return {
    id: undefined,
    guidePrice: undefined,
    pledgePrice: undefined,
    overduePrice: undefined,
    handAdjustment: undefined,
    assessmentPrice: undefined,
    remark: undefined,
  }
}

const BudgetPricingFtpInterestPriceEditModal = ({ store }) => {
  const [form] = Form.useForm()

  const handleChange = async (value) => {
    const initValues = getInitValues()

    if (value) {
      const recordDate = moment(value).format('YYYY-MM-DD')
      const res = {}
      Object.keys(initValues).map((key) => {
        form.setFieldValue([key], res?.[key] ?? undefined)
      })
    } else {
      form.setFieldsValue(initValues)
    }
  }
  return (
    <Modal store={store.$editModal} title="编辑FTP价格">
      <Form labelCol={{ span: 6 }} form={form}>
        <Item name="id" hidden>
          <Input></Input>
        </Item>
        <Item
          name="recordDate"
          label="日期"
          rules={[{ required: true, message: '请选择' }]}
          transform={(value) => {
            return moment(value).format('YYYY-MM-DD')
          }}
        >
          <DatePicker />
        </Item>
        <FormAmount.Item label="FTP指引价格" name="guidePrice" addonAfter="%" isRequired />
        <FormAmount.Item label="FTP是否质押" name="pledgePrice" addonAfter="%" isRequired />
        <FormAmount.Item label="FTP是否逾期" name="overduePrice" addonAfter="%" isRequired />
        <FormAmount.Item label="FTP手工调整" name="handAdjustment" addonAfter="%" isRequired />
        <Item
          noStyle
          dependencies={['guidePrice', 'pledgePrice', 'overduePrice', 'handAdjustment']}
        >
          {({ getFieldsValue }) => {
            const { guidePrice, pledgePrice, overduePrice, handAdjustment } = getFieldsValue(true)
            const result = mathjs.toNonExponentialPlus(
              mathjs.format(
                mathjs
                  .chain(hasValue(guidePrice) ? +guidePrice : 0)
                  .add(hasValue(pledgePrice) ? +pledgePrice : 0)
                  .add(hasValue(overduePrice) ? +overduePrice : 0)
                  .add(hasValue(handAdjustment) ? handAdjustment : 0)
                  .done()
              )
            )
            store.setAssessmentPrice(result)
            return (
              <div style={{ display: 'flex', marginBottom: 20 }}>
                <div style={{ width: 110, textAlign: 'right', marginRight: 10 }}>FTP考核价格:</div>
                <div>
                  <FormAmount addonAfter="%" disabled value={result} isRequired={false} />
                </div>
              </div>
            )
          }}
        </Item>

        <Item label="FTP备注" name="remark">
          <Input.TextArea></Input.TextArea>
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(BudgetPricingFtpInterestPriceEditModal)
