import IconFont from '@/components/Icon'
import { amountFormat, getInputNumberAmountProps, getKeyOptionsLabelMap, hasValue } from '@/utils'
import { history, observer } from '@zswl/admin'
import { Col, Form, Input, InputNumber, Row, Select, Tooltip } from 'antd'
import { useRef } from 'react'
import Api from '../api'
import styles from './index.less'
import ClientSelect from '../ClientSelect'
import { App } from '@zswl/components'
import classNames from 'classnames'
import { BlackInfo } from '@/components/BlackInfo/BlackInfoEntries'

export const toDetail = (id, clientType) => {
  if (id) {
    history.push(`/customer/maintain/detail/${id}?clientType=${clientType}&flag=info&typeId=create`)
  }
}
const debtorTypeOptions = [
  { label: '法人', value: 'CORPORATION' },
  { label: '非法人', value: 'NO-CORPORATION' },
]
const FormListItem = ({
  fields,
  add,
  remove,
  addText,
  noClientType = false,
  fieldKey,
  required,
  isDebtor,
  scene = 'other',
}) => {
  const options = App.getData().optionsType
  const form = Form.useFormInstance()

  const { setFieldsValue, setFieldValue, getFieldValue: getValue } = form
  const clientRef = useRef({})
  const onClientIdChange = async (val, name) => {
    if (!val?.value) {
      setFieldsValue({
        name: undefined,
      })
      return
    }
    const data = await Api.postProjectBaseInfoExposure({ clientId: val.value })
    if (data) {
      const { stockRiskExposure } = data
      const value = getValue(name.slice(0, 1))
      const itemValue = value[name[1]]
      itemValue.stockRiskExposure = stockRiskExposure / 10000
      itemValue.clientName = val.label
      value[name[1]] = itemValue

      setFieldsValue({
        [name[0]]: value,
      })
    }
  }
  const onClientTypeChange = async (val, name, key) => {
    const value = getValue(name.slice(0, 1))
    const itemValue = value[name[1]]
    itemValue.clientId = undefined
    itemValue.stockRiskExposure = undefined
    itemValue.clientName = undefined
    value[name[1]] = itemValue
    if (clientRef.current[key]) {
      clientRef.current[key].searchClient('', { clientType: val })
    }
    setFieldsValue({
      [name[0]]: value,
    })
  }

  return (
    <>
      {fields?.map(({ key, name, ...restField }, index) => {
        return (
          <Row key={key} style={{ display: 'flex', alignItems: 'flex-start', marginBottom: '8px' }}>
            <Col span={9}>
              <Input.Group compact>
                {!noClientType && (
                  <Form.Item
                    {...restField}
                    name={[name, 'clientType']}
                    style={{ width: '100px' }}
                    rules={[{ required, message: '请选择!' }]}
                  >
                    <Select
                      allowClear
                      placeholder="请选择!"
                      onChange={(val) => onClientTypeChange(val, [fieldKey, name, 'clientId'], key)}
                      options={isDebtor ? debtorTypeOptions : options?.clientType}
                    />
                  </Form.Item>
                )}
                <Form.Item
                  rules={[{ required, message: '请选择!' }]}
                  shouldUpdate
                  style={{ width: noClientType ? '100%' : 'calc(100% - 100px)' }}
                >
                  {({ getFieldValue }) => {
                    const clientType =
                      getFieldValue([fieldKey, name, 'clientType']) || 'CORPORATION'
                    if (clientType === 'NO-CORPORATION') {
                      return (
                        <Form.Item
                          {...restField}
                          name={[name, 'clientName']}
                          rules={[{ required, message: '请选择!' }]}
                        >
                          <Input placeholder="请输入!" style={{ width: '100%' }} />
                        </Form.Item>
                      )
                    }
                    return (
                      <Form.Item
                        {...restField}
                        name={[name, 'clientId']}
                        rules={[{ required, message: '请选择!' }]}
                      >
                        <ClientSelect
                          allowClear
                          placeholder="请选择!"
                          ref={(ref) => {
                            clientRef.current[key] = ref
                          }}
                          onChange={(val) =>
                            onClientIdChange(val, [fieldKey, name, 'stockRiskExposure'])
                          }
                          queryParams={{
                            clientType,
                            scene,
                          }}
                        />
                      </Form.Item>
                    )
                  }}
                </Form.Item>
              </Input.Group>
            </Col>
            <Form.Item shouldUpdate>
              {({ getFieldValue, getFieldsValue }) => {
                const clientType = getFieldValue([fieldKey, name, 'clientType'])
                let clientTypes = clientType || 'CORPORATION'
                return (
                  <div
                    className={styles.icon}
                    style={{
                      marginRight: 20,
                      opacity: clientType === 'NO-CORPORATION' ? 0 : 1,
                    }}
                  >
                    <IconFont
                      type="icon-icon_link"
                      onClick={() => {
                        if (clientType === 'NO-CORPORATION') {
                          return
                        }
                        const { value } = getFieldValue([fieldKey, name, 'clientId'])
                        toDetail(value, clientTypes)
                      }}
                    />
                  </div>
                )
              }}
            </Form.Item>

            <Col span={4} style={{ marginRight: '15px' }}>
              <div className={styles.label}>存量风险敞口(元)</div>
            </Col>
            <Col span={4} style={{ marginRight: '10px', marginLeft: '3%' }}>
              <Form.Item shouldUpdate>
                {({ getFieldValue, getFieldsValue }) => {
                  const clientType = getFieldValue([fieldKey, name, 'clientType'])
                  if (clientType === 'NORMAL' || clientType === 'NO-CORPORATION') {
                    return '-'
                  }
                  return (
                    <Form.Item {...restField} name={[name, 'stockRiskExposure']}>
                      <InputNumber
                        {...getInputNumberAmountProps()}
                        disabled={true}
                        style={{ width: 130 }}
                      />
                    </Form.Item>
                  )
                }}
              </Form.Item>
            </Col>
            <Col span={4}>
              {index === 0 ? (
                <div className={styles.add} onClick={() => add()}>
                  <div
                    className={styles.icon}
                    style={{
                      marginRight: 8,
                    }}
                  >
                    <IconFont type="icon-icon_add" />
                  </div>
                  {addText}
                </div>
              ) : (
                <div className={styles.add} onClick={() => remove(name)}>
                  <div className={styles.icon}>
                    <IconFont type="icon-icon_delete" />
                  </div>
                </div>
              )}
            </Col>
          </Row>
        )
      })}
    </>
  )
}
FormListItem.Detail = observer(({ values, isDebtor }) => {
  const options = App.getData().optionsType
  return (
    <div className={styles.list}>
      {values?.map((item, index) => {
        const { clientId, clientName, stockRiskExposure, clientType } = item
        let clientTypes = clientType || 'CORPORATION'
        const clientTypeStr =
          isDebtor && clientType === 'NO-CORPORATION'
            ? '非法人'
            : getKeyOptionsLabelMap('clientType', options)[clientType]
        return (
          <Row key={index} className={styles.item}>
            <Col span={13}>
              {clientId || clientTypeStr ? (
                <div style={{ display: 'flex', alignItems: 'center' }}>
                  <Tooltip title={clientName}>
                    <a
                      style={{
                        color: clientType === 'NO-CORPORATION' ? 'rgba(0, 0, 0, 0.85)' : undefined,
                      }}
                      onClick={() => {
                        if (clientType === 'NO-CORPORATION') {
                          return
                        }
                        toDetail(clientId?.value ?? clientId, clientTypes)
                      }}
                      className={styles.name}
                    >
                      {clientName}
                    </a>
                  </Tooltip>
                  {clientTypeStr ? (
                    <div
                      className={classNames(styles.type, {
                        [styles.type1]: clientType === 'NORMAL',
                        [styles.type2]: clientType === 'NO-CORPORATION',
                      })}
                    >
                      {clientTypeStr}
                    </div>
                  ) : (
                    ''
                  )}
                  <BlackInfo
                    params={{ clientId: clientId?.value ?? clientId }}
                    style={{ marginLeft: 4 }}
                  />
                </div>
              ) : (
                '-'
              )}
            </Col>
            <Col span={5} style={{ marginLeft: '-8px' }}>
              <div className={styles.label}>存量风险敞口(元)</div>
            </Col>
            <Col span={6} style={{ marginLeft: '8px' }}>
              <div className={styles.value}>
                {hasValue(stockRiskExposure) ? amountFormat(stockRiskExposure) : '-'}
              </div>
            </Col>
          </Row>
        )
      })}
    </div>
  )
})
export default observer(FormListItem)
