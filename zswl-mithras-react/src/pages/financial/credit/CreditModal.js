import { observer } from '@zswl/admin'
import { Form, Modal } from '@zswl/components'
import store from './store'
import _debounce from 'lodash/debounce'
import { CreditOrgSelect } from '@/components/Select'
import { FormAmount } from '@/components/Form'
import { dateRangeTransform, rules } from '@/utils'
import styles from './index.less'
import Api from '@/api/financial/creditManage'
import { useState } from 'react'
import { DatePicker } from 'antd'

const { Item } = Form
function EditModal() {
  const [form] = Form.useForm()
  const [hasOldCredit, setHasOldCredit] = useState(false)
  const organizationIdChange = async (value) => {
    const res = await Api.getCreditLimit({ organizationId: value })
    setHasOldCredit(res.originalCreditLimit !== 0)
    form.setFieldsValue(res)
  }
  return (
    <Modal
      title="新增授信"
      store={store.createModal}
      okText="确定"
      onOk={form.submit}
      destroyOnClose
    >
      <Form labelCol={{ span: 6 }} preserve={false} form={form}>
        <Item
          label="授信机构"
          name="organizationId"
          rules={[{ required: true, message: '请选择授信机构！' }]}
        >
          <CreditOrgSelect onChange={organizationIdChange} />
        </Item>
        {hasOldCredit && (
          <>
            <Item
              label="授信生效时间"
              name="effectiveDate"
              transform={(val) => dateRangeTransform(val, 'effectiveDateFrom', 'effectiveDateTo')}
              rules={[rules.required()]}
            >
              <DatePicker.RangePicker />
            </Item>
            <FormAmount.Item disabled label="原授信额度" name="originalCreditLimit" />
            <FormAmount.Item disabled label="已融资金额" name="financingAmount" />
            <Item label="剩余授信额度" name="remainingCreditLimit">
              <FormAmount disabled min={-Infinity} />
            </Item>

            <FormAmount.Item label="新授信总额度" name="totalCreditLimit" labelCol={{ span: 6 }} />

            <div className={styles.warmingTip}>
              温馨提示：确认新增后，原授信失效，原授信项下融资关联新授信。
            </div>
          </>
        )}
        {!hasOldCredit && <FormAmount.Item label={'新增授信总额度'} name="totalCreditLimit" />}
      </Form>
    </Modal>
  )
}

export default observer(EditModal)
