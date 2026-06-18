import { observer } from '@zswl/admin'

import { Row, Col } from 'antd'
import { Form } from '@zswl/components'
import styles from '../style.less'
import { options } from '@/utils'
import InstructionsText from '../InstructionsText'

const { yesOrNoString: yesOrNo } = options

/**
 * 承租人基本情况组件
 * @param {Object} props - 组件属性
 * @param {boolean} props.editable - 是否可编辑
 * @param {boolean} props.required - 是否必填
 * @param {string} props.namePrefix - 表单字段名前缀，默认为 'BUS'
 */
const TenantBasicInfo = observer(({ editable, required = true, namePrefix = 'BUS', chiName }) => {
  return (
    <>
      <div className={styles.titleRow}>承租人基本情况</div>
      <InstructionsText
        title="承租人本期是否出现变更股东、注册资本、经营范围、法定代表人等情况"
        radioName="B_C_3_01_01"
        textAreaName="B_C_3_01_02"
        editable={editable}
        required={required}
        triggerValue="1"
      />

      <InstructionsText
        title="承租人本期是否出现住所、通讯地址、联系人、联系方式变更"
        radioName="B_C_3_02_01"
        textAreaName="B_C_3_02_02"
        editable={editable}
        required={required}
        triggerValue="1"
      />

      <InstructionsText
        title="承租人主要职能定位及经营业务是否发生重大变化"
        radioName="B_C_3_03_01"
        textAreaName="B_C_3_03_02"
        editable={editable}
        required={required}
        triggerValue="1"
      />

      <InstructionsText
        title="承租人融资渠道是否通畅"
        radioName="B_C_3_04_01"
        textAreaName="B_C_3_04_02"
        editable={editable}
        required={required}
        triggerValue="0"
      />

      <InstructionsText
        title="是否存在被关闭或划转兼并的明确安排"
        radioName="B_C_3_05_01"
        textAreaName="B_C_3_05_02"
        editable={editable}
        required={required}
        triggerValue="1"
      />
      <div className={styles.titleRow}>承租人舆情信息</div>
      <Row className={styles.row}>
        <Col span={8} className={styles.subTitle}>
          是否存在重大负面舆情
        </Col>
        <Col span={16} className={styles.right}>
          <Form.Item label="" name={'B_C_4_01'} required={required} rules={[{ required }]}>
            <RadioReadOnly onlyRead={!editable} options={yesOrNo} />
          </Form.Item>

          <Form.Item dependencies={['B_C_4_01']}>
            {({ getFieldValue }) => {
              const isRequired = getFieldValue('B_C_4_01') === '1'
              return (
                <Form.Item
                  label="具体分析"
                  name={'B_C_4_02'}
                  required={isRequired}
                  rules={[{ required: isRequired }]}
                >
                  <TextAreaReadOnly onlyRead={!editable} placeholder="请填写情况说明" />
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
      {/* <InstructionsText
        title="是否存在重大负面舆情"
        radioName="B_C_4_01"
        textAreaName="B_C_4_02"
        editable={editable}
        required={required}
        triggerValue="1"
        label="具体分析"
      /> */}
    </>
  )
})

export default TenantBasicInfo
