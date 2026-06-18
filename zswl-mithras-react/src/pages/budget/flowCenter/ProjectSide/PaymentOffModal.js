import { observer } from '@zswl/admin'
import { App, Form, Modal, Select } from '@zswl/components'
import { Checkbox, DatePicker, Divider, Input, InputNumber, Space } from 'antd'
import { FormAmount } from '@/components/Form'
import styles from './index.less'
import { useState } from 'react'
import bankFlowProcessingCenterApi from '@/api/budget/flowCenter/bankFlowProcessingCenterApi'

const { Item } = Form
function WriteOffModal({ store }) {
  const { paymentId } = store.offModal.getInitialValues() ?? {}
  const [cashFlowCodeList, setCashFLowCodeList] = useState([])
  const [form] = Form.useForm()
  const cashFlowCodeChange = (val) => {
    const { writeOffedAmount, paymentActualDetailId, payInAmount } =
      cashFlowCodeList.find((item) => item.cashFlowCode === val) ?? {}
    form.setFieldsValue({
      paidAmount: writeOffedAmount,
      paymentActualDetailId,
      paymentAmount: payInAmount,
    })
  }

  const paymentMethodChange = async (val) => {
    try {
      const res =
        await bankFlowProcessingCenterApi.postCollectionFlowCenterBusinessPaymentManualCashFlowList(
          {
            paymentId,
            paymentMethod: App.matchOption('paymentMethod', val)?.label,
          }
        )
      setCashFLowCodeList(res)
      form.setFieldsValue({
        cashFlowCode: res?.[0]?.cashFlowCode,
        paidAmount: res?.[0]?.writeOffedAmount,
        paymentAmount: res?.[0].payInAmount,
        paymentActualDetailId: res?.[0]?.paymentActualDetailId,
      })
    } catch (e) {
      console.log(e)
      setCashFLowCodeList([])
      form.setFieldsValue({
        cashFlowCode: null,
        paidAmount: 0,
        paymentActualDetailId: null,
        paymentAmount: 0,
      })
    }
  }
  return (
    <Modal title={'手工核销'} store={store.offModal} okText={'确定'} width={600} destroyOnClose>
      <div style={{ maxHeight: 500, overflowY: 'auto', padding: '0 10px' }}>
        <Form labelCol={{ span: 8 }} form={form}>
          <div className={styles.title}>流水信息</div>

          <Item name={'paymentId'} hidden>
            <Input disabled />
          </Item>
          <Item name={'paymentActualDetailId'} hidden>
            <Input disabled />
          </Item>
          <Item label={'客户名称'} name={'clientName'}>
            <Input disabled />
          </Item>
          <Item label={'合同编号'} name={'contractCode'}>
            <Input disabled />
          </Item>
          <Item label={'现金流项目'} name={'cashFlowItem'}>
            <Select disabled options={'paymentFlowItemEnum'} />
          </Item>

          <FormAmount.Item disabled label={'应付金额（元）'} name={'paymentAmount'} />

          <FormAmount.Item
            disabled
            label={'已付金额（元）'}
            name={'paidAmount'}
            isRequired={false}
          />

          <Divider />
          <div className={styles.title}> 本次核销</div>
          <Item
            label={'付款方式'}
            name={'paymentMethod'}
            rules={[{ required: true, message: '请选择付款方式！' }]}
          >
            <Select options={'paymentMethod'} onChange={paymentMethodChange} />
          </Item>
          <Item
            name={'cashFlowCode'}
            label={'现金流编号'}
            rules={[{ required: true, message: '请输入现金流编号' }]}
          >
            <Select
              options={cashFlowCodeList}
              fieldNames={{ label: 'cashFlowCode', value: 'cashFlowCode' }}
              onChange={cashFlowCodeChange}
            />
          </Item>
          <Item dependencies={['paymentMethod']} noStyle>
            {({ getFieldValue }) => {
              const paymentMethod = getFieldValue('paymentMethod')

              const defaultComponent = (
                <>
                  <Item
                    label={'实付日期'}
                    name={'paidInDate'}
                    rules={[{ required: true }]}
                    transform={(value) => value && value.format('YYYY-MM-DD')}
                  >
                    <DatePicker />
                  </Item>

                  <FormAmount.Item label={'付款金额（元）'} name={'paidInAmount'} min={-Infinity} />
                </>
              )

              if (paymentMethod === 'PJ') {
                return (
                  <div>
                    <Item
                      label={'票据号'}
                      name={'billCode'}
                      rules={[{ required: true, message: '请输入票据号！' }]}
                    >
                      <Input />
                    </Item>
                    <FormAmount.Item label={'票据面额'} name={'billAmount'} min={-Infinity} />
                    <Item
                      label={'票据到期日'}
                      name={'billExpireDate'}
                      transform={(value) => value && value.format('YYYY-MM-DD')}
                      rules={[{ required: true, message: '请输入票据到期日！' }]}
                    >
                      <DatePicker />
                    </Item>

                    {defaultComponent}
                  </div>
                )
              }
              return defaultComponent
            }}
          </Item>
        </Form>
      </div>
    </Modal>
  )
}

export default observer(WriteOffModal)
