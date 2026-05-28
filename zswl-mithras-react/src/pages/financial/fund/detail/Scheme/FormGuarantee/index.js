import { Form, Select } from '@zswl/components'
import { amountFormat, formatPercent } from '@/utils'
import IconFont from '@/components/Icon'
import { Col, Input, Row } from 'antd'
import styles from './index.less'
import { history, observer } from '@zswl/admin'
import _ from 'lodash'
import Api from '@/api/financial/orgManage'
import { useEffect, useState } from 'react'
import FormAmount from '@/components/Form/FormAmount'

function Index(props) {
  const { listName, addText = '请添加担保方', value, orgList, isYT, baseInfoDetail, rules } = props
  const [init, setInit] = useState(true)
  useEffect(() => {
    setInit(true)
  }, [])
  return (
    <Form.List name={listName} initialValue={value} rules={rules}>
      {(fields, { add, remove }) => {
        return (
          <FormListItem
            fields={fields}
            fieldKey={listName}
            add={add}
            initialValue={value}
            orgList={orgList}
            remove={remove}
            baseInfoDetail={baseInfoDetail}
            isYT={isYT}
            addText={addText}
          />
        )
      }}
    </Form.List>
  )
}

const RowRender = ({
  field,
  required,
  fieldKey,
  orgList,
  initialValue,
  addButton,
  isYT,
  remove,
}) => {
  const { key, name, ...restField } = field

  const form = Form.useFormInstance()
  useEffect(() => {
    getList({
      organizationId: isYT
        ? initialValue?.[field.name]?.organizationId
        : orgList?.[0]?.organizationId,
    })
  }, [])
  const [list, setList] = useState([])
  const getList = _.debounce(async (val) => {
    if (!val?.organizationId) return setList([])
    const res = await Api.postAgencyPulldown(val)
    setList(res)
  }, 500)

  const orgChange = async (val, name, option) => {
    form.setFieldValue([fieldKey, name, 'organizationName'], option?.organizationName)
    form.setFieldValue([fieldKey, name, 'guaranteeAgencyId'], null)
    form.setFieldValue([fieldKey, name, 'guaranteeAgencyName'], null)
    form.setFieldValue([fieldKey, name, 'guaranteeAmount'], null)
    setTimeout(() => {
      getList({ organizationId: val })
    }, 200)
  }
  const onGuaranteeChange = (val, name, option) => {
    form.setFieldValue([fieldKey, name, 'guaranteeAgencyName'], option.guaranteeAgencyName)
  }

  return (
    <Row
      key={key}
      gutter={12}
      style={{ display: 'flex', alignItems: 'center', marginBottom: '8px' }}
    >
      <Col span={isYT ? 6 : 0}>
        <Form.Item
          {...restField}
          name={[name, 'organizationId']}
          rules={[{ required, message: '请选择!' }]}
          style={{ margin: 0 }}
          hidden={!isYT}
        >
          <Select
            allowClear={true}
            onChange={(val, option) => orgChange(val, name, option)}
            options={orgList}
            style={{ width: '100%' }}
            fieldNames={{ label: 'organizationName', value: 'organizationId' }}
            placeholder="请选择！"
          />
        </Form.Item>
        <Form.Item
          hidden
          {...restField}
          name={[name, 'organizationName']}
          rules={[{ required, message: '请选择!' }]}
          style={{ margin: 0 }}
        >
          <Input allowClear={false} placeholder="请选择！" />
        </Form.Item>
      </Col>

      <Col span={isYT ? 6 : 8}>
        <Form.Item
          {...restField}
          name={[name, 'guaranteeAgencyId']}
          rules={[{ required, message: '请选择!' }]}
          style={{ margin: 0 }}
        >
          <Select
            onChange={(val, option) => onGuaranteeChange(val, name, option)}
            allowClear={false}
            options={list}
            style={{ width: '100%' }}
            placeholder="请选择！"
            fieldNames={{ label: 'guaranteeAgencyName', value: 'id' }}
            filterOption={(input, option) => {
              // 搜索
              return option.guaranteeAgencyName.indexOf(input) >= 0
            }}
          />
        </Form.Item>
        <Form.Item {...restField} name={[name, 'guaranteeAgencyName']} hidden>
          <Input />
        </Form.Item>
      </Col>
      <Col span={isYT ? 6 : 12} style={{ display: 'flex', marginLeft: 12 }}>
        <div className={styles.label}>担保金额</div>
        <Form.Item {...restField} name={[name, 'guaranteeAmount']} style={{ margin: 0 }}>
          <FormAmount style={{ width: '100%' }} min={0} addonAfter="元" />
        </Form.Item>
      </Col>

      <Col>
        <Row>
          <div className={styles.add} onClick={() => remove(name)}>
            <div className={styles.icon}>
              <IconFont type="icon-icon_delete" />
            </div>
          </div>
        </Row>
      </Col>
    </Row>
  )
}
const FormListItem = ({
  fields,
  add,
  remove,
  addText,
  required,
  fieldKey,
  orgList,
  initialValue,
  isYT,
  baseInfoDetail,
}) => {
  const defaultValue = isYT
    ? {}
    : {
        organizationId: baseInfoDetail?.organizationId?.[0],
        organizationName: baseInfoDetail?.organizationName?.[0],
      }

  const addButton = (
    <div className={styles.add} onClick={() => add(defaultValue)}>
      <div className={styles.icon} style={{ marginRight: 8 }}>
        <IconFont type="icon-icon_add" />
      </div>
      {addText}
    </div>
  )
  const newOrgList = isYT
    ? orgList ?? []
    : [
        {
          organizationId: baseInfoDetail?.organizationId?.[0],
          organizationName: baseInfoDetail?.organizationName?.[0],
        },
      ]
  console.log('newOrgList: ', newOrgList)

  return (
    <div style={{ width: '100%' }}>
      {fields?.map((field, index) => {
        return (
          <RowRender
            remove={remove}
            required={required}
            field={field}
            fieldKey={fieldKey}
            orgList={newOrgList}
            initialValue={initialValue}
            addButton={addButton}
            isYT={isYT}
          />
        )
      })}
      {addButton}
    </div>
  )
}

Index.Detail = ({ value, isYT }) => {
  const toDetail = (id) => {
    history.push(`/financial/guarantee/detail/${id}`)
  }
  return (
    <div style={{ display: 'block', width: '100%' }}>
      {value?.length > 0
        ? value?.map((v, index) => {
            return (
              <Row gutter={12} key={index} style={{ width: '100%', alignItems: 'center' }}>
                {isYT && (
                  <Col span={isYT ? 6 : 8}>
                    <span className={styles.itemLabel}>融资机构：</span>
                    {v.organizationName}
                  </Col>
                )}

                <Col span={isYT ? 6 : 8}>
                  <a type="link" onClick={() => toDetail(v.guaranteeAgencyId)}>
                    {v.guaranteeAgencyName}
                  </a>
                </Col>
                <Col span={isYT ? 6 : 8}>
                  <span className={styles.itemLabel}>担保金额(元)：</span>
                  {amountFormat(formatPercent(v.guaranteeAmount))}
                </Col>
              </Row>
            )
          })
        : '无'}
    </div>
  )
}
export default observer(Index)
