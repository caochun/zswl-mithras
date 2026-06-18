import { Form, Modal } from '@zswl/components'
import { rules } from '@/utils'
import { DatePicker } from 'antd'
import FormAmount from '@/components/Form/FormAmount'
import moment from 'moment'

const FinancingCostEditModal = ({ store }) => {
  return (
    <Modal
      destroyOnClose
      store={store}
      propsBy={(data) => {
        return {
          title: data ? '编辑融资成本' : '创建融资成本',
        }
      }}
    >
      <Form labelCol={{ span: 8 }} preserve={false}>
        <Form.Item
          label="时间"
          name="month"
          rules={[rules.required('请选择')]}
          transform={(val) => {
            return moment(val).format('YYYY-MM-01')
          }}
        >
          <DatePicker.MonthPicker disabled />
        </Form.Item>
        <Form.Item label="一年期当月均值(%)" name="oneCurrentAverage" rules={[rules.required()]}>
          <FormAmount addonAfter="%" step={0.01} />
        </Form.Item>
        <Form.Item
          label="一年期季度均值(%)"
          name="oneCurrentQuarterAverage"
          rules={[rules.required()]}
        >
          <FormAmount addonAfter="%" step={0.01} />
        </Form.Item>
        <Form.Item label="一年期当年均值(%)" name="oneAnnualAverage" rules={[rules.required()]}>
          <FormAmount addonAfter="%" step={0.01} />
        </Form.Item>

        <Form.Item label="1-3年期当月均值(%)" name="threeCurrentAverage" rules={[rules.required()]}>
          <FormAmount addonAfter="%" step={0.01} />
        </Form.Item>
        <Form.Item
          label="1-3年期季度均值(%)"
          name="threeCurrentQuarterAverage"
          rules={[rules.required()]}
        >
          <FormAmount addonAfter="%" step={0.01} />
        </Form.Item>
        <Form.Item label="1-3年期当年均值(%)" name="threeAnnualAverage" rules={[rules.required()]}>
          <FormAmount addonAfter="%" step={0.01} />
        </Form.Item>

        <Form.Item
          label="3年以上期当月均值(%)"
          name="fiveCurrentAverage"
          rules={[rules.required()]}
        >
          <FormAmount addonAfter="%" step={0.01} />
        </Form.Item>
        <Form.Item
          label="3年以上期季度均值(%)"
          name="fiveCurrentQuarterAverage"
          rules={[rules.required()]}
        >
          <FormAmount addonAfter="%" step={0.01} />
        </Form.Item>
        <Form.Item label="3年以上期当年均值(%)" name="fiveAnnualAverage" rules={[rules.required()]}>
          <FormAmount addonAfter="%" step={0.01} />
        </Form.Item>
      </Form>
    </Modal>
  )
}

export default FinancingCostEditModal
