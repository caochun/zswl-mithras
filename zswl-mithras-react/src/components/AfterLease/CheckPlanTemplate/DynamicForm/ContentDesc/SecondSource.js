import { Radio, Input, Row, Col, Tooltip, Space } from 'antd'

import { Button, Form } from '@zswl/components'
import styles from './style.less'
import { options } from '@/utils'
import { FinancialConditionAnalysis } from './NoPublicContent/V1V2'
import { TABLE_INITIAL_VALUES2 } from './CheckContent'
import RowSpan from './RowSpan'
import { useEffect } from 'react'
import _ from 'lodash'

const { yesOrNoString: yesOrNo } = options
// 担保人分析
const GuarantorAnalysis = ({ editable, field, name, isV1, isV2, isV3, required = false }) => {
  const getName = (itemField) => (field ? [field?.name, itemField] : itemField)
  const fieldList = ['NP_C_3_01_01', 'NP_C_3_01_02', 'NP_C_3_01_03', 'NP_C_3_01_04']
  const dependencies = fieldList.map((v) => (field ? [name, field.name, v] : v))
  return (
    <Row className={styles.row}>
      <Col span={8} className={styles.subTitle}>
        担保人主体资格分析
      </Col>

      <Col span={16} className={styles.right}>
        <Form.Item label="主体资格是否发生重大变化" name={getName('NP_C_3_01_01')}>
          <RadioReadOnly onlyRead={!editable} options={yesOrNo} />
        </Form.Item>

        <Form.Item
          label="是否发生歇业、解散、停业整顿、被吊销营业执照或被撤销等情况"
          name={getName('NP_C_3_01_02')}
        >
          <RadioReadOnly onlyRead={!editable} options={yesOrNo} />
        </Form.Item>

        <Form.Item
          label="注册资本、经营住所、经营范围、法定代表人是否发生变化"
          name={getName('NP_C_3_01_03')}
        >
          <RadioReadOnly onlyRead={!editable} options={yesOrNo} />
        </Form.Item>

        <Form.Item label="担保人具备偿还租金意愿" name={getName('NP_C_3_01_04')}>
          <RadioReadOnly onlyRead={!editable} options={yesOrNo} />
        </Form.Item>
        {!isV1 && (
          <Form.Item dependencies={dependencies}>
            {({ getFieldValue }) => {
              const object = getFieldValue(name)
              const values = _.hasOwnProperty(object, field?.name) ? object[field?.name] : {}
              const itemRequired =
                required && fieldList?.some((v, i) => values[v] === ['1', '1', '1', '0'][i])
              return (
                <Form.Item
                  label="若发生重大变化,具体情况"
                  name={getName('NP_C_3_01_05')}
                  rules={[{ required: itemRequired }]}
                >
                  <TextAreaReadOnly onlyRead={!editable} />
                </Form.Item>
              )
            }}
          </Form.Item>
        )}
      </Col>
    </Row>
  )
}
const SecondSource = (props) => {
  const { editable, isV1, isV2, isV3 } = props
  const form = Form.useFormInstance()
  const NP_C_3_03_01 = Form.useWatch('NP_C_3_03_01', form)
  useEffect(() => {
    const guarantor = form.getFieldValue('guarantor')
    if (NP_C_3_03_01 === '1' && guarantor?.length === 0 && isV2) {
      form.setFieldsValue({
        guarantor: [{ NP_C_3_05_03: TABLE_INITIAL_VALUES2 }],
      })
    }
  }, [NP_C_3_03_01])

  const isGuarantor = NP_C_3_03_01 === '1'
  const NP_C_3_06_01 = Form.useWatch('NP_C_3_06_01', form)
  useEffect(() => {
    const naturalPerson = form.getFieldValue('naturalPerson')
    if (NP_C_3_06_01 === '1' && naturalPerson?.length === 0 && isV2) {
      form.setFieldsValue({
        naturalPerson: [{}],
      })
    }
  }, [NP_C_3_06_01])
  const isNaturalPerson = NP_C_3_06_01 === '1'
  return (
    <>
      <div className={styles.titleRow}>第二还款来源分析</div>

      {isV1 && <GuarantorAnalysis {...props} />}
      {!isV1 && (
        <>
          <Row className={styles.row}>
            <Col span={8} className={styles.subTitle}>
              企业担保情况
            </Col>
            <Col span={16} className={styles.right}>
              <Form.Item label="涉及企业担保" name="NP_C_3_03_01">
                <RadioReadOnly onlyRead={!editable} options={yesOrNo} />
              </Form.Item>
            </Col>
          </Row>
          <div style={{ display: isGuarantor ? 'block' : 'none' }}>
            <Form.List name={'guarantor'}>
              {(fields, { add, remove }) => {
                const formList = fields.map((field, index) => {
                  return (
                    <>
                      <Space className={styles.secondTitle}>
                        担保人 {index + 1}
                        {editable && index !== 0 && (
                          <Button.Delete onClick={() => remove(field.name)} />
                        )}
                      </Space>
                      <RowSpan
                        subTitle={'担保人名称'}
                        name={[field.name, 'NP_C_3_04']}
                        required={isGuarantor}
                      >
                        <TextAreaReadOnly onlyRead={!editable} />
                      </RowSpan>

                      <GuarantorAnalysis
                        editable={editable}
                        field={field}
                        name="guarantor"
                        required={isGuarantor}
                        {...props}
                      />
                      {!isV3 && (
                        <RowSpan
                          subTitle={'担保人经营情况分析'}
                          name={[field.name, 'NP_C_3_02_01']}
                          required={isGuarantor}
                        >
                          <TextAreaReadOnly onlyRead={!editable} />
                        </RowSpan>
                      )}

                      <FinancialConditionAnalysis
                        editable={editable}
                        nameIndex={field.name}
                        attributionList={'guarantor'}
                        required={isGuarantor}
                        {...props}
                      />
                    </>
                  )
                })
                return (
                  <div>
                    {formList}
                    {editable && (
                      <Space align="center" className={styles.buttonRow}>
                        <Button.Add
                          type="primary"
                          onClick={() => add({ NP_C_3_05_03: TABLE_INITIAL_VALUES2 })}
                        >
                          新增企业担保人信息
                        </Button.Add>
                      </Space>
                    )}
                  </div>
                )
              }}
            </Form.List>
          </div>

          <Row className={styles.row}>
            <Col span={8} className={styles.subTitle}>
              自然人担保情况
            </Col>
            <Col span={16} className={styles.right}>
              <Form.Item label="涉及自然人担保" name="NP_C_3_06_01">
                <RadioReadOnly onlyRead={!editable} options={yesOrNo} />
              </Form.Item>
            </Col>
          </Row>
          <div style={{ display: isNaturalPerson ? 'block' : 'none' }}>
            <Form.List name={'naturalPerson'}>
              {(fields, { add, remove }) => {
                const formList = fields.map((filed, index) => {
                  return (
                    <>
                      <Space className={styles.secondTitle}>
                        自然人担保人 {index + 1}
                        {editable && index !== 0 && (
                          <Button.Delete onClick={() => remove(filed.name)} />
                        )}
                      </Space>
                      <RowSpan
                        subTitle={'担保人名称'}
                        name={[filed.name, 'NP_C_3_07']}
                        required={isNaturalPerson}
                      >
                        <TextAreaReadOnly onlyRead={!editable} />
                      </RowSpan>

                      <Row className={styles.row}>
                        <Col span={8} className={styles.subTitle}>
                          自然人担保人分析
                        </Col>

                        <Col span={16} className={styles.right}>
                          <Form.Item
                            label="是否存在相关负面消息"
                            name={[filed.name, 'NP_C_3_08_01']}
                            required={isNaturalPerson}
                          >
                            <RadioReadOnly onlyRead={!editable} options={yesOrNo} />
                          </Form.Item>
                          <Form.Item dependencies={[['naturalPerson', filed.name, 'NP_C_3_08_01']]}>
                            {({ getFieldValue }) => {
                              const required =
                                isNaturalPerson &&
                                getFieldValue(['naturalPerson', filed.name, 'NP_C_3_08_01']) === '1'
                              return (
                                <Form.Item
                                  label="负面信息及对担保能力的影响"
                                  name={[filed.name, 'NP_C_3_08_02']}
                                  rules={[{ required }]}
                                >
                                  <TextAreaReadOnly onlyRead={!editable} />
                                </Form.Item>
                              )
                            }}
                          </Form.Item>
                        </Col>
                      </Row>
                    </>
                  )
                })
                return (
                  <div>
                    {formList}
                    {editable && (
                      <Space align="center" className={styles.buttonRow}>
                        <Button.Add type="primary" onClick={() => add({})}>
                          新增自然人担保人信息
                        </Button.Add>
                      </Space>
                    )}
                  </div>
                )
              }}
            </Form.List>
          </div>
        </>
      )}
      {!isV3 && (
        <RowSpan subTitle={'抵质押物分析'} name={'NP_C_3_02'}>
          <TextAreaReadOnly onlyRead={!editable} />
        </RowSpan>
      )}
    </>
  )
}

export default SecondSource
