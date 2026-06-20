import { observer } from '@zswl/admin'
import { Button, Form, Modal, Select } from '@zswl/components'
import { DatePicker, Input, Space, Tag, Tooltip } from 'antd'
import { useState } from 'react'
import _ from 'lodash'
import bankFlowCapitalApi from '@/api/budget/flowCenter/bankFlowCapitalApi'
import { amountFormat, formatPercent } from '@/utils'

const { RangePicker } = DatePicker

const { Item } = Form
function CashFlowModal({ store }) {
  const { editType } = store.offModal.getInitialValues() ?? {}
  const disabled = editType === 'EDIT'

  const [financeList, setFinanceList] = useState([])

  const orgChange = async (value, record) => {
    const res = await bankFlowCapitalApi.postInfoList(record)
    const newList = res.map(({ financingCode, receiptRepayBaseId, ...rest }) => ({
      label: financingCode,
      value: receiptRepayBaseId,
      ...rest,
    }))
    setFinanceList(newList)
  }

  return (
    <Modal
      title={`${disabled ? '编辑' : '新增'}现金流项目`}
      store={store.specialOffModal}
      okText={'确定'}
      width={600}
      destroyOnClose
    >
      <Form labelCol={{ span: 5 }}>
        <Item name={'orgId'} label="机构名称" rules={[{ required: true, message: '请选择！' }]}>
          <Select
            options={async () => {
              const res = await bankFlowCapitalApi.postOrgList({})
              return res.map((v) => ({ ...v, value: `${v.name}_${v.id}` }))
            }}
            fieldNames={{ label: 'name', value: 'value' }}
            onChange={orgChange}
            placeholder="请选择！"
            getPopupContainer={() => document.body}
            disabled={disabled}
          />
        </Item>
        <Item dependencies={['orgId']} noStyle>
          {({ getFieldValue }) => {
            const orgId = getFieldValue('orgId')
            if (!orgId) return null
            return (
              <Item
                label={'借据编号'}
                name={'receiptRepayBaseIdList'}
                rules={[{ required: true, message: '请选择！' }]}
              >
                <Select
                  placeholder="请选择！"
                  disabled={disabled}
                  optionLabelProp="label"
                  mode="multiple"
                >
                  {financeList.map(({ label, value, actualLoanDate, financingAmount }) => (
                    <Option value={value} label={label}>
                      <Space className="demo-option-label-item">
                        {label} <Tag color="green">{actualLoanDate}</Tag>
                        <Tag color="gold">
                          {amountFormat(formatPercent(financingAmount ?? 0))} 元
                        </Tag>
                      </Space>
                    </Option>
                  ))}
                </Select>
              </Item>
            )
          }}
        </Item>

        <Form.Item
          name={'actualLoanDate'}
          label={'应付日期'}
          rules={[{ required: true, message: '请选择！' }]}
        >
          <RangePicker style={{ width: '100%' }} />
        </Form.Item>
      </Form>
    </Modal>
  )
}

export default observer(CashFlowModal)
