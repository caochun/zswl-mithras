import { observer } from '@zswl/admin'
import { Form, Modal, Select } from '@zswl/components'
import _debounce from 'lodash/debounce'
import { Input, DatePicker } from 'antd'
import { useState, useEffect } from 'react'
import moment from 'moment'
import { BankListSelect } from '../../../Select'
import Api from '@/api/financial/fundApi'

const { Item } = Form

function Index({ store }) {
  const { curItem } = store
  const [form] = Form.useForm()
  const [accountBank, setAccountBank] = useState()
  const [projList, setProjList] = useState([])

  const onOrgChange = (value) => {
    form.setFieldsValue({
      bankAccountId: undefined,
    })
    setAccountBank(value)
  }

  useEffect(() => {
    accountBank && getProjList()
  }, [accountBank])

  useEffect(() => {
    curItem && setAccountBank(curItem.accountBank)
  }, [curItem])

  const onProjChange = (data) => {
    const findCurr = projList.find((item) => item.bankAccountId === data.value)
    const { accountOpeningDate, accountType } = findCurr ?? {}
    form.setFieldsValue({
      accountType,
      accountOpeningDate: accountOpeningDate ? moment(accountOpeningDate) : undefined,
    })
  }

  const getProjList = _debounce(async () => {
    const res = await Api.postPayAccountBackInfoList({
      accountBank,
    })
    setProjList(res)
  }, 500)

  return (
    <Modal
      propsBy={(data) => {
        const title = `${data ? '编辑' : '新增'}`
        return {
          title,
        }
      }}
      store={store.$createModal}
      destroyOnClose
    >
      <Form form={form} labelCol={{ span: 6 }} wrapperCol={{ span: 18 }} preserve={false}>
        <Item
          label={'银行名称'}
          name={'accountBank'}
          rules={[{ required: true, message: '请选择！' }]}
        >
          <BankListSelect onChange={onOrgChange} />
        </Item>
        <Item dependencies={['accountBank']} noStyle>
          {({ getFieldValue }) => {
            const accountBankVal = getFieldValue('accountBank')
            if (!accountBankVal) {
              return null
            }
            return (
              <Item
                label={'银行账号'}
                name={'bankAccountId'}
                rules={[{ required: true, message: '请选择！' }]}
              >
                <Select
                  labelInValue
                  fieldNames={{
                    label: 'accountNumber',
                    value: 'bankAccountId',
                  }}
                  onChange={onProjChange}
                  options={projList}
                  placeholder="请选择！"
                  onSearch={(v) => getProjList(v)}
                />
              </Item>
            )
          }}
        </Item>

        <Item dependencies={['bankAccountId']} noStyle>
          {({ getFieldValue }) => {
            const bankAccountIdVal = getFieldValue('bankAccountId')
            if (!bankAccountIdVal) {
              return null
            }
            return (
              <>
                <Item label={'账户性质'} name={'accountType'}>
                  <Select options="baseDataBankAccountTypeEnum" disabled></Select>
                </Item>
                <Item label={'开户日期'} name="accountOpeningDate">
                  <DatePicker disabled style={{ width: '100%' }}></DatePicker>
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

export default observer(Index)
