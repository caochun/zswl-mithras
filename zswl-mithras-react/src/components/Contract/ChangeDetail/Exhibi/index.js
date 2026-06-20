import { FormItemContent, StarDom } from '@/components/Form'
import { useEffect } from 'react'
import { observer } from '@zswl/admin'
import { Form } from '@zswl/components'
import { Descriptions, Input, InputNumber } from 'antd'
import { amountStrToNumber, getInputNumberValueFromEvent, getInputNumberMonthProps } from '@/utils'
import mathjs from '@/utils/math'
import { bizTypeMap } from './bizTypeConfig'
import styles from './index.less'

function Index({ detail = {}, oldDetail = {}, store = {} }) {
  const { bizType } = store.page.getParams()
  const { showVal: showValue } = store
  const { form } = store

  const curBizTypeConfig = bizTypeMap[bizType] || {}

  const getDetailValue = (key, isOld) => {
    return isOld ? oldDetail[key] : detail[key]
  }

  useEffect(() => {
    if (detail) {
      let leaseMonthCountAdd = detail[curBizTypeConfig.name] - oldDetail[curBizTypeConfig.name]
      form.setFieldsValue({
        leaseMonthCount: detail[curBizTypeConfig.name],
        leaseMonthCountAdd: leaseMonthCountAdd > 0 ? leaseMonthCountAdd : undefined,
      })
    }
    if (oldDetail) {
      form.setFieldsValue({
        old_leaseMonthCount: oldDetail[curBizTypeConfig.name],
      })
    }
  }, [detail, oldDetail, form])

  return (
    <div>
      <Descriptions
        title="展期方案"
        bordered
        column={2}
        labelStyle={{ background: '#F5F6FA' }}
        size={'small'}
        className={styles.des}
      >
        {showValue ? (
          <>
            <Descriptions.Item
              label={<StarDom name={'原' + curBizTypeConfig.title}></StarDom>}
              span={2}
            >
              {getDetailValue(curBizTypeConfig.name, true)}
            </Descriptions.Item>
            <Descriptions.Item
              label={<StarDom name={'新' + curBizTypeConfig.title}></StarDom>}
              span={2}
            >
              {getDetailValue(curBizTypeConfig.name)}
            </Descriptions.Item>
          </>
        ) : (
          <>
            <Descriptions.Item label={'原' + curBizTypeConfig.title}>
              <FormItemContent
                formContent={
                  <div>
                    <Form.Item
                      name="old_leaseMonthCount"
                      getValueFromEvent={getInputNumberValueFromEvent}
                    >
                      <Input style={{ width: '100%' }} placeholder="请输入" disabled />
                    </Form.Item>
                  </div>
                }
                value={getDetailValue('leaseMonthCount', true)}
                showValue={showValue}
              />
            </Descriptions.Item>
            <Descriptions.Item label={<StarDom name="展期月数"></StarDom>}>
              <FormItemContent
                formContent={
                  <div>
                    <Form.Item
                      name="leaseMonthCountAdd"
                      rules={[
                        {
                          required: true,
                          message: '请输入展期月数！',
                        },
                      ]}
                    >
                      <InputNumber
                        style={{ width: '100%' }}
                        {...getInputNumberMonthProps()}
                        placeholder="请输入"
                      />
                    </Form.Item>
                  </div>
                }
                value={getDetailValue('leaseMonthCountAdd')}
                showValue={showValue}
              />
            </Descriptions.Item>
            <Descriptions.Item label={'新' + curBizTypeConfig.title}>
              <Form.Item dependencies={['old_leaseMonthCount', 'leaseMonthCountAdd']} noStyle>
                {({ getFieldValue }) => {
                  const caclFn = (key, isCurrent) => {
                    return isCurrent
                      ? amountStrToNumber(getFieldValue(key)) || 0
                      : amountStrToNumber(getDetailValue(key)) || 0
                  }
                  return (
                    <FormItemContent
                      formContent={
                        <div>
                          <Form.Item
                            style={{ width: '100%' }}
                            getValueFromEvent={getInputNumberValueFromEvent}
                          >
                            <Input
                              disabled
                              style={{ width: '100%' }}
                              placeholder="请输入"
                              value={mathjs.format(
                                mathjs.add(
                                  caclFn('old_leaseMonthCount', true),
                                  caclFn('leaseMonthCountAdd', true)
                                )
                              )}
                            />
                          </Form.Item>
                        </div>
                      }
                      value={mathjs.format(
                        mathjs.add(caclFn('old_leaseMonthCount'), caclFn('leaseMonthCountAdd'))
                      )}
                      showValue={showValue}
                    />
                  )
                }}
              </Form.Item>
            </Descriptions.Item>
            <Descriptions.Item></Descriptions.Item>
          </>
        )}
      </Descriptions>
    </div>
  )
}

export default observer(Index)
