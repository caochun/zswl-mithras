import { observer } from '@zswl/admin'
import { TextAreaReadOnly, RadioReadOnly } from '@/components/Form/FormRead'
import { Row, Col } from 'antd'
import { Form } from '@zswl/components'
import styles from './style.less'
import { options } from '@/utils'

const { yesOrNoString: yesOrNo } = options

/**
 * 带情况说明的单选组件
 * @param {Object} props - 组件属性
 * @param {string} props.title - 标题文本
 * @param {string|Array} props.radioName - 单选框字段名，支持字符串或数组格式
 * @param {string|Array} props.textAreaName - 文本域字段名，支持字符串或数组格式
 * @param {boolean} props.editable - 是否可编辑
 * @param {boolean} props.required - 是否必填
 * @param {string} props.triggerValue - 触发显示文本域的值，默认为'1'(是)
 * @param {string} props.placeholder - 文本域占位符
 * @param {string} props.label - 文本域标签，默认为'情况说明'
 * @param {boolean} props.showTitle - 是否显示标题，默认为true
 * @param {Function} props.onRadioChange - 单选框变化回调
 * @param {boolean} props.conditionalRequired - 是否使用条件必填
 * @param {Array} props.dependencies - 依赖字段数组
 * @param {Function} props.requiredCondition - 必填条件函数
 */
const InstructionsText = observer(
  ({
    listName,
    title,
    radioName,
    textAreaName,
    editable,
    required = true,
    triggerValue = '1',
    placeholder = '请填写情况说明',
    label = '情况说明',
    showTitle = true,
    onRadioChange,
    conditionalRequired = false,
    dependencies = [],
    requiredCondition,
  }) => {
    const form = Form.useFormInstance()

    // 如果是条件必填模式且没有单选框
    if (conditionalRequired && !radioName) {
      return (
        <Row className={styles.row}>
          {showTitle && (
            <Col span={8} className={styles.subTitle}>
              {title}
            </Col>
          )}
          <Col span={showTitle ? 16 : 24} className={styles.right}>
            <Form.Item dependencies={dependencies}>
              {({ getFieldValue }) => {
                const isRequired = requiredCondition ? requiredCondition(getFieldValue) : false
                return (
                  <Form.Item
                    name={textAreaName}
                    rules={[{ required: isRequired }]}
                    messageVariables={{ label: title }}
                  >
                    <TextAreaReadOnly onlyRead={!editable} placeholder={placeholder} />
                  </Form.Item>
                )
              }}
            </Form.Item>
          </Col>
        </Row>
      )
    }

    return (
      <Row className={styles.row}>
        {showTitle && (
          <Col span={8} className={styles.subTitle}>
            {title}
          </Col>
        )}
        <Col span={showTitle ? 16 : 24} className={styles.right}>
          <Form.Item label="" name={radioName} required={required} rules={[{ required }]}>
            <RadioReadOnly
              onlyRead={!editable}
              options={yesOrNo}
              onChange={(value) => {
                // 触发情况说明字段的验证
                if (form) {
                  const fieldsToValidate = listName ? [[listName, ...textAreaName]] : [textAreaName]
                  form.validateFields(fieldsToValidate).catch(() => {})
                }
                // 调用外部传入的onChange回调
                if (onRadioChange) {
                  onRadioChange(value)
                }
              }}
            />
          </Form.Item>

          <Form.Item dependencies={listName ? [[listName, ...radioName]] : [radioName]}>
            {({ getFieldValue }) => {
              const radioValue = getFieldValue(listName ? [listName, ...radioName] : radioName)
              const isRequired = radioValue === triggerValue
              return (
                <Form.Item
                  label={label}
                  name={textAreaName}
                  required={isRequired}
                  rules={[{ required: isRequired }]}
                  messageVariables={{ label: title }}
                >
                  <TextAreaReadOnly onlyRead={!editable} placeholder={placeholder} />
                </Form.Item>
              )
            }}
          </Form.Item>
        </Col>
      </Row>
    )
  }
)

export default InstructionsText
