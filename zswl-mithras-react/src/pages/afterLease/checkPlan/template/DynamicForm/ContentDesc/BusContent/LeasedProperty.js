import { observer } from '@zswl/admin'
import { TextAreaReadOnly, RadioReadOnly } from '@/components/Form/FormRead'
import { Row, Col } from 'antd'
import { Form } from '@zswl/components'
import styles from '../style.less'
import { options } from '@/utils'
import InstructionsText from '../InstructionsText'

const { yesOrNoString: yesOrNo, templateStatus } = options

/**
 * 租赁物情况组件
 * @param {Object} props - 组件属性
 * @param {boolean} props.editable - 是否可编辑
 * @param {boolean} props.required - 是否必填
 * @param {string} props.namePrefix - 表单字段名前缀
 */
const LeasedProperty = observer(({ editable, required = true, namePrefix }) => {
  return (
    <>
      <div className={styles.titleRow}>租赁物情况</div>

      <Row className={styles.row}>
        <Col span={8} className={styles.subTitle}>
          承租人是否将租赁物进行再次销售、转让
        </Col>
        <Col span={16} className={styles.right}>
          <Form.Item
            name={`${namePrefix}_C_10_01`}
            required={required}
            rules={[{ required, message: '请选择' }]}
          >
            <RadioReadOnly onlyRead={!editable} options={templateStatus} />
          </Form.Item>
        </Col>
      </Row>

      <Row className={styles.row}>
        <Col span={8} className={styles.subTitle}>
          承租人是否将租赁物进行了转租
        </Col>
        <Col span={16} className={styles.right}>
          <Form.Item
            name={`${namePrefix}_C_10_02`}
            required={required}
            rules={[{ required, message: '请选择' }]}
          >
            <RadioReadOnly onlyRead={!editable} options={templateStatus} />
          </Form.Item>
        </Col>
      </Row>

      <Row className={styles.row}>
        <Col span={8} className={styles.subTitle}>
          承租人是否将租赁物进行再次抵押、质押
        </Col>
        <Col span={16} className={styles.right}>
          <Form.Item
            name={`${namePrefix}_C_10_03`}
            required={required}
            rules={[{ required, message: '请选择' }]}
          >
            <RadioReadOnly onlyRead={!editable} options={templateStatus} />
          </Form.Item>
        </Col>
      </Row>

      <Row className={styles.row}>
        <Col span={8} className={styles.subTitle}>
          承租人是否将租赁物进行了投资入股、抵偿债务
        </Col>
        <Col span={16} className={styles.right}>
          <Form.Item
            name={`${namePrefix}_C_10_04`}
            required={required}
            rules={[{ required, message: '请选择' }]}
          >
            <RadioReadOnly onlyRead={!editable} options={templateStatus} />
          </Form.Item>
        </Col>
      </Row>

      <Row className={styles.row}>
        <Col span={8} className={styles.subTitle}>
          承租人是否将租赁物进行诉讼担保、是否对租赁物进行了保全担保等处置行为
        </Col>
        <Col span={16} className={styles.right}>
          <Form.Item
            name={`${namePrefix}_C_10_05`}
            required={required}
            rules={[{ required, message: '请选择' }]}
          >
            <RadioReadOnly onlyRead={!editable} options={templateStatus} />
          </Form.Item>
        </Col>
      </Row>

      <Row className={styles.row}>
        <Col span={8} className={styles.subTitle}>
          承租人是否以其他任何方式进行了侵害出租人对租赁物的所有权的行为
        </Col>
        <Col span={16} className={styles.right}>
          <Form.Item
            name={`${namePrefix}_C_10_06`}
            required={required}
            rules={[{ required, message: '请选择' }]}
          >
            <RadioReadOnly onlyRead={!editable} options={templateStatus} />
          </Form.Item>
        </Col>
      </Row>

      <Row className={styles.row}>
        <Col span={8} className={styles.subTitle}>
          租赁物是否能够正常使用
        </Col>
        <Col span={16} className={styles.right}>
          <Form.Item
            name={`${namePrefix}_C_10_07`}
            required={required}
            rules={[{ required, message: '请选择' }]}
          >
            <RadioReadOnly onlyRead={!editable} options={templateStatus} />
          </Form.Item>
        </Col>
      </Row>

      <Row className={styles.row}>
        <Col span={8} className={styles.subTitle}>
          租赁物是否发生过升级换代、改造
        </Col>
        <Col span={16} className={styles.right}>
          <Form.Item
            name={`${namePrefix}_C_10_08`}
            required={required}
            rules={[{ required, message: '请选择' }]}
          >
            <RadioReadOnly onlyRead={!editable} options={templateStatus} />
          </Form.Item>
        </Col>
      </Row>

      <Row className={styles.row}>
        <Col span={8} className={styles.subTitle}>
          租赁物是否发生过重大停产停运、重大故障、维修情况
        </Col>
        <Col span={16} className={styles.right}>
          <Form.Item
            name={`${namePrefix}_C_10_10`}
            required={required}
            rules={[{ required, message: '请选择' }]}
          >
            <RadioReadOnly onlyRead={!editable} options={templateStatus} />
          </Form.Item>
        </Col>
      </Row>
      <Row className={styles.row}>
        <Col span={8} className={styles.subTitle}>
          租赁物的位置是否出现非正常移动
        </Col>
        <Col span={16} className={styles.right}>
          <Form.Item
            name={`${namePrefix}_C_10_09`}
            required={required}
            rules={[{ required, message: '请选择' }]}
          >
            <RadioReadOnly onlyRead={!editable} options={templateStatus} />
          </Form.Item>
        </Col>
      </Row>
      <InstructionsText
        title="租赁物情况补充说明"
        textAreaName={`${namePrefix}_C_10_11`}
        editable={editable}
        required={false}
        conditionalRequired={true}
        dependencies={[
          `${namePrefix}_C_10_01`,
          `${namePrefix}_C_10_02`,
          `${namePrefix}_C_10_03`,
          `${namePrefix}_C_10_04`,
          `${namePrefix}_C_10_05`,
          `${namePrefix}_C_10_06`,
          `${namePrefix}_C_10_07`,
          `${namePrefix}_C_10_08`,
          `${namePrefix}_C_10_09`,
          `${namePrefix}_C_10_10`,
        ]}
        requiredCondition={(getFieldValue) =>
          getFieldValue(`${namePrefix}_C_10_01`) === '1' ||
          getFieldValue(`${namePrefix}_C_10_02`) === '1' ||
          getFieldValue(`${namePrefix}_C_10_03`) === '1' ||
          getFieldValue(`${namePrefix}_C_10_04`) === '1' ||
          getFieldValue(`${namePrefix}_C_10_05`) === '1' ||
          getFieldValue(`${namePrefix}_C_10_06`) === '1' ||
          getFieldValue(`${namePrefix}_C_10_07`) === '0' ||
          getFieldValue(`${namePrefix}_C_10_08`) === '1' ||
          getFieldValue(`${namePrefix}_C_10_09`) === '1' ||
          getFieldValue(`${namePrefix}_C_10_10`) === '1'
        }
        placeholder="请输入"
      />
    </>
  )
})

export default LeasedProperty
