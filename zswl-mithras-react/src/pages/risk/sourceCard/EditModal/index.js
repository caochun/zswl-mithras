import { observer } from '@zswl/admin'
import { App, Form, Modal, Select } from '@zswl/components'
import store from '../store'
import { Button, DatePicker, Input } from 'antd'
import styles from './index.less'
import { getRiskControlIndustryClassifySelectOptions, rules } from '@/utils'

function EditModal() {
  const [form] = Form.useForm()
  const { editMode } = store

  return (
    <Modal title={'新增评分卡'} store={store.createModal} destroyOnClose width={520}>
      <Form form={form} layout={'vertical'} className={styles.modalForm}>
        <Form.Item name="scorecardName" label="评分卡名称" required rules={[rules.required()]}>
          <Input />
        </Form.Item>
        <Form.Item
          noStyle
          shouldUpdate={(prev, cur) => prev?.suitTrade !== cur?.suitTrade}
        >
          {() => (
            <Form.Item name="suitTrade" label="适用风控行业分类" required rules={[rules.required()]}>
              <Select
                options={getRiskControlIndustryClassifySelectOptions(form.getFieldValue('suitTrade'))}
              />
            </Form.Item>
          )}
        </Form.Item>
        <Form.Item name="provinceSeat" label="选择省内/省外" required rules={[rules.required()]}>
          <Select options="provinceTypeEnum" />
        </Form.Item>
        <Form.Item
          name="year"
          label="适用年份"
          required
          rules={[rules.required()]}
          transform={(val) => val && val.format('YYYY')}
        >
          <DatePicker.YearPicker />
        </Form.Item>
        <Form.Item name="content" label="说明">
          <Input />
        </Form.Item>
      </Form>
    </Modal>
  )
}

export default observer(EditModal)
