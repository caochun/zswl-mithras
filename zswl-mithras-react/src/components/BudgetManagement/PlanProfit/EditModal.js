import { dateRangeTransformV2 } from '@/utils'
import { observer } from '@zswl/admin'
import { App, Form, Modal, Select } from '@zswl/components'
import { DatePicker, Input } from 'antd'
import { useState, useEffect } from 'react'

const { Item } = Form
const { RangePicker } = DatePicker

function EditModal({ store }) {
  const { editType, budgetType, needCollect } = store.getInitialValues() ?? {}
  const { budgetPlanTypeEnum } = App.getData().optionsType
  const isEdit = editType === 'EDIT'
  const modalForm = store.getFormStore()
  const initHideCollect = budgetType === 'OTHER'
  const [hideCollect, setHideCollect] = useState(initHideCollect)

  useEffect(() => {
    const budgetType = modalForm.getFieldValue('budgetType')
    if (['YEAR', 'HALF_OF_YEAR', 'MONTH', 'MONTH_ADJUST'].includes(budgetType) && !isEdit) {
      modalForm.setFieldValue('needCollect', 1)
      setHideCollect(false)
    } else if (['OTHER'].includes(budgetType) && !isEdit) {
      modalForm.setFieldValue('needCollect', 0)
      setHideCollect(true)
    }
  }, [modalForm.getFieldValue('budgetType'), isEdit])

  useEffect(() => {
    setHideCollect(!needCollect)
  }, [needCollect])

  return (
    <Modal
      title={`${isEdit ? '调整' : '创建'}预算`}
      store={store}
      okText={'确定'}
      width={720}
      destroyOnClose
      afterClose={() => {
        modalForm.resetFields()
      }}
    >
      <Form labelCol={{ span: 8 }}>
        <div style={{ maxHeight: 500, overflowY: 'auto', padding: '0 10px' }}>
          <Item
            label="预算区间"
            name="budgetDate"
            rules={[{ required: true, message: '请选择预算区间！' }]}
            transform={(value) => dateRangeTransformV2(value, 'budgetDate')}
          >
            <RangePicker style={{ width: '100%' }} disabled={isEdit} format="YYYY-MM-DD" />
          </Item>
          <Item
            label="填报区间"
            name="writeDate"
            rules={[{ required: true, message: '请选择填报区间！' }]}
            transform={(value) => dateRangeTransformV2(value, 'writeDate')}
          >
            <RangePicker style={{ width: '100%' }} format="YYYY-MM-DD" />
          </Item>
          {isEdit && (
            <Item label="计划名称" name="budgetPlanName">
              <Input placeholder="请输入计划名称" disabled={isEdit} />
            </Item>
          )}
          <Item
            label="预算类型"
            name="budgetType"
            rules={[{ required: true, message: '请选择预算类型！' }]}
          >
            <Select
              options={
                isEdit
                  ? budgetPlanTypeEnum
                  : budgetPlanTypeEnum.filter((item) => item.value !== 'MONTH_ADJUST')
              }
              placeholder="请选择预算类型"
              disabled={isEdit}
              onChange={(value) => {
                modalForm.setFieldValue('needCollect', value === 'OTHER' ? 0 : 1)
                setHideCollect(value === 'OTHER')
              }}
            />
          </Item>
          <Item dependencies={['budgetType']} noStyle>
            {({ getFieldValue }) => {
              const budgetType = getFieldValue('budgetType')
              return (
                <Item
                  label="是否收集"
                  name="needCollect"
                  rules={[{ required: true, message: '请选择是否收集！' }]}
                >
                  <Select
                    options="yesOrNo"
                    placeholder="请选择是否收集"
                    disabled={isEdit}
                    onChange={(value) => {
                      setHideCollect(!value)
                    }}
                  />
                </Item>
              )
            }}
          </Item>
          {!hideCollect && (
            <Item
              label="收集截止时间"
              name="collectDateTo"
              rules={[{ required: true, message: '请选择收集截止时间！' }]}
              transform={(value) => value && moment(value).format('yyyy-MM-DD')}
            >
              <DatePicker style={{ width: '100%' }} format="YYYY-MM-DD" />
            </Item>
          )}
          <Item name="budgetPlanId" hidden>
            <Input />
          </Item>
        </div>
      </Form>
    </Modal>
  )
}

export default observer(EditModal)
