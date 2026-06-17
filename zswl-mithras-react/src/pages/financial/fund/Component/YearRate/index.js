import { Form, Select, App } from '@zswl/components'
import { Col, Row } from 'antd'
import FormAmount from '@/components/Form/FormAmount'
import mathjs from '@/utils/math'
import { hasValue } from '@/utils'
import Api from '@/api/financial/fundApi'
import styles from './index.less'

const { Item } = Form

const YEARRETE = {
  interestRateType: { name: 'interestRateType', disabled: false },
  lprType: { name: 'lprType', disabled: false },
  lprRatePercent: { name: 'lprRatePercent', disabled: false },
  lprAddPercent: { name: 'lprAddPercent', disabled: false },
}

const Index = ({ data, config = YEARRETE, onRateTypeChange }) => {
  const interestRateType = config.interestRateType
  const lprType = config.lprType
  const lprRatePercent = config.lprRatePercent
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
            initialValue={data?.[interestRateType.name]}
            name={interestRateType.name}
            rules={[{ required: true, message: '请选择!' }]}
          >
            <Select
              onChange={(value) => {
                onRateTypeChange?.(value)
              }}
              options="rateType"
              placeholder="利率类型"
              disabled={interestRateType.disabled}
            ></Select>
          </Item>
        </Col>
        <Col span={4}>
          <Item
            initialValue={data?.[lprType.name]}
            name={lprType.name}
            rules={[{ required: true, message: '请选择!' }]}
          >
            <Select
              options="LPRTypeEnum"
              placeholder="LPR品种"
              disabled={lprType.disabled}
              onChange={(v) => onLprTypeChange(v, lprRatePercent.name)}
            ></Select>
          </Item>
        </Col>
        <Col span={4}>
          <Item
            initialValue={data?.[lprRatePercent.name]}
            name={lprRatePercent.name}
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
              step={0.01}
              placeholder="LPR利率"
              disabled={lprRatePercent.disabled}
            />
          </Item>
        </Col>
        <Col span={1}>
          <Item>+</Item>
        </Col>
        <Col span={4}>
          <Item
            initialValue={data?.[lprAddPercent.name]}
            name={lprAddPercent.name}
            rules={[{ required: true, message: '请输入!' }]}
          >
            <FormAmount
              style={{ width: '100%' }}
              min={-9999}
              addonAfter={'%'}
              placeholder="加点利率"
              step={0.01}
              disabled={lprAddPercent.disabled}
            />
          </Item>
        </Col>
        <Col span={1}>
          <Item>=</Item>
        </Col>
        <Col span={4}>
          <Item noStyle dependencies={[lprRatePercent.name, lprAddPercent.name]}>
            {({ getFieldValue }) => {
              const lprPercent = getFieldValue(lprRatePercent.name)
              const lprAdd = getFieldValue(lprAddPercent.name)
              const total = mathjs.format(mathjs.add(lprPercent, lprAdd))
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
  const interestRateType = data[config.interestRateType.name]
  const lprType = data[config.lprType.name]
  const lprRatePercent = data[config.lprRatePercent.name] / 10000
  const lprAddPercent = data[config.lprAddPercent.name] / 10000
  if (!lprType || !interestRateType) return '-'
  const totalRate = mathjs.format(mathjs.add(lprRatePercent, lprAddPercent))
  return (
    <div style={{ display: 'block', width: '100%' }}>
      <span style={getChangeStyle('interestRateType')}>
        {App.matchOption('rateType', interestRateType).label}
      </span>
      ，
      <span>
        <span style={{ ...getChangeStyle('lprRatePercent'), ...getChangeStyle('lprAddPercent') }}>
          {totalRate}%
        </span>
        <span>
          <span>（</span>
          <span style={getChangeStyle('lprType')}>
            LPR品种：{App.matchOption('LPRTypeEnum', lprType).label}
          </span>
          ，
          <span style={getChangeStyle('lprRatePercent')}>
            LPR利率：{hasValue(lprRatePercent) ? lprRatePercent + '%' : '-'}
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
