import { observer } from '@zswl/admin'
import { App, Form, Modal, Select } from '@zswl/components'
import { Checkbox, DatePicker, Divider, Input, InputNumber, Space } from 'antd'
import { FormAmount } from '@/components/Form'
import styles from './index.less'
import { amountFormat, formatPercent } from '@/utils'
import { useEffect, useState } from 'react'
import moment from 'moment'
import { isString } from 'lodash'
import flowCenterApi from '@/api/budget/flowCenter/flowCenterApi'

const { Item } = Form
function WriteOffModal({ store }) {
  const [contractList, setContractList] = useState([])
  const [planCollectionDate, setPlanCollectionDate] = useState('')
  const { clientId,collectionId } = store?.receiptOffModal.getInitialValues?.() ?? {}
  const getContract = async (clientId) => {
    const res = await flowCenterApi.postClientContractMargin({ clientId })
    setContractList(res)
  }
  const onPaymentMethodChange = async (str) => {
    if (str !== '1') {
      return
    }
    const res = await flowCenterApi.postRecycleMarginPlan({ collectionId })
    // 不存在日期时返回今日
    setPlanCollectionDate(res.planCollectionDate ? moment(res.planCollectionDate, 'YYYY-MM-DD') : moment())
  }
  const contractChange = (val) => {
    const { earnestMoneyBalance } = contractList.find((item) => item.contractId === val)
    store.receiptOffModal.getFormStore().setFieldsValue({
      earnestMoneyBalance,
    })
  }
  useEffect(() => {
    clientId && getContract(clientId);
  }, [clientId])
  return (
    <Modal
      title={'手工核销'}
      store={store.receiptOffModal}
      okText={'确定'}
      width={600}
      destroyOnClose
    >
      <div style={{ maxHeight: 500, overflowY: 'auto', padding: '0 10px' }}>
        <Form
          labelCol={{ span: 8 }}
          preserve={true}
         >
          <div className={styles.title}>流水信息</div>

          <Item name={'collectionId'} hidden>
            <Input disabled />
          </Item>
          <Item name={'code'} hidden>
            <Input disabled />
          </Item>
          <Item label={'客户名称'} name={'clientName'}>
            <Input disabled />
          </Item>
          <Item label={'合同编号'} name={'contractCode'}>
            <Input disabled />
          </Item>
          <Item label={'现金流项目'} name={'cashFlowItem'}>
            <Select disabled options={'cashFlowItemEnum'} />
          </Item>
          <FormAmount.Item
            disabled
            label={'应收金额（元）'}
            isRequired={false}
            name={'planCollectionAmount'}
            min={-Infinity}
          />
          <FormAmount.Item
            disabled
            label={'本金（元）'}
            name={'principal'}
            isRequired={false}
            min={-Infinity}
          />
          <FormAmount.Item
            disabled
            label={'利息（元）'}
            name={'interest'}
            isRequired={false}
            min={-Infinity}
          />
          <FormAmount.Item
            disabled
            label={'已收（元）'}
            name={'collectionAmount'}
            isRequired={false}
            min={-Infinity}
          />
          <Divider />
          <div className={styles.title}> 本次核销</div>
          <Item
            label={'收款方式'}
            name={'collectionType'}
            rules={[{ required: true, message: '请选择收款方式！' }]}
          >
            <Select options={'paymentMethod'} />
          </Item>
          <Item dependencies={['collectionType', 'cashFlowItem','isRecycleManager']} noStyle>
            {({ getFieldValue }) => {
              const collectionType = getFieldValue('collectionType')
              const isRecycleManager = getFieldValue('isRecycleManager')
              const cashFlowItem = getFieldValue('cashFlowItem')
              if (cashFlowItem !== 'RENT') {
                return (
                  <>
                    <Item
                      label={'实收日期'}
                      rules={[{ required: true }]}
                      name={'collectionDate'}
                      transform={(value) => value && value.format('YYYY-MM-DD')}
                    >
                      <DatePicker />
                    </Item>

                    <FormAmount.Item
                      label={'实收金额'}
                      name={'actualAmountReceive'}
                      min={-Infinity}
                    />
                  </>
                )
              }
              const defaultComponent = (
                <>
                  <Item
                    label={'实收日期'}
                    name={'collectionDate'}
                    rules={[{ required: true }]}
                    transform={(value) => value && value.format('YYYY-MM-DD')}
                  >
                    <DatePicker />
                  </Item>

                  <FormAmount.Item label={'实收本金'} name={'paidInPrincipal'} min={-Infinity} />
                  <FormAmount.Item label={'实收利息'} name={'paidInInterest'} min={-Infinity} />
                  <FormAmount.Item
                    label={'实收罚息'}
                    name={'paidInPenaltyInterest'}
                    min={-Infinity}
                    isRequired={false}
                  />
                </>
              )

              if (collectionType === 'PJ') {
                return (
                  <div>
                    <Item
                      label={'票据号'}
                      name={'billCode'}
                      rules={[{ required: true, message: '请输入票据号！' }]}
                    >
                      <Input />
                    </Item>
                    <FormAmount.Item
                      label={'票据面额'}
                      name={'billAmount'}
                      // rules={[{ required: true, message: '请输入票据面额！' }]}
                    />
                    <Item
                      label={'票据到期日'}
                      name={'billExpireDate'}
                      transform={(value) => value && value.format('YYYY-MM-DD')}
                      rules={[{ required: true, message: '请输入票据到期日！' }]}
                    >
                      <DatePicker />
                    </Item>
                    <Item label={'票据买入价'} required>
                      <Space.Compact>
                        <Item name={'billBuyRateType'} valuePropName="checked">
                          <Checkbox>同项目 FTP</Checkbox>
                        </Item>
                        <Item dependencies={['billBuyRateType']} noStyle>
                          {({ getFieldValue }) => {
                            const billBuyRateType = getFieldValue('billBuyRateType')
                            return (
                              <FormAmount.Item
                                name={'billBuyRate'}
                                disabled={billBuyRateType}
                                min={-Infinity}
                                isRequired={!billBuyRateType}
                              />
                            )
                          }}
                        </Item>
                      </Space.Compact>
                    </Item>
                    {defaultComponent}
                  </div>
                )
              }
              if (collectionType === 'REFUND_MARGIN_DEDUCT') {
                return (
                  <div>
                    <Item label={'被抵扣客户'} name={'clientName'}>
                      <Input disabled />
                    </Item>
                    <Item label={'被抵扣合同'} name={'deductionContractId'}>
                      <Select
                        options={contractList}
                        onChange={contractChange}
                        fieldNames={{ label: 'contractCode', value: 'contractId' }}
                      />
                    </Item>
                    <FormAmount.Item
                      label={'保证金余额'}
                      name={'earnestMoneyBalance'}
                      isRequired={false}
                      disabled
                    />
                    {defaultComponent}
                    {
                    collectionType === 'REFUND_MARGIN_DEDUCT' &&
                    <Item
                      label={'是否回收保证金'}
                      name={'isRecycleManager'}
                      rules={[{ required: true, message: '是否回收保证金' }]}
                    >
                      <Select onChange={onPaymentMethodChange} options={[{ label:'不回收', value:'0'},{ label:'回收', value:'1'}]} />
                    </Item>
                    }
                    {
                      planCollectionDate &&
                      isRecycleManager === '1' &&
                      <>
                        <FormAmount.Item label={'回收金额（元）'} name={'recycleManagerAmount'} min={-Infinity} />
                        <Item
                          label={'回收日期'}
                          name={'recycleManagerDate'}
                          rules={[{ required: true }]}
                          initialValue={planCollectionDate}
                          transform={(value) => value && value.format('YYYY-MM-DD')}
                        >
                          <DatePicker />
                        </Item>
                      </>
                    }
                  </div>
                )
              }
              return defaultComponent
            }}
          </Item>
          <Item
            dependencies={[
              'paidInPrincipal',
              'actualAmountReceive',
              'paidInInterest',
              'paidInPenaltyInterest',
              'recycleManagerAmount'
            ]}
            noStyle
          >
            {({ getFieldValue,setFieldValue }) => {
              const actualAmountReceive = getFieldValue('actualAmountReceive')
              const paidInPrincipal = getFieldValue('paidInPrincipal') ?? 0
              const paidInInterest = getFieldValue('paidInInterest') ?? 0
              const paidInPenaltyInterest = getFieldValue('paidInPenaltyInterest') ?? 0
              const cashFlowItem = getFieldValue('cashFlowItem')
              if (getFieldValue('recycleManagerAmount') === null) {
                setFieldValue('recycleManagerAmount','')
              } else {
               setFieldValue('recycleManagerAmount',isString(getFieldValue('recycleManagerAmount')) ? getFieldValue('recycleManagerAmount') : (+paidInPrincipal + +paidInInterest + +paidInPenaltyInterest))

              }
              const notRent = cashFlowItem !== 'RENT'
              const total = notRent
                ? actualAmountReceive
                : +paidInPrincipal + +paidInInterest + +paidInPenaltyInterest
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
