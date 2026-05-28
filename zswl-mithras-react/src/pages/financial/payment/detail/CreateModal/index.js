import { observer } from '@zswl/admin'
import { Form, Modal, Select } from '@zswl/components'
import { OrgSelect } from '@/components'
import { debounce as _debounce } from 'lodash'
import { Input, InputNumber, DatePicker } from 'antd'
import Amount from '@/components/Amount'
import { useState, useEffect } from 'react'
import moment from 'moment'
import styles from '../index.less'
import Api from '@/pages/financial/fund/api'

const { Item } = Form

function Index({ store, financingId }) {
  const { curItem } = store
  const [form] = Form.useForm()
  const [bizDeptId, setBizDeptId] = useState()
  const [projId, setProjId] = useState()
  const [projList, setProjList] = useState([])
  const [contractList, setContractList] = useState([])

  const onOrgChange = (value) => {
    form.setFieldsValue({
      projReviewId: undefined,
    })
    setBizDeptId(value)
  }

  const onProjChange = (data) => {
    form.setFieldsValue({
      contractId: undefined,
    })
    setProjId(data.value)
  }

  const onContractChange = (value) => {
    const findCurr = contractList.find((item) => item.contractId === value.value) ?? {}
    const { bizType, applyCreditAmount, actualLeaseDate, settleTime, remainingUnpaidPrincipal } =
      findCurr ?? {}
    form.setFieldsValue({
      bizType,
      contractAmount: applyCreditAmount,
      contractStartDate: actualLeaseDate && moment(actualLeaseDate),
      contractEndDate: settleTime && moment(settleTime),
      remainingUnpaidPrincipal,
    })
  }

  useEffect(() => {
    if (curItem) {
      setBizDeptId(curItem.bizDeptId)
      setProjId(curItem.projReviewId)
    }
  }, [curItem])

  useEffect(() => {
    bizDeptId && getProjList()
  }, [bizDeptId])

  useEffect(() => {
    projId && getContractList()
  }, [projId])

  const getProjList = _debounce(async (val) => {
    const res = await Api.postProjList({
      projName: val,
      bizDeptId,
      financingId,
    })
    setProjList(res)
  }, 500)

  const getContractList = _debounce(async (val) => {
    const res = await Api.postContractList({
      projId,
      financingId,
    })
    setContractList(res)
  }, 500)

  return (
    <Modal
      propsBy={(data) => {
        const title = `${data ? '编辑' : '新增'}质押`
        return {
          title,
        }
      }}
      store={store}
      footer={null}
      destroyOnClose
    >
      <Form form={form} labelCol={{ span: 6 }} wrapperCol={{ span: 18 }} preserve={false}>
        <Item
          label={'业务部门'}
          name={'bizDeptId'}
          rules={[{ required: true, message: '请选择！' }]}
        >
          <OrgSelect functionCode="selectorgs-groupCreditReview" onChange={onOrgChange} disabled />
        </Item>
        <Item dependencies={['bizDeptId']} noStyle>
          {({ getFieldValue }) => {
            const bizDeptIdVal = getFieldValue('bizDeptId')
            if (!bizDeptIdVal) {
              return null
            }
            return (
              <Item
                label={'项目名称'}
                name={'projReviewId'}
                rules={[{ required: true, message: '请选择！' }]}
              >
                <Select
                  labelInValue
                  fieldNames={{ label: 'projName', value: 'projReviewId' }}
                  onChange={onProjChange}
                  options={projList}
                  placeholder="请选择！"
                  onSearch={(v) => getProjList(v)}
                  disabled
                />
              </Item>
            )
          }}
        </Item>
        <Item dependencies={['projReviewId']} noStyle>
          {({ getFieldValue }) => {
            const projIdVal = getFieldValue('projReviewId')
            const bizDeptIdVal = getFieldValue('bizDeptId')
            if (!bizDeptIdVal || !projIdVal) {
              return null
            }
            return (
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
                  onSearch={(v) => getContractList(v)}
                  disabled
                />
              </Item>
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
                <Item label={'合同金额(元)'} name={'contractAmount'}>
                  <Amount>
                    <InputNumber style={{ width: '100%' }} disabled />
                  </Amount>
                </Item>
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
                <Item label={'剩余未还本金(元)'} name={'remainingUnpaidPrincipal'}>
                  <Amount>
                    <InputNumber style={{ width: '100%' }} disabled placeholder="" />
                  </Amount>
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
