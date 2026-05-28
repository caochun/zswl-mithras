import { observer } from '@zswl/admin'
import { TextAreaReadOnly, RadioReadOnly } from '@/components/Form/FormRead'
import { Row, Col, Space } from 'antd'
import { Form, Button } from '@zswl/components'
import RowSpan from '../RowSpan'
import styles from '../style.less'
import { options } from '@/utils'
import { useEffect } from 'react'
import InstructionsText from '../InstructionsText'

const { yesOrNoString: yesOrNo } = options
const NaturalPerson = ({ isNaturalPerson, editable }) => {
  return (
    <>
      <Row className={styles.row}>
        <Col span={8} className={styles.subTitle}>
          自然人担保情况
        </Col>
        <Col span={16} className={styles.right}>
          <Form.Item label="涉及自然人担保" name="SOA_C_8_01">
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
                    name={[filed.name, 'SOA_C_8_02']}
                    required={isNaturalPerson}
                  >
                    <TextAreaReadOnly onlyRead={!editable} placeholder="请输入担保人名称" />
                  </RowSpan>

                  <Row className={styles.row}>
                    <Col span={8} className={styles.subTitle}>
                      自然人担保人分析
                    </Col>

                    <Col span={16} className={styles.right}>
                      <Form.Item
                        label="是否存在相关负面消息"
                        name={[filed.name, 'SOA_C_8_03_01']}
                        required={isNaturalPerson}
                      >
                        <RadioReadOnly onlyRead={!editable} options={yesOrNo} />
                      </Form.Item>
                      <Form.Item dependencies={[['naturalPerson', filed.name, 'SOA_C_8_03_01']]}>
                        {({ getFieldValue }) => {
                          const required =
                            isNaturalPerson &&
                            getFieldValue(['naturalPerson', filed.name, 'SOA_C_8_03_01']) === '1'
                          return (
                            <Form.Item
                              label="负面信息及对担保能力的影响"
                              name={[filed.name, 'SOA_C_8_03_02']}
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
  )
}
const GuarantorInfo = ({ editable, required, namePrefix, chiName, name, listName }) => {
  console.log('listName: ', listName)
  return (
    <>
      <RowSpan
        subTitle={'担保人经营情况分析'}
        name={[name, 'SOA_C_5_03']}
        tooltip="对主体评级、主要经营财务指标变动、人员稳定性及工资发放情况、投资计划及融资变化等进行说明和分析"
      >
        <TextAreaReadOnly onlyRead={!editable} placeholder="请输入担保人经营情况分析" />
      </RowSpan>
      <InstructionsText
        listName={listName}
        title="担保人本期是否出现变更股东、注册资本、经营范围、法定代表人等情况"
        radioName={[name, 'SOA_C_6_01_01']}
        textAreaName={[name, 'SOA_C_6_01_02']}
        editable={editable}
        required={required}
        triggerValue="1"
      />

      <InstructionsText
        title="担保人本期是否出现住所、通讯地址、联系人、联系方式变更"
        radioName={[name, 'SOA_C_6_02_01']}
        textAreaName={[name, 'SOA_C_6_02_02']}
        editable={editable}
        required={required}
        listName={listName}
        triggerValue="1"
      />

      <InstructionsText
        title="担保人主要职能定位及经营业务是否发生重大变化"
        radioName={[name, 'SOA_C_6_03_01']}
        textAreaName={[name, 'SOA_C_6_03_02']}
        editable={editable}
        required={required}
        listName={listName}
        triggerValue="1"
      />

      <InstructionsText
        title="担保人融资渠道是否通畅"
        radioName={[name, 'SOA_C_6_04_01']}
        textAreaName={[name, 'SOA_C_6_04_02']}
        editable={editable}
        required={required}
        listName={listName}
        triggerValue="0"
      />

      <InstructionsText
        title="是否存在被关闭或划转兼并的明确安排"
        radioName={[name, 'SOA_C_6_05_01']}
        textAreaName={[name, 'SOA_C_6_05_02']}
        editable={editable}
        required={required}
        triggerValue="1"
        listName={listName}
      />

      <Row className={styles.row}>
        <Col span={8} className={styles.subTitle}>
          担保人舆情信息
        </Col>
        <Col span={16} className={styles.right}>
          <Form.Item
            label="是否存在重大负面舆情"
            name={[name, 'SOA_C_7_01']}
            required={required}
            rules={[{ required }]}
          >
            <RadioReadOnly onlyRead={!editable} options={yesOrNo} />
          </Form.Item>

          <Form.Item dependencies={[[listName, name, 'SOA_C_7_01']]}>
            {({ getFieldValue }) => {
              const isRequired = getFieldValue([listName, name, 'SOA_C_7_01']) === '1'
              return (
                <Form.Item
                  label="具体分析"
                  name={[name, 'SOA_C_7_02']}
                  required={isRequired}
                  rules={[{ required: isRequired }]}
                >
                  <TextAreaReadOnly onlyRead={!editable} />
                </Form.Item>
              )
            }}
          </Form.Item>

          <Form.Item label="舆情信息">
            <a href={`/risk/publicMonitor?chiName=${chiName}`} target="_blank">
              查看舆情监测信息
            </a>
          </Form.Item>
        </Col>
      </Row>
    </>
  )
}
/**
 * 担保人基本情况组件
 * @param {Object} props - 组件属性
 * @param {boolean} props.editable - 是否可编辑
 * @param {boolean} props.required - 是否必填
 * @param {string} props.namePrefix - 表单字段名前缀
 */
const GuarantorBasicInfo = observer(({ editable, required = true, namePrefix, chiName }) => {
  const form = Form.useFormInstance()
  const SOA_C_5_01 = Form.useWatch('SOA_C_5_01', form)
  const isGuarantor = SOA_C_5_01 === '1'

  const SOA_C_8_01 = Form.useWatch('SOA_C_8_01', form)
  useEffect(() => {
    const naturalPerson = form.getFieldValue('naturalPerson')
    if (SOA_C_8_01 === '1' && naturalPerson?.length === 0) {
      form.setFieldsValue({
        naturalPerson: [{}],
      })
    }
  }, [SOA_C_8_01])
  const isNaturalPerson = SOA_C_8_01 === '1'

  return (
    <>
      <div className={styles.titleRow}>担保人经营情况</div>
      <Row className={styles.row}>
        <Col span={8} className={styles.subTitle}>
          企业担保情况
        </Col>
        <Col span={16} className={styles.right}>
          <Form.Item label="涉及企业担保" name="SOA_C_5_01">
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
                    name={[field.name, 'SOA_C_5_02']}
                    required={isGuarantor}
                  >
                    <TextAreaReadOnly onlyRead={!editable} placeholder="请输入担保人名称" />
                  </RowSpan>
                  <GuarantorInfo
                    listName={'guarantor'}
                    name={field.name}
                    editable={editable}
                    required={required}
                    namePrefix={namePrefix}
                    chiName={chiName}
                  />
                </>
              )
            })
            return (
              <div>
                {formList}
                {editable && (
                  <Space align="center" className={styles.buttonRow}>
                    <Button.Add type="primary" onClick={() => add({})}>
                      新增企业担保人信息
                    </Button.Add>
                  </Space>
                )}
              </div>
            )
          }}
        </Form.List>
      </div>
      <NaturalPerson isNaturalPerson={isNaturalPerson} editable={editable} />
    </>
  )
})

export default GuarantorBasicInfo
