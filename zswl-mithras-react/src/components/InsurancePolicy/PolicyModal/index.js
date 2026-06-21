import { observer } from '@zswl/admin'
import { Form, Modal, Select } from '@zswl/components'
import { DatePicker, Input } from 'antd'
import moment from 'moment'
import DataUpload from '@/components/DataUpload'
import { FormAmount } from '@/components/Form'

const layout = {
  labelCol: { span: 7 },
  wrapperCol: { span: 17 },
}
const InsurancePolicyModal = ({ store }) => {
  const [form] = Form.useForm()
  const { isPolicyPage, paramsAsPolicy } = store
  const isRequired = !isPolicyPage || paramsAsPolicy.type === 'policyRemind'
  return (
    <Modal
      store={store.policyModal}
      destroyOnClose
      propsBy={(data) => {
        return {
          title: (!data ? '新增' : '编辑') + '保单信息',
        }
      }}
    >
      <Form form={form} {...layout}>
        <Form.Item
          label="保单编号"
          name="policyCode"
          rules={[
            {
              required: isRequired,
              message: '请输入',
            },
          ]}
        >
          <Input />
        </Form.Item>
        <Form.Item
          label="保险机构"
          name="insuranceCompany"
          rules={[
            {
              required: isRequired,
              message: '请输入',
            },
          ]}
        >
          <Input />
        </Form.Item>
        <Form.Item
          label="险种"
          name="policyType"
          rules={[
            {
              required: isRequired,
              message: '请输入',
            },
          ]}
        >
          <Select options="policyTypeEnum" />
        </Form.Item>
        <FormAmount.Item
          label="保单金额"
          name="policyAmount"
          isRequired={false}
          rules={[
            {
              required: isRequired,
              message: '请输入',
            },
          ]}
        ></FormAmount.Item>
        <Form.Item
          label="保险起始日"
          name="insuranceStartDate"
          rules={[
            {
              required: isRequired,
              message: '请选择',
            },
          ]}
        >
          <DatePicker style={{ width: '100%' }} format={'YYYY-MM-DD'} />
        </Form.Item>
        <Form.Item
          label="保险到期日"
          name="insuranceEndDate"
          dependencies={['insuranceEndDate']}
          rules={[
            {
              required: isRequired,
              message: '请选择',
            },
            ({ getFieldValue }) => ({
              validator(_, value) {
                const insuranceStartDate = getFieldValue('insuranceStartDate')
                if (insuranceStartDate && moment(value).isBefore(moment(insuranceStartDate))) {
                  return Promise.reject(new Error('保险到期日不能小于保险起始日！'))
                }
                return Promise.resolve()
              },
            }),
          ]}
        >
          <DatePicker style={{ width: '100%' }} format={'YYYY-MM-DD'} />
        </Form.Item>
        <Form.Item
          label="是否续保"
          name="renewInsuranceFlag"
          rules={[
            {
              required: isRequired,
              message: '请选择',
            },
          ]}
        >
          <Select options="policyRenewInsuranceEnum" />
        </Form.Item>
        <Form.Item label="标识信息" name="identificationInformation">
          <Input />
        </Form.Item>
        <Form.Item label="备注" name="remark">
          <Input.TextArea row={4} />
        </Form.Item>
        <Form.Item
          name="files"
          rules={[{ required: isRequired, message: '请上传文件！' }]}
          label="保单资料"
        >
          <DataUpload
            accept="*"
            maxCount={10}
            onRemove={(file) => {
              store.removeFiles(file)
            }}
          />
        </Form.Item>
      </Form>
    </Modal>
  )
}
export default observer(InsurancePolicyModal)
