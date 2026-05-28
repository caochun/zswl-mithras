import { getQuery, observer } from '@zswl/admin'
import { Modal, Form, Select, InputNumber, DatePicker } from '@zswl/components'
import dayjs from 'dayjs'
import FormAmount from '@/components/Form/FormAmount'

const { Item } = Form

/**
 * 汇率设置新增/编辑弹窗组件
 * @param {Object} store - 页面store对象
 */
const AddModal = ({ store }) => {
  const [form] = Form.useForm()
  const isFormApproval = getQuery('typeId') === 'approval'
  const { id, currency } = store.createModal?.getInitialValues() || {}
  const { year, month } = store.page?.getParams() || {}
  const applyTime = year ? moment(`${year}-${month}`) : undefined
  const isEdit = !!id

  const handleChangeDate = (date) => {
    form.setFieldValue('targetMonth', dayjs(date))
    form.setFieldValue('targetYear', dayjs(date))
  }
  return (
    <Modal
      title={isEdit ? '编辑汇率' : '新增汇率'}
      width={600}
      store={store.createModal}
      destroyOnClose
    >
      <Form form={form} labelCol={{ span: 6 }} wrapperCol={{ span: 16 }}>
        <Item
          label="年份"
          name="targetYear"
          rules={[{ required: true, message: '请输入年份' }]}
          transform={(value) => value?.year()}
        >
          <DatePicker.YearPicker placeholder="请选择年份" style={{ width: '100%' }} disabled />
        </Item>
        <Item
          label="月份"
          name="targetMonth"
          rules={[{ required: true, message: '请选择月份' }]}
          transform={(value) => value?.month() + 1}
        >
          <DatePicker.MonthPicker
            placeholder="请选择月份"
            style={{ width: '100%' }}
            format="MM月"
            disabled
          />
        </Item>

        <Item
          label="汇率日期"
          name="targetDate"
          rules={[{ required: true, message: '请选择汇率日期' }]}
          transform={(value) => value && moment(value).format('YYYY-MM-DD')}
        >
          <DatePicker
            placeholder="请选择汇率日期"
            style={{ width: '100%' }}
            format="YYYY-MM-DD"
            onChange={handleChangeDate}
            disabledDate={(current) =>
              current &&
              applyTime &&
              (current > applyTime.endOf('month') || current < applyTime.startOf('month'))
            }
          />
        </Item>
        <Item label="币种" name="currency" rules={[{ required: true, message: '请输入币种' }]}>
          <Select placeholder="请选择币种" options={'currencyType'} disabled={currency === 'USD'} />
        </Item>
        <Item label="汇率" name="exchangeRate" rules={[{ required: true, message: '请输入汇率' }]}>
          <FormAmount
            initFormat={1}
            placeholder="请输入汇率"
            min={0}
            precision={4}
            style={{ width: '100%' }}
          />
        </Item>
        <Item name="id" hidden />
      </Form>
    </Modal>
  )
}

export default observer(AddModal)
