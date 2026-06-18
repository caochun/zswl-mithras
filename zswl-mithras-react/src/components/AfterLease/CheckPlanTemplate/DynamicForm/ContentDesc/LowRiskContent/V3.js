import { observer } from '@zswl/admin'
import { forwardRef } from 'react'
import styles from '../style.less'
import { options } from '@/utils'
import { Row, Col, Tooltip } from 'antd'
import { Form } from '@zswl/components'
import { RadioReadOnly, TextAreaReadOnly } from '@/components/Form/FormRead'
import { QuestionCircleOutlined } from '@ant-design/icons'
import SecondSource from './SecondSource'
import FinancialConditionAnalysis from './FinancialConditionAnalysis'

const { yesOrNoString: yesOrNo, templateStatus } = options

export const ApprovalIdea = observer(({ editable }) => {
  return (
    <>
      <div className={styles.titleRow}>有权机构审批意见落实情况</div>
      <Row className={styles.row}>
        <Col span={8} className={styles.subTitle}>
          有权机构审批意见落实情况
        </Col>
        <Col span={16} className={styles.right}>
          <Form.Item label="是否落实审批条件" name="LR_C_1_01_01" rules={[{ required: true }]}>
            <RadioReadOnly onlyRead={!editable} options={yesOrNo} />
          </Form.Item>
          <Form.Item dependencies={['LR_C_1_01_01']}>
            {({ getFieldValue }) => {
              const required = getFieldValue('LR_C_1_01_01') === '0'
              return (
                <Form.Item
                  label="若未落实"
                  name="LR_C_1_01_02"
                  required={required}
                  rules={[{ required: true }]}
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

export const CollectAnalysis = observer(({ chiName, editable, isV2 }) => {
  return (
    <Row className={styles.row}>
      <Col span={8} className={styles.subTitle}>
        其它情况搜集与分析
        <Tooltip
          className={styles.tooltip}
          title="其它情况搜集与分析提示：有无影响授信的相关信息，包括不限于客户有无新增重大诉讼、正在被执行案件、失信被执行案件、负面新闻、征信情况、有无参与期货等重大变动情况。客户实际控制人/法定代表人有无不良嗜好、有无新增重大诉讼、正在被执行案件、失信被执行案件、负面新闻、征信情况、有无参与期货等重大变动情况。管理层是否出现重大变动等情况。"
        >
          <QuestionCircleOutlined />
        </Tooltip>
      </Col>
      <Col span={16} className={styles.right}>
        <Form.Item label="是否出现重大经济纠纷、诉讼、破产等情况" name="LR_C_2_05_01">
          <RadioReadOnly onlyRead={!editable} options={yesOrNo} />
        </Form.Item>
        <Form.Item dependencies={['LR_C_2_05_01']}>
          {({ getFieldValue }) => {
            const required = getFieldValue('LR_C_2_05_01')
            return (
              <Form.Item
                label="具体分析"
                name="LR_C_2_05_02"
                required={required}
                rules={[{ required: true }]}
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
  )
})
function Index(props, ref) {
  const { editable, reportTemplateType, chiName } = props
  const isV1 = reportTemplateType === 'V1'
  const isV2 = reportTemplateType === 'V2'
  const isV3 = reportTemplateType === 'V3'
  const baseProps = { isV1, isV2, isV3, ...props }
  return (
    <div className={styles.contentBox}>
      <div className={styles.titleRow}>客户经营情况分析</div>
      <Row className={styles.row}>
        <Col span={8} className={styles.subTitle}>
          承租人主体资格分析
        </Col>
        <Col span={16} className={styles.right}>
          <Form.Item label="主体资格是否发生重大变化" name="LR_C_2_01_01">
            <RadioReadOnly onlyRead={!editable} options={yesOrNo} />
          </Form.Item>
          <Form.Item
            label="是否发生歇业、解散、停业整顿、被吊销营业执照或被撤销等情况"
            name="LR_C_2_01_02"
          >
            <RadioReadOnly onlyRead={!editable} options={yesOrNo} />
          </Form.Item>
          <Form.Item
            label="注册资本、经营住所、经营范围、法定代表人是否发生变化"
            name="LR_C_2_01_03"
          >
            <RadioReadOnly onlyRead={!editable} options={yesOrNo} />
          </Form.Item>
          <Form.Item label="承租人具备偿还租金意愿" name="LR_C_2_01_04">
            <RadioReadOnly onlyRead={!editable} options={yesOrNo} />
          </Form.Item>
          <Form.Item
            dependencies={['LR_C_2_01_01', 'LR_C_2_01_02', 'LR_C_2_01_03', 'LR_C_2_01_04']}
          >
            {({ getFieldValue }) => {
              const required =
                getFieldValue('LR_C_2_01_01') === '1' ||
                getFieldValue('LR_C_2_01_02') === '1' ||
                getFieldValue('LR_C_2_01_03') === '1' ||
                getFieldValue('LR_C_2_01_04') === '0'
              return (
                <>
                  <Form.Item
                    label="若发生重大变化，具体情况"
                    name="LR_C_1_01_05"
                    required={required}
                    rules={[{ required }]}
                  >
                    <TextAreaReadOnly onlyRead={!editable} />
                  </Form.Item>
                </>
              )
            }}
          </Form.Item>
        </Col>
      </Row>

      <FinancialConditionAnalysis {...baseProps} />

      <CollectAnalysis {...baseProps}></CollectAnalysis>

      <SecondSource {...baseProps} />
      <div className={styles.titleRow}>租赁物情况</div>
      <Row className={styles.row}>
        <Col span={8} className={styles.subTitle}>
          租赁物是否正常使用
        </Col>
        <Col span={16} className={styles.right}>
          <Form.Item name="LR_C_4_01">
            <RadioReadOnly onlyRead={!editable} options={templateStatus} />
          </Form.Item>
        </Col>
      </Row>
      <Row className={styles.row}>
        <Col span={8} className={styles.subTitle}>
          承租人是否对租赁物计提折旧
        </Col>
        <Col span={16} className={styles.right}>
          <Form.Item name="LR_C_4_02">
            <RadioReadOnly onlyRead={!editable} options={templateStatus} />
          </Form.Item>
        </Col>
      </Row>
      <Row className={styles.row}>
        <Col span={8} className={styles.subTitle}>
          租赁物是否再次销售/转让
        </Col>
        <Col span={16} className={styles.right}>
          <Form.Item name="LR_C_4_03">
            <RadioReadOnly onlyRead={!editable} options={templateStatus} />
          </Form.Item>
        </Col>
      </Row>
      <Row className={styles.row}>
        <Col span={8} className={styles.subTitle}>
          租赁物是否转让
        </Col>
        <Col span={16} className={styles.right}>
          <Form.Item name="LR_C_4_04">
            <RadioReadOnly onlyRead={!editable} options={templateStatus} />
          </Form.Item>
        </Col>
      </Row>
      <Row className={styles.row}>
        <Col span={8} className={styles.subTitle}>
          租赁物是否发生过重大停产停运、重大故障、重大维修情况
        </Col>
        <Col span={16} className={styles.right}>
          <Form.Item name="LR_C_4_05">
            <RadioReadOnly onlyRead={!editable} options={templateStatus} />
          </Form.Item>
        </Col>
      </Row>
      <Row className={styles.row}>
        <Col span={8} className={styles.subTitle}>
          租赁物是否发生过升级换代、重大改造
        </Col>
        <Col span={16} className={styles.right}>
          <Form.Item name="LR_C_4_06">
            <RadioReadOnly onlyRead={!editable} options={templateStatus} />
          </Form.Item>
        </Col>
      </Row>
      <Row className={styles.row}>
        <Col span={8} className={styles.subTitle}>
          租赁物是否抵押/质押/留置
        </Col>
        <Col span={16} className={styles.right}>
          <Form.Item name="LR_C_4_07">
            <RadioReadOnly onlyRead={!editable} options={templateStatus} />
          </Form.Item>
        </Col>
      </Row>
      <Row className={styles.row}>
        <Col span={8} className={styles.subTitle}>
          租赁物是否用于投资入股、抵偿
        </Col>
        <Col span={16} className={styles.right}>
          <Form.Item name="LR_C_4_08">
            <RadioReadOnly onlyRead={!editable} options={templateStatus} />
          </Form.Item>
        </Col>
      </Row>
      <Row className={styles.row}>
        <Col span={8} className={styles.subTitle}>
          租赁物是否用于诉讼担保/保全等
        </Col>
        <Col span={16} className={styles.right}>
          <Form.Item name="LR_C_4_09">
            <RadioReadOnly onlyRead={!editable} options={templateStatus} />
          </Form.Item>
        </Col>
      </Row>
      <Row className={styles.row}>
        <Col span={8} className={styles.subTitle}>
          承租人是否对租赁物定期保养、维护
        </Col>
        <Col span={16} className={styles.right}>
          <Form.Item name="LR_C_4_10">
            <RadioReadOnly onlyRead={!editable} options={templateStatus} />
          </Form.Item>
        </Col>
      </Row>
      <Row className={styles.row}>
        <Col span={8} className={styles.subTitle}>
          租赁物的位置是否被移动
        </Col>
        <Col span={16} className={styles.right}>
          <Form.Item name="LR_C_4_11">
            <RadioReadOnly onlyRead={!editable} options={templateStatus} />
          </Form.Item>
        </Col>
      </Row>
      <Row className={styles.row}>
        <Col span={8} className={styles.subTitle}>
          是否存在其他损害租赁物所有权行为
        </Col>
        <Col span={16} className={styles.right}>
          <Form.Item name="LR_C_4_12">
            <RadioReadOnly onlyRead={!editable} options={templateStatus} />
          </Form.Item>
        </Col>
      </Row>
      <Row className={styles.row}>
        <Col span={8} className={styles.subTitle}>
          租赁物情况补充说明
        </Col>
        <Col span={16} className={styles.right}>
          <Form.Item
            dependencies={[
              'LR_C_4_01',
              'LR_C_4_02',
              'LR_C_4_03',
              'LR_C_4_04',
              'LR_C_4_05',
              'LR_C_4_06',
              'LR_C_4_07',
              'LR_C_4_08',
              'LR_C_4_09',
              'LR_C_4_10',
              'LR_C_4_11',
              'LR_C_4_12',
            ]}
          >
            {({ getFieldValue }) => {
              const required =
                getFieldValue('LR_C_4_01') === '0' ||
                getFieldValue('LR_C_4_02') === '1' ||
                getFieldValue('LR_C_4_03') === '1' ||
                getFieldValue('LR_C_4_04') === '1' ||
                getFieldValue('LR_C_4_05') === '1' ||
                getFieldValue('LR_C_4_06') === '1' ||
                getFieldValue('LR_C_4_07') === '1' ||
                getFieldValue('LR_C_4_08') === '1' ||
                getFieldValue('LR_C_4_09') === '1' ||
                getFieldValue('LR_C_4_10') === '0' ||
                getFieldValue('LR_C_4_11') === '1' ||
                getFieldValue('LR_C_4_12') === '1'

              return (
                <Form.Item
                  name="LR_C_4_13"
                  rules={[{ required }]}
                  messageVariables={{ label: '租赁物情况补充说明' }}
                >
                  <TextAreaReadOnly onlyRead={!editable} />
                </Form.Item>
              )
            }}
          </Form.Item>
        </Col>
      </Row>
    </div>
  )
}

export default observer(forwardRef(Index))
