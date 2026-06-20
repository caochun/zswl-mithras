import { StarDom } from '@/components/Form'
import { useEffect } from 'react'
import { Select, Form, App } from '@zswl/components'
import { Descriptions, Input } from 'antd'
import { amountStrToNumber, getInputNumberValueFromEvent } from '@/utils'
import mathjs from '@/utils/math'
import { bizTypeMap } from './bizTypeConfig'
import styles from './index.less'

function Index({ store = {}, detail = {}, oldDetail = {} }) {
  const { bizType } = store.page.getParams()
  const { form } = store
  const { showVal: showValue } = store

  const curBizTypeConfig = bizTypeMap[bizType] || {}
  const { optionsType } = App.getData()
  const getDetailValue = (key, isOld) => {
    return isOld ? oldDetail[key] : detail[key]
  }
  useEffect(() => {
    if (detail) {
      form.setFieldsValue({
        rateType: detail.rateType,
        lprType: detail.lprType,
        lprPercent: detail.lprPercent,
        lprAddPercent: detail.lprAddPercent,
      })
    }
    if (oldDetail) {
      form.setFieldsValue({
        old_rateType: oldDetail.rateType,
        old_lprType: oldDetail.lprType,
        old_lprPercent: oldDetail.lprPercent,
        old_lprAddPercent: oldDetail.lprAddPercent,
      })
    }
  }, [detail, oldDetail, form])

  return (
    <Descriptions
      title="利率调整方案"
      bordered
      column={2}
      labelStyle={{ background: '#F5F6FA' }}
      size={'small'}
      className={styles.des}
    >
      {showValue ? (
        <>
          <Descriptions.Item
            label={<StarDom name={`原${curBizTypeConfig.title}`}></StarDom>}
            span={2}
          >
            {mathjs.format(
              mathjs.add(getDetailValue('lprPercent', true), getDetailValue('lprAddPercent', true))
            ) + '%'}
          </Descriptions.Item>
          <Descriptions.Item
            label={<StarDom name={`新${curBizTypeConfig.title}`}></StarDom>}
            span={2}
          >
            {mathjs.format(
              mathjs.add(getDetailValue('lprPercent'), getDetailValue('lprAddPercent'))
            ) + '%'}
          </Descriptions.Item>
        </>
      ) : (
        <>
          <Descriptions.Item
            label={<StarDom name={`原${curBizTypeConfig.title}`}></StarDom>}
            span={2}
          >
            <Input.Group compact={true}>
              <Form.Item name="old_rateType" style={{ width: 120, marginRight: 10 }}>
                <Select options={optionsType.rateType} placeholder="利率类型" disabled />
              </Form.Item>
              <Form.Item name="old_lprType" style={{ width: 120, marginRight: 10 }}>
                <Select options={optionsType.LPRTypeEnum} placeholder="LPR品种" disabled />
              </Form.Item>
              <Form.Item name="old_lprPercent" style={{ width: 140 }}>
                <Input placeholder="LPR" disabled suffix={<div className={styles.suffix}>%</div>} />
              </Form.Item>
              <Form.Item style={{ marginLeft: 16, marginRight: 16 }}>+</Form.Item>
              <Form.Item name="old_lprAddPercent" style={{ width: 150 }}>
                <Input
                  placeholder="加点利率"
                  disabled
                  suffix={<div className={styles.suffix}>%</div>}
                />
              </Form.Item>
              <Form.Item style={{ marginLeft: 16, marginRight: 16 }}>=</Form.Item>
              <Form.Item dependencies={['old_lprPercent', 'old_lprAddPercent']} noStyle>
                {({ getFieldValue }) => {
                  const old_lprPercent = amountStrToNumber(getFieldValue('old_lprPercent')) || 0
                  const old_lprAddPercent =
                    amountStrToNumber(getFieldValue('old_lprAddPercent')) || 0
                  return (
                    <Form.Item>
                      <Input
                        disabled
                        suffix={<div className={styles.suffix}>%</div>}
                        style={{ width: '100%' }}
                        placeholder={curBizTypeConfig.title}
                        value={mathjs.format(mathjs.add(old_lprPercent, old_lprAddPercent))}
                      />
                    </Form.Item>
                  )
                }}
              </Form.Item>
            </Input.Group>
          </Descriptions.Item>
          <Descriptions.Item
            label={<StarDom name={`新${curBizTypeConfig.title}`}></StarDom>}
            span={2}
          >
            <Input.Group compact={true}>
              <Form.Item name="rateType" style={{ width: 120, marginRight: 10 }}>
                <Select options={optionsType.rateType} disabled placeholder="利率类型" />
              </Form.Item>
              <Form.Item name="lprType" style={{ width: 120, marginRight: 10 }}>
                <Select options={optionsType.LPRTypeEnum} disabled placeholder="LPR品种" />
              </Form.Item>
              <Form.Item
                name="lprPercent"
                style={{ width: 140 }}
                getValueFromEvent={getInputNumberValueFromEvent}
                rules={[
                  {
                    required: true,
                    message: '请输入LPR！',
                  },
                ]}
              >
                <Input placeholder="LPR" suffix={<div className={styles.suffix}>%</div>} />
              </Form.Item>
              <Form.Item style={{ marginLeft: 16, marginRight: 16 }}>+</Form.Item>
              <Form.Item name="lprAddPercent" style={{ width: 150 }}>
                <Input
                  placeholder="加点利率"
                  disabled
                  suffix={<div className={styles.suffix}>%</div>}
                />
              </Form.Item>
              <Form.Item style={{ marginLeft: 16, marginRight: 16 }}>=</Form.Item>
              <Form.Item dependencies={['lprPercent', 'lprAddPercent']} noStyle>
                {({ getFieldValue }) => {
                  const lprPercent = amountStrToNumber(getFieldValue('lprPercent')) || 0
                  const lprAddPercent = amountStrToNumber(getFieldValue('lprAddPercent')) || 0
                  return (
                    <Form.Item getValueFromEvent={getInputNumberValueFromEvent}>
                      <Input
                        disabled
                        suffix={<div className={styles.suffix}>%</div>}
                        style={{ width: '100%' }}
                        placeholder={curBizTypeConfig.title}
                        value={mathjs.format(mathjs.add(lprPercent, lprAddPercent))}
                      />
                    </Form.Item>
                  )
                }}
              </Form.Item>
            </Input.Group>
          </Descriptions.Item>
        </>
      )}
    </Descriptions>
  )
}

export default Index
