import { observer } from '@zswl/admin'
import { Form, Modal, Select } from '@zswl/components'
import { OrgSelect } from '@/components/Select'
import { debounce as _debounce } from 'lodash'
import { Input, DatePicker, Checkbox } from 'antd'
import { FormAmount } from '@/components/Form'
import { useState, useEffect } from 'react'
import moment from 'moment'
import styles from '../index.less'
import Api from '@/api/financial/fundApi'
import { BankListSelect } from '../../../Select'

const { Item } = Form

function FinancialDirectPledgeCreateModal({ store, financingId }) {
  const { curItem, contractList } = store
  const [form] = Form.useForm()
  const [accountBank, setAccountBank] = useState()
  const [bankList, setBankList] = useState([])

  const onContractChange = (value) => {
    const findCurr = contractList.find((item) => item.contractId === value.value) ?? {}
    const {
      bizType,
      bizDeptId,
      clientId,
      projReviewId,
      bizDeptName,
      clientName,
      projName,
      applyCreditAmount,
      actualLeaseDate,
      settleTime,
      remainingUnpaidPrincipal,
    } = findCurr ?? {}

    form.setFieldsValue({
      bizType,
      bizDeptId,
      clientName,
      bizDeptName,
      projName,
      projReviewId,
      contractAmount: applyCreditAmount,
      contractStartDate: actualLeaseDate && moment(actualLeaseDate),
      contractEndDate: settleTime && moment(settleTime),
      remainingUnpaidPrincipal,
    })
  }

  useEffect(() => {
    if (curItem) {
      setAccountBank(curItem.accountBank)

      setTimeout(() => {
        form.setFieldsValue({ accountNumber: curItem.accountNumber })
      }, 0)
    }
  }, [curItem])

  const getBankList = _debounce(async () => {
    const res = await Api.postPayAccountBackInfoList({
      accountBank,
    })
    setBankList(res)
  }, 500)

  const onAccountBankChange = (value) => {
    form.setFieldsValue({
      accountNumber: undefined,
      accountName: undefined,
    })
    setAccountBank(value)
  }

  const onBankChange = (data) => {
    const findCurr = bankList.find((item) => item.accountNumber === data)
    const { accountName } = findCurr ?? {}
    form.setFieldsValue({
      accountName,
    })
  }

  useEffect(() => {
    accountBank && getBankList()
  }, [accountBank])
  return (
    <Modal
      propsBy={(data) => {
        const title = `${data ? '编辑' : '新增'}关联合同明细`
        return {
          title,
        }
      }}
      store={store.$createModal}
      destroyOnClose
    >
      <Form form={form} labelCol={{ span: 6 }} wrapperCol={{ span: 18 }} preserve={false}>
        <Item
          label={'合同编号'}
          name={'contractId'}
          rules={[{ required: true, message: '请选择！' }]}
        >
          <Select
            labelInValue
            fieldNames={{ label: 'contractCode', value: 'contractId' }}
            onChange={onContractChange}
            options={contractList}
            placeholder="请选择！"
            onSearch={(v) => store.getContractSearch(v)}
          />
        </Item>

        <Item dependencies={['contractId']} noStyle>
          {({ getFieldValue }) => {
            const contractCodeVal = getFieldValue('contractId')
            if (!contractCodeVal) {
              return null
            }
            return (
              <>
                <Item label={'业务部门id'} name={'bizDeptId'} hidden>
                  <Input />
                </Item>
                <Item label={'业务部门'} name={'bizDeptName'}>
                  <Input disabled />
                </Item>
                <Item label={'项目评审 id'} name={'projReviewId'} hidden>
                  <Input />
                </Item>
                <Item label={'项目名称'} name={'projName'}>
                  <Input disabled />
                </Item>
                <Item label={'客户名称'} name={'clientName'}>
                  <Input disabled />
                </Item>
              </>
            )
          }}
        </Item>

        <Item dependencies={['contractId']} noStyle>
          {({ getFieldValue }) => {
            const contractIdVal = getFieldValue('contractId')
            if (!contractIdVal) {
              return null
            }
            return (
              <>
                <Item label={'业务类型'} name={'bizType'}>
                  <Select options="projEstablishBizType" disabled></Select>
                </Item>
                <FormAmount.Item
                  label={'合同金额(元)'}
                  name={'contractAmount'}
                  disabled
                  isRequired={false}
                ></FormAmount.Item>
                <Item label={'合同期限'}>
                  <div className={styles.row}>
                    <Item label={''} name={'contractStartDate'}>
                      <DatePicker disabled></DatePicker>
                    </Item>
                    <Item>~</Item>
                    <Item label={''} name={'contractEndDate'}>
                      <DatePicker disabled></DatePicker>
                    </Item>
                  </div>
                </Item>
                <FormAmount.Item
                  label={'剩余未还本金(元)'}
                  name={'remainingUnpaidPrincipal'}
                  disabled
                  isRequired={false}
                ></FormAmount.Item>
              </>
            )
          }}
        </Item>
        <Item name="isPledge" label="是否质押" valuePropName="checked">
          <Checkbox />
        </Item>
        <Item name="isSupervise" label="是否监管" valuePropName="checked">
          <Checkbox
            onChange={async (e) => {
              if (e.target.checked) {
                form.setFieldsValue({
                  accountBank: undefined,
                  accountNumber: undefined,
                })
              } else {
                const res = await Api.getDefaultValues()
                form.setFieldsValue({
                  accountBank: res.accountBank,
                  accountNumber: res.accountNumber,
                  accountName: res.accountName,
                })
              }
            }}
          />
        </Item>
        <Item dependencies={['isSupervise']} noStyle>
          {({ getFieldValue }) => {
            const isSupervise = getFieldValue('isSupervise')
            if (!isSupervise) {
              return (
                <>
                  <Item
                    initialValue={'中国⼯商银⾏杭州市武林⽀⾏'}
                    label={'银行名称'}
                    name={'accountBank'}
                  >
                    <Input disabled />
                  </Item>
                  <Item
                    initialValue={'1202 0212 1990 0394 595'}
                    label={'银行账号'}
                    name={'accountNumber'}
                  >
                    <Input disabled />
                  </Item>{' '}
                  <Item
                    label={'户名'}
                    name={'accountName'}
                    initialValue={'浙江浙商融资租赁有限公司'}
                  >
                    <Input disabled></Input>
                  </Item>
                </>
              )
            }
            return (
              <>
                <Item
                  label={'开户行'}
                  name={'accountBank'}
                  rules={[{ required: true, message: '请选择' }]}
                >
                  <BankListSelect onChange={onAccountBankChange} />
                </Item>
                <Item dependencies={['accountBank']} noStyle>
                  {({ getFieldValue }) => {
                    const accountBankVal = getFieldValue('accountBank')
                    if (!accountBankVal) {
                      return null
                    }
                    return (
                      <>
                        <Item
                          label={'银行账号'}
                          name={'accountNumber'}
                          rules={[{ required: true, message: '请选择' }]}
                        >
                          <Select
                            fieldNames={{
                              label: 'accountNumber',
                              value: 'accountNumber',
                            }}
                            onChange={onBankChange}
                            options={bankList}
                            placeholder="请选择！"
                            onSearch={(v) => getBankList(v)}
                          />
                        </Item>
                        <Item label={'户名'} name={'accountName'}>
                          <Input disabled></Input>
                        </Item>
                      </>
                    )
                  }}
                </Item>
              </>
            )
          }}
        </Item>
        <Item name="id" hidden>
          <Input />
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(FinancialDirectPledgeCreateModal)
