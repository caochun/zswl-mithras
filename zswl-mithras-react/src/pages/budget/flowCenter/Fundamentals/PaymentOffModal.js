import { observer } from '@zswl/admin'
import { App, Form, Modal, Select } from '@zswl/components'
import { Checkbox, DatePicker, Divider, Input, InputNumber, Space } from 'antd'
import { FormAmount } from '@/components/Form'
import styles from './index.less'
import { amountFormat, formatPercent } from '@/utils'

const { Item } = Form
function WriteOffModal({ store }) {
  const { cashFlowItem } = store.offModal.getInitialValues() ?? {}
  const isPay = store.flowType === 'PAY'
  const isRepay = cashFlowItem === 'REPAY'
  return (
    <Modal title={'手工核销'} store={store.offModal} okText={'确定'} width={600} destroyOnClose>
      <div style={{ maxHeight: 500, overflowY: 'auto', padding: '0 10px' }}>
        <Form labelCol={{ span: 8 }}>
          <div className={styles.title}>流水信息</div>

          <Item name={'receiptRepayId'} hidden>
            <Input disabled />
          </Item>

          <Item name={'serialNo'} hidden>
            <Input disabled />
          </Item>

          <Item label={'融资编号'} name={'financingCode'}>
            <Input disabled />
          </Item>
          <Item label={'现金流项目'} name={'cashFlowItem'}>
            <Select disabled options={'businessFlowFinanceCashFlowItemType'} />
          </Item>
          <FormAmount.Item
            disabled
            label={isPay ? '应付金额（元）' : '应收金额（元）'}
            name={'amount'}
            isRequired={false}
          />
          {isRepay && (
            <FormAmount.Item
              disabled
              label={'本金（元）'}
              name={'actualVerifyPrincipalAmount'}
              isRequired={false}
              min={-Infinity}
            />
          )}
          {isRepay && (
            <FormAmount.Item
              disabled
              label={'利息（元）'}
              name={'actualVerifyInterestAmount'}
              isRequired={false}
              min={-Infinity}
            />
          )}
          <FormAmount.Item
            disabled
            label={'已付金额（元）'}
            name={'actualVerifyAmount'}
            isRequired={false}
          />

          <Divider />
          <div className={styles.title}> 本次核销</div>
          <Item
            label={'付款方式'}
            name={'settleMethod'}
            rules={[{ required: true, message: '请选择付款方式！' }]}
          >
            <Select options={'paymentMethod'} />
          </Item>
          <Item dependencies={['settleMethod']} noStyle>
            {({ getFieldValue }) => {
              const settleMethod = getFieldValue('settleMethod')

              const defaultComponent = (
                <>
                  <Item
                    label={'实付日期'}
                    name={'cashFlowDate'}
                    rules={[{ required: true }]}
                    transform={(value) => value && value.format('YYYY-MM-DD')}
                  >
                    <DatePicker />
                  </Item>
                  {isRepay && (
                    <FormAmount.Item label={'实收本金'} name={'principalAmount'} min={-Infinity} />
                  )}
                  {isRepay && (
                    <FormAmount.Item label={'实收利息'} name={'interestAmount'} min={-Infinity} />
                  )}
                  {!isRepay && (
                    <FormAmount.Item
                      label={'付款金额（元）'}
                      name={'totalAmount'}
                      min={-Infinity}
                    />
                  )}
                </>
              )

              if (settleMethod === 'PJ') {
                return (
                  <div>
                    <Item
                      label={'票据号'}
                      name={'billCode'}
                      rules={[{ required: true, message: '请输入票据号！' }]}
                    >
                      <Input />
                    </Item>
                    <FormAmount.Item label={'票据面额'} name={'billAmount'} />
                    <Item
                      label={'票据到期日'}
                      name={'billExpireDate'}
                      transform={(value) => value && value.format('YYYY-MM-DD')}
                      rules={[{ required: true, message: '请输入票据到期日！' }]}
                    >
                      <DatePicker />
                    </Item>

                    {!isPay && (
                      <FormAmount.Item
                        name={'billBuyRate'}
                        label={'票据买入价'}
                        required
                        min={-Infinity}
                      />
                    )}

                    {defaultComponent}
                  </div>
                )
              }
              return defaultComponent
            }}
          </Item>
          <Item dependencies={['principalAmount', 'interestAmount', 'totalAmount']} noStyle>
            {({ getFieldValue }) => {
              const principalAmount = getFieldValue('principalAmount') ?? 0
              const interestAmount = getFieldValue('interestAmount') ?? 0
              const totalAmount = getFieldValue('totalAmount') ?? 0

              const total = +principalAmount + +interestAmount + +totalAmount
              return (
                <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
                  合计金额：{amountFormat(formatPercent(total)) ?? 0}
                </div>
              )
            }}
          </Item>
        </Form>
      </div>
    </Modal>
  )
}

export default observer(WriteOffModal)
