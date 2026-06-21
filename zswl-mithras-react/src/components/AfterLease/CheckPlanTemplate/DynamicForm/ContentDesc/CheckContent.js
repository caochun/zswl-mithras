import { observer } from '@zswl/admin'
import styles from './style.less'
import React, { forwardRef, useEffect, useImperativeHandle, useMemo, useRef, useState } from 'react'
import { Button, message, Space } from 'antd'
import { Form } from '@zswl/components'
import { errorFormScroll } from '@/utils'

const defaultValues = {
  currentPeriod: 0,
  samePeriodLastYear: 0,
  growthRate: 0,
}
export const TABLE_INITIAL_VALUES = [
  { subject: '其他应收款' },
  { subject: '存货' },
  { subject: '总资产' },
  { subject: '其他应付款' },
  { subject: '有息负债' },
  { subject: '总负债' },
  { subject: '主营业务收入' },
]
export const TABLE_INITIAL_VALUES2 = [
  { subject: '主营业务收入' },
  { subject: '毛利润' },
  { subject: '净利润' },
  { subject: '总资产' },
  { subject: '总负债' },
  { subject: '资产负债率' },
  { subject: '流动比率' },
  { subject: '速动比率' },
]
export const TABLE_INITIAL_VALUES4 = [
  { subject: '主营业务收入' },
  { subject: '利润总额' },
  { subject: '总资产' },
  { subject: '总负债' },
  { subject: '有息负债' },
  { subject: '资产负债率' },
]
export const TABLE_INITIAL_VALUES3 = [
  ...TABLE_INITIAL_VALUES4,
  { subject: '票款收入' },
  { subject: '财政补贴收入' },
  { subject: '车辆总数' },
  { subject: '线路数量' },
]

function AfterLeaseCheckContent(props, ref) {
  const { contentData = [], title, saveApi, canEdit, initEdit = false, children, active } = props
  useEffect(() => {
    form.resetFields()
  }, [active])
  const dataSource = useMemo(() => {
    return contentData.reduce((prev, { fieldName, fieldValue, attributionList, moduleIndex }) => {
      if (attributionList) {
        if (!prev[attributionList]) prev[attributionList] = []
        const index = moduleIndex - 1
        if (!prev[attributionList][index]) prev[attributionList][index] = {}
        if (['NP_C_3_05_03', 'LR_C_3_05_03'].includes(fieldName)) {
          prev[attributionList][index][fieldName] = fieldValue && JSON.parse(fieldValue)?.fieldValue
        } else {
          prev[attributionList][index][fieldName] = fieldValue
        }
        return prev
      }
      if (
        ['NP_C_2_03_03', 'LR_C_2_03_03', 'P_C_2_07', 'P_C_3_07', 'B_C_2_01', 'SOA_C_2_01'].includes(
          fieldName
        )
      ) {
        prev[fieldName] = fieldValue && JSON.parse(fieldValue)?.fieldValue
        return prev
      }

      prev[fieldName] = fieldValue
      return prev
    }, {})
  }, [contentData])
  useEffect(() => {
    form.setFieldsValue(dataSource)
  }, [dataSource])
  const [form] = Form.useForm()

  const [editable, setEditable] = useState(initEdit)

  const saveData = async (saveType = 'SAVE') => {
    setIsLoading(true)
    try {
      let data
      if (saveType === 'SAVE') {
        data = await form.validateFields().catch((err) => errorFormScroll(err, form))
      } else {
        data = form.getFieldsValue()
      }
      await saveApi?.(data, saveType)
      setEditable(false)
    } catch (err) {
      // message.info('请填写完整数据')
    } finally {
      setIsLoading(false)
    }
  }

  const [isLoading, setIsLoading] = useState(false)
  return (
    <div>
      <div className={styles.js}>
        <div className={styles.title}>{title}</div>
        <Space key="edit">
          {canEdit && editable && (
            <>
              <Button key="cancel" onClick={() => setEditable(false)}>
                取消
              </Button>
              <Button
                type="primary"
                key="draft"
                onClick={() => saveData('DRAFT')}
                loading={isLoading}
              >
                暂存
              </Button>
              <Button type="primary" key="save" onClick={() => saveData()} loading={isLoading}>
                保存
              </Button>
            </>
          )}
          {canEdit && !editable && (
            <Button type="primary" key="edit" onClick={() => setEditable(true)} disabled={!canEdit}>
              编辑
            </Button>
          )}
        </Space>
      </div>
      <Form
        form={form}
        initialValues={{
          NP_C_2_03_03: TABLE_INITIAL_VALUES2,
          LR_C_2_03_03: TABLE_INITIAL_VALUES2,
          B_C_2_01: TABLE_INITIAL_VALUES3,
          SOA_C_2_01: TABLE_INITIAL_VALUES4,
          P_C_2_07: TABLE_INITIAL_VALUES,
          P_C_3_07: TABLE_INITIAL_VALUES,

          guarantor: [],
          naturalPerson: [],
        }}
      >
        {React.cloneElement(children, {
          editable,
        })}
      </Form>
    </div>
  )
}

export default observer(forwardRef(AfterLeaseCheckContent))
