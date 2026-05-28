import IconFont from '@/components/Icon'
import { observer } from '@zswl/admin'
import { Col, Form, Input, Row, Radio } from 'antd'
import { useRef } from 'react'
import styles from './index.less'
import ClientSelect from './ClientSelect'
import { App } from '@zswl/components'
import React, { useEffect, useMemo, useState } from 'react'
import FormAmount from '@/components/Form/FormAmount'
import { amountFormat, formatPercent } from '@/utils'
import Store from '../../store'
import { Table, TableStore } from '@zswl/components'
const FormListItem = ({
  form,
  enumType,
  fields,
  type,
  add,
  remove,
  addText,
  noClientType = false,
  fieldKey,
  required,
  isDebtor,
  businessKey,
  other,
  detail,
  scene = 'other',
}) => {
  const options = App.getData().optionsType
  const { setFieldsValue, setFieldValue, getFieldValue, getValue } = form
  const clientRef = useRef({})
  const ids = detail['id']
  const onClientIdChange = async (val, name) => {
    const store = new Store()
    // 解构出方法
    const { onCustomers } = store
    console.log('form', form)

    console.log('id', ids, detail)
    const data = await onCustomers(businessKey, ids)
    if (data) {
      let dataList = getFieldValue(fieldKey)
      if (type) {
        dataList[name[0]].clientRole = type
      }
      if (dataList[name[0]].isReport === undefined || dataList[name[0]].isReport === null) {
        dataList[name[0]].isReport = 1
      }
      if (
        dataList[name[0]].guaranteeRate === undefined ||
        dataList[name[0]].guaranteeRate === null
      ) {
        dataList[name[0]].guaranteeRate = 1000000
      }
      const selectedOption = data.filter((option) =>
        val.some((item) => {
          return item.value === option.clientName
        })
      )
      // selectedOption = selectedOption.map((item) => { return {label:item.clientName ,...item}})
      dataList[name[0]].clientInfoList = selectedOption.map((item, i) => {
        return { ...item, label: item.clientName, value: item.clientName }
      })
      setFieldValue({
        [fieldKey]: dataList,
      })
    }
  }
  useEffect(() => {
    getFieldValue('')
  })
  return (
    <>
      {
        <>
          {fields?.map(({ key, name, ...restField }, index) => {
            name = name + 1
            key = key + 1
            let items = form.getFieldValue(fieldKey)
            const firstIndex = items.findIndex(
              (item) => item.clientRole && item.clientRole === type
            )
            if (!items[name] || (items[name].clientRole && type !== items[name].clientRole)) {
              return
            }

            return (
              <Row key={name} style={{ display: 'flex', alignItems: 'flex-start' }}>
                <Col span={20}>
                  <Input.Group compact>
                    <Form.Item
                      // label={enumType.label}
                      shouldUpdate
                      style={{ width: noClientType ? '100%' : 'calc(100% - 100px)' }}
                    >
                      {({ getFieldValue }) => {
                        const lesseeInfoDetail = getFieldValue([fieldKey, name])

                        if (fieldKey === 'guaranteeMeasureDetails') {
                          return (
                            <>
                              <Form.Item
                                {...restField}
                                name={[name, 'clientInfoList']}
                                label={'担保人名称'}
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
                              <Form.Item
                                style={{ margin: '5px 0' }}
                                label={'担保比例'}
                                name={[name, 'guaranteeRate']}
                              >
                                <FormAmount
                                  max={100}
                                  min={0}
                                  addonAfter="%"
                                  precision={2}
                                  step="0"
                                ></FormAmount>
                              </Form.Item>
                              <Form.Item
                                style={{ margin: '5px 0' }}
                                label={'是否上报征信'}
                                name={[name, 'isReport']}
                              >
                                <Radio.Group
                                  // defaultValue={1}
                                  options={[
                                    { label: '是', value: 1 },
                                    { label: '否', value: 0 },
                                  ]}
                                ></Radio.Group>
                              </Form.Item>
                            </>
                          )
                        }
                        if (fieldKey === 'pledgeMeasuresDetails') {
                          return (
                            <>
                              {enumType !== options['pledgeMeasuresTypeEnum'][0] && (
                                <Form.Item
                                  {...restField}
                                  name={[name, 'clientInfoList']}
                                  label={
                                    enumType === options['pledgeMeasuresTypeEnum'][1]
                                      ? '质押人名称'
                                      : '抵押人名称'
                                  }
                                  style={{ margin: ' 5px 0' }}
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
                              )}

                              {enumType === options['pledgeMeasuresTypeEnum'][2] && (
                                <Form.Item
                                  style={{ margin: '5px 0' }}
                                  label={'抵押类型'}
                                  name={[name, 'pledgedType']}
                                >
                                  <Radio.Group
                                    options={options['projReviewPledgeTypeEnum']}
                                  ></Radio.Group>
                                </Form.Item>
                              )}
                              <Form.Item
                                style={{ margin: '5px 0' }}
                                label={
                                  enumType === options['pledgeMeasuresTypeEnum'][2]
                                    ? '抵押标的'
                                    : '质押标的'
                                }
                                name={[name, 'pledgedObject']}
                              >
                                <Input placeholder="请输入" maxLength={2500} />
                              </Form.Item>
                              <Form.Item
                                style={{ margin: '5px 0' }}
                                label={
                                  enumType === options['pledgeMeasuresTypeEnum'][2]
                                    ? '是否办理抵押登记'
                                    : enumType === options['pledgeMeasuresTypeEnum'][0]
                                    ? '是否办理质押登记'
                                    : '是否办理中登登记'
                                }
                                name={[name, 'isReport']}
                              >
                                <Radio.Group
                                  options={[
                                    { label: '是', value: 1 },
                                    { label: '否', value: 0 },
                                  ]}
                                ></Radio.Group>
                              </Form.Item>
                            </>
                          )
                        }
                      }}
                    </Form.Item>
                  </Input.Group>
                </Col>
                <Col span={4}>
                  <div className={styles.noWrap}>
                    {index === firstIndex - 1 && (
                      <div className={styles.add} onClick={(key) => add({ clientRole: type })}>
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

                    <div className={styles.add} onClick={() => remove(name)}>
                      <div className={styles.icon}>
                        <IconFont type="icon-icon_delete" />
                      </div>
                    </div>
                  </div>
                </Col>
                {/* } */}
              </Row>
            )
          })}
          {type !== 'OTHER' &&
            form.getFieldValue(fieldKey) &&
            form.getFieldValue(fieldKey).every((item) => item['clientRole'] !== type) && (
              <Row style={{ display: 'flex', alignItems: 'flex-start' }}>
                <Col span={4}>
                  <div className={styles.add} onClick={(key) => add({ clientRole: type })}>
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
      }
    </>
  )
}
FormListItem.Detail = observer(({ values, isDebtor, enumTypeList, fieldKey = '', detail }) => {
  const options = App.getData().optionsType
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
  const columns_guarantee_1 = [
    { title: '担保人名称', dataIndex: 'clientName' },
    { title: '担保比例', dataIndex: 'guaranteeRate', render: (text) => <>{text}%</> },
  ]
  const columns_guarantee_2 = [
    { title: '担保人名称', dataIndex: 'clientName' },
    { title: '担保比例', dataIndex: 'guaranteeRate', render: (text) => <>{text}%</> },
    { title: '是否上报征信', dataIndex: 'isReport' },
  ]
  const columns_pledgeMeasures_1 = [
    { title: '质押标的', dataIndex: 'pledgedObject' },
    { title: '是否办理质押登记', dataIndex: 'isReport' },
  ]
  const columns_pledgeMeasures_2 = [
    { title: '质押人名称', dataIndex: 'clientName' },
    { title: '质押标的', dataIndex: 'pledgedObject' },
    { title: '是否办理中登登记', dataIndex: 'isReport' },
  ]
  const columns_pledgeMeasures_3 = [
    { title: '抵押人名称', dataIndex: 'clientName' },
    { title: '抵押类型', dataIndex: 'pledgedType' },
    { title: '抵押标的', dataIndex: 'pledgedObject' },
    { title: '是否办理抵押登记', dataIndex: 'isReport' },
  ]
  const result = {}
  const result2 = {}
  if (detail) {
    if (detail.guaranteeMeasureDetails) {
      detail.guaranteeMeasureDetails.forEach((item) => {
        const clientRole = item.clientRole || ''
        const clientInfoList = item.clientInfoList || []

        // if (clientRole && clientInfoList) {
        const clientNames = clientInfoList?.map((info) => info.clientName).join(',')

        if (!result[clientRole]) {
          result[clientRole] = []
        }

        result[clientRole].push({
          ...item,
          clientName: clientNames,
          guaranteeRate: item.guaranteeRate / 10000 || 0,
          isReport: item.isReport !== null && (item.isReport === 1 ? '是' : '否'),
        })
        // }
      })
    }

    if (detail.pledgeMeasuresDetails) {
      detail.pledgeMeasuresDetails.forEach((item) => {
        const clientRole = item.clientRole || ''
        const clientInfoList = item.clientInfoList || []
        const clientNames = clientInfoList?.map((info) => info.clientName).join(',')

        if (!result2[clientRole]) {
          result2[clientRole] = []
        }

        result2[clientRole].push({
          ...item,
          clientName: clientNames,
          pledgedType: getKeyOptionsLabelMap('projReviewPledgeTypeEnum')[item.pledgedType],
          guaranteeRate: item.guaranteeRate / 10000 || 0,
          isReport: item.isReport !== null && (item.isReport === 1 ? '是' : '否'),
        })
      })
    }
  }

  return (
    <div className={styles.list}>
      {fieldKey === 'guaranteeMeasureDetails' && values && values.length > 0 && (
        <>
          {
            <>
              <div className={styles.detailTitle}>法人连带责任担保</div>
              <Table
                dataSource={result[options['guaranteeMeasuresTypeEnum'][0].value]}
                columns={columns_guarantee_1}
                pagination={false}
              />
            </>
          }
          {
            <>
              <div className={styles.detailTitle}>自然人连带责任担保</div>
              <Table
                dataSource={result[options['guaranteeMeasuresTypeEnum'][1].value]}
                columns={columns_guarantee_2}
                pagination={false}
              />
            </>
          }
          {values[0].otherMessage && (
            <>
              <Row key={0} className={styles.item}>
                <Col className={styles.padding5} span={18}>
                  <div className={styles.detailTitle}>其他: </div>
                  <div>{values[0].otherMessage || '-'}</div>
                </Col>
              </Row>
            </>
          )}
        </>
      )}
      {fieldKey === 'pledgeMeasuresDetails' && values && values.length > 0 && (
        <>
          {
            <>
              <div className={styles.detailTitle}>股权质押</div>
              <Table
                dataSource={result2[options['pledgeMeasuresTypeEnum'][0].value]}
                columns={columns_pledgeMeasures_1}
                pagination={false}
              />
            </>
          }
          {
            <>
              <div className={styles.detailTitle}>应收账款质押</div>
              <Table
                dataSource={result2[options['pledgeMeasuresTypeEnum'][1].value]}
                columns={columns_pledgeMeasures_2}
                pagination={false}
              />
            </>
          }
          {
            <>
              <div className={styles.detailTitle}>抵押担保</div>
              <Table
                dataSource={result2[options['pledgeMeasuresTypeEnum'][2].value]}
                columns={columns_pledgeMeasures_3}
                pagination={false}
              />
            </>
          }
          {values[0].otherMessage && (
            <>
              {' '}
              <Row key={0} className={styles.item}>
                <Col className={styles.padding5} span={18}>
                  <div className={styles.detailTitle}>其他: </div>
                  <div>{values[0].otherMessage || '-'}</div>
                </Col>
              </Row>
            </>
          )}
        </>
      )}
    </div>
  )
})
export default observer(FormListItem)
