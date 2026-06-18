import { Form, Select, App } from '@zswl/components'
import { Col, Row, Input } from 'antd'

import mathjs from '@/utils/math'
import { hasValue } from '@/utils'
import Api from '@/api/financial/fundApi'
import styles from './index.less'
import { useEffect } from 'react'

const { Item } = Form

const YEARRETE = {
  rateType: { name: 'rateType', disabled: false },
  lprType: { name: 'lprType', disabled: false },
  lprPercent: { name: 'lprPercent', disabled: false },
  lprAddPercent: { name: 'lprAddPercent', disabled: false },
}

const Index = ({ data, config = YEARRETE, needUpdate = true }) => {
  const rateType = config.rateType
  const lprType = config.lprType
  const lprPercent = config.lprPercent
  const lprAddPercent = config.lprAddPercent

  const form = Form.useFormInstance()
  const onLprTypeChange = async (val, name) => {
    const LPRMap = {
      ONE_YEAR: 'oneYear',
      FIVE_YEAR: 'fiveYear',
    }
    const res = await Api.getLprLast()
    form.setFieldsValue({
      [name]: res[LPRMap[val]] * 10000,
    })
  }

  useEffect(() => {
    needUpdate &&
      form.setFieldsValue({
        [rateType.name]: data?.[rateType.name],
        [lprType.name]: data?.[lprType.name],
        [lprPercent.name]: data?.[lprPercent.name],
        [lprAddPercent.name]: data?.[lprAddPercent.name],
      })
  }, [form, data, needUpdate])

  const validateLimit = (r, value) => {
    if (hasValue(value)) {
      if (value < 0) {
        return Promise.reject(`输入值不能小于0`)
      }
    }
    return Promise.resolve()
  }

  return (
    <div className={styles.rowWrap}>
      <Row gutter={12} align="middle">
        <Col span={4}>
          <Item
            // initialValue={data?.[rateType.name]}
            name={rateType.name}
            rules={[{ required: true, message: '请选择!' }]}
          >
            <Select options="rateType" placeholder="利率类型" disabled={rateType.disabled}></Select>
          </Item>
        </Col>
        <Col span={4}>
          <Item
            // initialValue={data?.[lprType.name]}
            name={lprType.name}
            rules={[{ required: true, message: '请选择!' }]}
          >
            <Select
              options="LPRTypeEnum"
              placeholder="LPR品种"
              disabled={lprType.disabled}
              onChange={(v) => onLprTypeChange(v, lprPercent.name)}
            ></Select>
          </Item>
        </Col>
        <Col span={4}>
          <Item
            // initialValue={data?.[lprPercent.name]}
            name={lprPercent.name}
            rules={[
              { required: true, message: '请输入!' },
              {
                validator: validateLimit,
              },
            ]}
          >
            <FormAmount
              style={{ width: '100%' }}
              min={-9999}
              addonAfter={'%'}
              placeholder="LPR利率"
              step="0.01"
              disabled={lprPercent.disabled}
            />
          </Item>
        </Col>
        <Col span={1}>
          <Item>+</Item>
        </Col>
        <Col span={4}>
          <Item
            // initialValue={data?.[lprAddPercent.name]}
            name={lprAddPercent.name}
            rules={[{ required: true, message: '请输入!' }]}
          >
            <FormAmount
              style={{ width: '100%' }}
              min={-9999}
              addonAfter={'%'}
              placeholder="加点利率"
              step="0.01"
              disabled={lprAddPercent.disabled}
            />
          </Item>
        </Col>
        <Col span={1}>
          <Item>=</Item>
        </Col>
        <Col span={4}>
          <Item noStyle dependencies={[lprPercent.name, lprAddPercent.name]}>
            {({ getFieldValue }) => {
              const lprPercent2 = getFieldValue(lprPercent.name)
              const lprAdd2 = getFieldValue(lprAddPercent.name)
              const total = mathjs.format(mathjs.add(lprPercent2, lprAdd2))
              return (
                <Form.Item>
                  <FormAmount style={{ width: '100%' }} value={total} disabled addonAfter={'%'} />
                </Form.Item>
              )
            }}
          </Item>
        </Col>
      </Row>
    </div>
  )
}

Index.Detail = ({ data, config = YEARRETE, isLog }) => {
  const getChangeStyle = (dataIndex) => {
    if (isLog && Object.keys(isLog).includes(dataIndex)) {
      return { color: 'red' }
    }
    return {}
  }
  const rateType = data[config.rateType.name]
  const lprType = data[config.lprType.name]
  const lprPercent = data[config.lprPercent.name] / 10000
  const lprAddPercent = data[config.lprAddPercent.name] / 10000
  if (!lprType || !rateType) return '-'
  const totalRate = mathjs.format(mathjs.add(lprPercent, lprAddPercent))
  return (
    <div style={{ display: 'block', width: '100%' }}>
      <span style={getChangeStyle('rateType')}>{App.matchOption('rateType', rateType).label}</span>
      ，
      <span>
        <span style={{ ...getChangeStyle('lprPercent'), ...getChangeStyle('lprAddPercent') }}>
          {totalRate}%
        </span>
        <span>
          <span>（</span>
          <span style={getChangeStyle('lprType')}>
            LPR品种：{App.matchOption('LPRTypeEnum', lprType).label}
          </span>
          ，
          <span style={getChangeStyle('lprPercent')}>
            LPR利率：{hasValue(lprPercent) ? lprPercent + '%' : '-'}
          </span>
          ，
          <span style={getChangeStyle('lprAddPercent')}>
            加点利率：{hasValue(lprAddPercent) ? lprAddPercent + '%' : '-'}
          </span>
          <span>）</span>
        </span>
      </span>
    </div>
  )
}

export default Index
