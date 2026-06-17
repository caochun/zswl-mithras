import IconFont from '@/components/Icon'
import { observer } from '@zswl/admin'
import { Col, Form, Input, Row, Radio } from 'antd'
import { useRef, useEffect, useMemo, useState } from 'react'
import Api from '../../api'
import styles from './index.less'
import ClientSelect from './ClientSelect'
import { App } from '@zswl/components'
import Store from '../../store'

const FormListItem = ({
  form,
  fields,
  add,
  remove,
  addText,
  noClientType = false,
  fieldKey,
  required,
  businessKey,
  detail,
  scene = 'other',
}) => {
  const options = App.getData().optionsType
  let Rate = JSON.parse(JSON.stringify(options['resolutionTypeRateEnum']))
  Rate[3].label = '质押担保  的有效决议文件或审批文件'
  Rate.pop()
  const { setFieldsValue, setFieldValue, getFieldValue, getValue } = form
  const clientRef = useRef({})
  const ids = detail['id']
  const onClientIdChange = async (val, name) => {
    const store = new Store()
    // 解构出方法
    const { onCustomers } = store

    const data = await onCustomers(businessKey, ids)
    if (data) {
      let dataList = getFieldValue(fieldKey)
      data.forEach((v) => {
        if (dataList[name[0]].clientName === v.clientId) {
          dataList[name[0]].clientName = v.clientName
          dataList[name[0]].clientType = v.clientType
          dataList[name[0]].clientId = v.clientId
        }
      })
      setFieldsValue({
        [fields]: dataList,
      })
    }
  }

  return (
    <>
      {fields?.map(({ key, name, ...restField }, index) => {
        if (fieldKey === 'resolutionInfoDetails' && name === 0) {
          return
        }
        return (
          <Row
            className={styles.FormListItemc}
            key={name}
            style={{ display: 'flex', alignItems: 'center' }}
          >
            <Col span={20}>
              <Input.Group compact>
                <Form.Item
                  rules={[{ required, message: '请选择!' }]}
                  shouldUpdate
                  style={{ width: noClientType ? '100%' : 'calc(100% - 100px)' }}
                >
                  {({ getFieldValue }) => {
                    const lesseeInfoDetail = getFieldValue([fieldKey, name])
                    if (fieldKey === 'lesseeInfoDetail') {
                      return (
                        <Form.Item
                          {...restField}
                          name={[name, 'clientName']}
                          rules={[{ required, message: '请选择!' }]}
                        >
                          <ClientSelect
                            allowClear
                            placeholder="请选择!"
                            ref={(ref) => {
                              clientRef.current[key] = ref
                            }}
                            onChange={(val) => {
                              onClientIdChange(val, [name, fieldKey])
                            }}
                            queryParams={{
                              lesseeInfoDetail,
                              fieldKey,
                              scene,
                              businessKey,
                              id: ids,
                            }}
                          />
                        </Form.Item>
                      )
                    }
                    if (fieldKey === 'resolutionInfoDetails') {
                      return (
                        <div style={{ marginBottom: '8px' }}>
                          {/* justifyContent: 'flex-end' */}
                          <div style={{ display: 'flex', flexWrap: 'wrap', marginBottom: '8px' }}>
                            <Form.Item
                              {...restField}
                              name={[name, 'clientName']}
                              rules={[{ required, message: '请选择!' }]}
                            >
                              <ClientSelect
                                allowClear
                                style={{ minWidth: '300px' }}
                                placeholder="请选择!"
                                ref={(ref) => {
                                  clientRef.current[key] = ref
                                }}
                                onChange={(val) => {
                                  onClientIdChange(val, [name, fieldKey])
                                }}
                                queryParams={{
                                  lesseeInfoDetail,
                                  fieldKey,
                                  scene,
                                  businessKey,
                                  id: ids,
                                }}
                              />
                            </Form.Item>
                            <span style={{ lineHeight: 2.3, margin: '0 15px' }}>
                              同意本项目关于{' '}
                            </span>
                          </div>
                          <Form.Item
                            {...restField}
                            name={[name, 'resolutionTypeRateEnum']}
                            rules={[{ required, message: '请选择!' }]}
                          >
                            <Radio.Group options={Rate}></Radio.Group>
                          </Form.Item>
                        </div>
                      )
                    }
                  }}
                </Form.Item>
              </Input.Group>
            </Col>
            <Col span={4}>
              <div className={styles.noWrap}>
                {((fieldKey !== 'resolutionInfoDetails' && index === 0) ||
                  (fieldKey === 'resolutionInfoDetails' && index === 1)) && (
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
                )}
                {((index !== 0 && fieldKey !== 'resolutionInfoDetails') ||
                  fieldKey === 'resolutionInfoDetails') && (
                  <div className={styles.add} onClick={() => remove(name)}>
                    <div className={styles.icon}>
                      <IconFont type="icon-icon_delete" />
                    </div>
                  </div>
                )}
              </div>
            </Col>
          </Row>
        )
      })}
      {((fieldKey !== 'resolutionInfoDetails' && fields.length === 0) ||
        (fieldKey === 'resolutionInfoDetails' && fields.length <= 1)) && (
        <Row style={{ display: 'flex', alignItems: 'flex-start' }}>
          <Col span={4}>
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
          </Col>
        </Row>
      )}
    </>
  )
}
FormListItem.Detail = observer(({ values, isDebtor, fieldKey = '' }) => {
  const options = App.getData().optionsType
  let Rate = JSON.parse(JSON.stringify(options['resolutionTypeRateEnum']))
  Rate.pop()
  const getKeyOptionsLabelMap = (key) => {
    const obj = {}
    if (options && options[key]) {
      options[key].forEach((item) => {
        const { label, value } = item
        obj[value] = label
      })
    }
    return obj
  }
  return (
    <div className={styles.list}>
      {values?.map((item, index) => {
        const { clientName = '', resolutionTypeRateEnum = '' } = item || {}
        if (fieldKey !== 'resolutionInfoDetails') {
          return <div key={index}>{clientName}</div>
        }
        if (!clientName) {
          return
        }
        return (
          <Row key={index} className={styles.item}>
            <Col className={styles.padding5} span={18} style={{ marginLeft: '-8px' }}>
              {fieldKey === 'resolutionInfoDetails' && clientName && (
                <>
                  <span>{clientName}同意本项目关于</span>
                  <span>
                    {Rate.map((item) => item.value === resolutionTypeRateEnum && item.label)}
                  </span>
                  <span>的有效决议文件或审批文件</span>
                </>
              )}
            </Col>
          </Row>
        )
      })}
      {fieldKey === 'resolutionInfoDetails' && values[0].otherMessage && (
        <Row key={0} className={styles.item}>
          <Col className={styles.padding5} span={18} style={{ marginLeft: '-8px' }}>
            <span className={styles.label}>其他：{values[0].otherMessage}</span>
          </Col>
        </Row>
      )}
    </div>
  )
})
export default observer(FormListItem)
