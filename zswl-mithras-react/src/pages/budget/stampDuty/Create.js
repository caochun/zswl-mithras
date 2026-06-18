import { OrgSelect } from '@/components/Select'
import { observer } from '@zswl/admin'
import { Modal, Select } from '@zswl/components'
import { DatePicker, Form, Input, message } from 'antd'
import moment from 'moment'
import { useMemo, useState } from 'react'
import Currency from './Currency'
import CreateStore from './createStore'
import styles from './index.less'

const { Item } = Form
function Index({ open, setOpen, refresh }) {
  const store = useMemo(() => new CreateStore(), [])
  const [formRef] = Form.useForm()
  const [nameState, setNameState] = useState('')
  const [receiptCodeState, setReceiptCodeState] = useState(true)
  const [rentState, setRentState] = useState(true)
  const [startDate, setStartDate] = useState(true)
  const handleChange = async (value, values) => {
    if ('name' in value) {
      if (['RZZLHT', 'MMHT', 'ZLHT'].includes(value.name)) {
        store.updateBelongCode(value.name === 'MMHT' ? 'mmht' : 'ht')
        setNameState('RZZLHT')
        setReceiptCodeState(true)
        store.receiptCodeList = []
      } else if ('JKHT' === value.name) {
        store.updateBelongCode('rzht')
        setNameState('JKHT')
        setReceiptCodeState(false)
      } else {
        setNameState('')
        setRentState(false)
        setReceiptCodeState(false)
      }
      if (['RZZLHT', 'MMHT', 'ZLHT', 'JKHT'].includes(value.name)) {
        setStartDate(true)
      } else {
        setStartDate(false)
      }
      let taxRateVal
      if (['RZZLHT', 'JKHT'].includes(value.name)) {
        taxRateVal = '0.005'
      } else if (['MMHT', 'JSHT', 'JSGCHT', 'CLHT', 'YSHT'].includes(value.name)) {
        taxRateVal = '0.030'
      } else if (['CCBXHT', 'ZLHT', 'BGHT', 'CCHT'].includes(value.name)) {
        taxRateVal = '0.100'
      } else if ('YYZB' === value.name) {
        taxRateVal = '0.025'
      } else if ('CQZYSJ' === value.name) {
        taxRateVal = '0.050'
      }
      formRef.setFieldsValue({
        startDate: '',
        belongCode: '',
        clientName: '',
        belongOrgName: '',
        receiptCode: '',
        rent: 0,
        commission: 0,
        consultingFee: 0,
        amount: 0,
        stampDuty: 0,
        taxRate: taxRateVal,
      })
    }
    if ('belongCode' in value) {
      if (['RZZLHT', 'MMHT', 'ZLHT'].includes(values.name)) {
        const ret = await store.getClientItem(value.belongCode, 'constractList')
        if (ret && ret.length > 0) {
          formRef.setFieldValue('clientName', ret[0].clientName)
          formRef.setFieldValue('belongOrgName', ret[0].belongOrgName)
          if (['RZZLHT', 'ZLHT'].includes(values.name)) {
            formRef.setFieldValue('commission', ret[0].commission)
            formRef.setFieldValue('consultingFee', ret[0].consultingFee)
          }
          formRef.setFieldValue('stampDuty', ret[0].stampDuty)
          store.receiptCodeList = ret.map((item) => ({
            ...item,
            label: item.receiptCode,
            value: item.receiptCode,
          }))
        }
        formRef.setFieldValue('receiptCode', '')
        formRef.setFieldValue('startDate', '')
        formRef.setFieldValue('rent', 0)
      }
      if ('JKHT' === values.name) {
        const ret = await store.getClientItem(value.belongCode, 'fundContractsList')
        if (ret && ret.length > 0) {
          formRef.setFieldValue('clientName', ret[0].clientName)
          formRef.setFieldValue('belongOrgName', ret[0].belongOrgName)
          formRef.setFieldValue('startDate', moment(ret[0].startDate))
          formRef.setFieldValue('rent', ret[0].rent)
        }
      }
    }
    if ('receiptCode' in value) {
      const index = store.receiptCodeList.findIndex((item) => item.receiptCode === value.receiptCode)
      if (index > -1) {
        const selectedItem = store.receiptCodeList[index]
        if (selectedItem['startDate']) {
          formRef.setFieldValue('startDate', moment(selectedItem['startDate'], 'YYYY-MM-DD'))
        }
        setRentState(true)
        if ('rent' in selectedItem) {
          formRef.setFieldValue('rent', selectedItem['rent'])
        }
        if (['RZZLHT', 'ZLHT'].includes(values.name) && 'commission' in selectedItem) {
          formRef.setFieldValue('commission', selectedItem['commission'])
        }
        if (['RZZLHT', 'ZLHT'].includes(values.name) && 'consultingFee' in selectedItem) {
          formRef.setFieldValue('consultingFee', selectedItem['consultingFee'])
        }
        if (values.name === 'MMHT' && 'stampDuty' in selectedItem) {
          formRef.setFieldValue('stampDuty', selectedItem['stampDuty'])
        }
      }
    }
    // amount 计算金额值
    const { name: currentName, rent = 0, commission = 0, consultingFee = 0, amount = 0, taxRate = 0, stampDuty = 0, receiptCode } = formRef.getFieldsValue()
    const calcAmount = rent + commission + consultingFee
    amount !== calcAmount && formRef.setFieldValue('amount', calcAmount)
    if (!(currentName === 'MMHT' && receiptCode)) {
      const calcStampDuty = (parseFloat(taxRate) / 100) * calcAmount
      calcStampDuty !== stampDuty && formRef.setFieldValue('stampDuty', calcStampDuty)
    }
  }
  return (
    <Modal
      title={'新增印花税明细'}
      open={open}
      okText={'确定'}
      width={600}
      onCancel={() => {
        store.constractList = []
        store.fundContractsList = []
        store.receiptCodeList = []
        setOpen(false)
      }}
      onOk={async () => {
        const formValues = formRef.getFieldsValue()
        const validate = await formRef.validateFields()
        await store.add({ ...formValues, startDate: moment(formValues.startDate).format('YYYY-MM-DD') }, setOpen)
        message.success('新增成功')
        refresh && refresh()
        store.constractList = []
        store.fundContractsList = []
        store.receiptCodeList = []
      }}
      destroyOnClose={true}
    >
      <Form labelCol={{ span: 6 }} wrapperCol={{ span: 15 }} preserve={false} onValuesChange={handleChange} form={formRef}>
        <Item label={'申报税目名称'} name={'name'} rules={[{ required: true }]}>
          <Select options={'stampDutyBizTypeEnum'} />
        </Item>
        <Item label={'合同编号/融资编号'} name={'belongCode'} rules={[{ required: true }]}>
          {['RZZLHT', 'MMHT', 'ZLHT'].includes(nameState) ? (
            <Select options={store.constractList} />
          ) : 'JKHT' === nameState ? (
            <Select options={store.fundContractsList} />
          ) : (
            <Input placeholder={'请输入'} autoComplete="off" maxLength={100} />
          )}
        </Item>
        <Item noStyle dependencies={['name', 'belongCode']}>
          {({ getFieldValue }) => {
            const name = getFieldValue('name')
            const belongCode = getFieldValue('belongCode')
            let disabled = true
            if (['RZZLHT', 'MMHT', 'ZLHT'].includes(name) && belongCode) {
              disabled = true
            } else if ('JKHT' === name && belongCode) {
              disabled = true
            } else if (!name) {
              disabled = true
            } else {
              disabled = false
            }
            return (
              <Item label={'客户名称/融资机构'} name={'clientName'} rules={[{ required: true }]}>
                <Input placeholder={'请输入'} autoComplete="off" disabled={disabled} maxLength={100} />
              </Item>
            )
          }}
        </Item>
        <Item noStyle dependencies={['name', 'belongCode']}>
          {({ getFieldValue }) => {
            const name = getFieldValue('name')
            const belongCode = getFieldValue('belongCode')
            let disabled = true
            if (['RZZLHT', 'MMHT', 'ZLHT', 'JKHT'].includes(name) || !name) {
              disabled = true
            } else {
              disabled = false
            }
            if (disabled) {
              return (
                <Item label={'业务部门'} name={'belongOrgName'} rules={[{ required: true }]}>
                  <Input placeholder={'请输入'} autoComplete="off" disabled />
                </Item>
              )
            }
            return (
              <Item label={'业务部门'} name={'belongOrgName'} rules={[{ required: true }]}>
                <OrgSelect functionCode="stampDutyOrg" url="/stampDuty/orgs" valueName={true}></OrgSelect>
              </Item>
            )
          }}
        </Item>
        <Item label={'借据编号'} name={'receiptCode'} rules={[{ required: receiptCodeState }]}>
          {receiptCodeState ? <Select options={store.receiptCodeList} /> : <Input placeholder={'请输入'} autoComplete="off" maxLength={100} />}
        </Item>
        <Item label={'实际起租日'} name={'startDate'} rules={[{ required: true }]}>
          <DatePicker style={{ width: '100%' }} disabled={startDate} />
        </Item>
        <Item label={'不含税租金'} name={'rent'} rules={[{ required: true }]}>
          {rentState ? (
            <Currency className={styles.inputNumber} disabled addonAfter="元" precision={2} step="0.01" />
          ) : (
            <Currency step="0.01" className={styles.inputNumber} addonAfter="元" precision={2} />
          )}
        </Item>
        <Item noStyle dependencies={['name']}>
          {({ getFieldValue }) => {
            const name = getFieldValue('name')
            const isEditable = name && !['RZZLHT', 'MMHT', 'ZLHT', 'JKHT'].includes(name)
            return (
              <Item label={'不含税手续费'} name={'commission'} rules={[{ required: false }]}>
                <Currency
                  className={styles.inputNumber}
                  disabled={!isEditable}
                  addonAfter="元"
                  step="0.01"
                  precision={2}
                  maxLength={isEditable ? 50 : undefined}
                />
              </Item>
            )
          }}
        </Item>
        <Item noStyle dependencies={['name']}>
          {({ getFieldValue }) => {
            const name = getFieldValue('name')
            const isEditable = name && !['RZZLHT', 'MMHT', 'ZLHT', 'JKHT'].includes(name)
            return (
              <Item label={'不含税咨询费'} name={'consultingFee'} rules={[{ required: false }]}>
                <Currency
                  className={styles.inputNumber}
                  disabled={!isEditable}
                  addonAfter="元"
                  step="0.01"
                  precision={2}
                  maxLength={isEditable ? 50 : undefined}
                />
              </Item>
            )
          }}
        </Item>
        <Item label={'金额'} name={'amount'} rules={[{ required: true }]}>
          <Currency placeholder={'请输入'} disabled className={styles.inputNumber} addonAfter="元" step="0.01" precision={2} />
        </Item>
        <Item label={'印花税率（%）'} name={'taxRate'} rules={[{ required: true }]}>
          <Input className={styles.inputNumber} disabled addonAfter="%" />
        </Item>
        <Item label={'印花税'} name={'stampDuty'} rules={[{ required: true }]}>
          <Currency disabled className={styles.inputNumber} addonAfter="元" step="0.01" precision={2} />
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(Index)
