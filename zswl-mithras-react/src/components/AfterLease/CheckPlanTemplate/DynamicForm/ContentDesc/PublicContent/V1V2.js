import { observer } from '@zswl/admin'

import styles from '../style.less'
import { options } from '@/utils'
import { Row, Col, Tooltip } from 'antd'

import { tableRequired, columns } from '../Utils'
import RowSpan from '../RowSpan'
import { Form } from '@zswl/components'
import { QuestionCircleOutlined } from '@ant-design/icons'

const { yesOrNoString: yesOrNo, templateStatus } = options

export const CollectAnalysis = observer(({ chiName, editable, isV1, isV2 }) => {
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
        <Form.Item label="是否出现重大经济纠纷、诉讼、破产等情况" name="P_C_2_05_01">
          <RadioReadOnly onlyRead={!editable} options={yesOrNo} />
        </Form.Item>
        <Form.Item dependencies={['P_C_2_05_01']}>
          {({ getFieldValue }) => {
            const required = getFieldValue('P_C_2_05_01')
            return (
              <Form.Item
                label="具体分析"
                name="P_C_2_05_02"
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

function Index(props) {
  const { editable, reportTemplateType } = props
  const isV1 = reportTemplateType === 'V1'
  const isV2 = reportTemplateType === 'V2'
  const isV3 = reportTemplateType === 'V3'
  const baseProps = { isV1, isV2, isV3, ...props }
  return (
    <div>
      <div className={styles.titleRow}>当地区域经济情况</div>
      <RowSpan
        subTitle={'承租人所在区域GDP、一般公共预算收入是否较上一年度下滑超过20%'}
        name="P_C_1_01"
      >
        <RadioReadOnly onlyRead={!editable} options={templateStatus} />
      </RowSpan>
      <RowSpan subTitle={'承租人所在区域是否有融资主体出现违约行为'} name="P_C_1_02">
        <RadioReadOnly onlyRead={!editable} options={templateStatus} />
      </RowSpan>
      <RowSpan subTitle={'当地区域经济情况补充说明'} name="P_C_1_03">
        <TextAreaReadOnly onlyRead={!editable} placeholder="请填写补充说明" />
      </RowSpan>

      <div className={styles.titleRow}>承租人经营情况</div>
      <RowSpan
        subTitle={'承租人本期是否出现变更股东、注册资本、经营范围、法定代表人等情况'}
        name="P_C_2_01"
      >
        <RadioReadOnly onlyRead={!editable} options={templateStatus} />
      </RowSpan>
      <RowSpan subTitle={'承租人本期是否出现住所、通讯地址、联系人、联系方式变更'} name="P_C_2_02">
        <RadioReadOnly onlyRead={!editable} options={templateStatus} />
      </RowSpan>
      <RowSpan subTitle={'承租人主要职能定位及经营业务是否发生重大变化'} name="P_C_2_03">
        <RadioReadOnly onlyRead={!editable} options={templateStatus} />
      </RowSpan>
      <RowSpan subTitle={'承租人融资渠道是否通畅'} name="P_C_2_04">
        <RadioReadOnly onlyRead={!editable} options={templateStatus} />
      </RowSpan>
      <RowSpan subTitle={'是否存在被关闭或划转兼并的明确安排'} name="P_C_2_05">
        <RadioReadOnly onlyRead={!editable} options={templateStatus} />
      </RowSpan>
      {isV2 && (
        <RowSpan subTitle={'承租人主要财务数据'} name="P_C_2_07" rules={[tableRequired()]}>
          <FormTable onlyRead={!editable} columns={columns} canAddDelete={false} />
        </RowSpan>
      )}
      <Form.Item dependencies={['P_C_2_01', 'P_C_2_02', 'P_C_2_03', 'P_C_2_04', 'P_C_2_05']}>
        {({ getFieldValue }) => {
          const required =
            getFieldValue('P_C_2_01') === '1' ||
            getFieldValue('P_C_2_02') === '1' ||
            getFieldValue('P_C_2_03') === '1' ||
            getFieldValue('P_C_2_04') === '0' ||
            getFieldValue('P_C_2_05') === '1'
          return (
            <RowSpan subTitle={'承租人经营情况补充说明'} name="P_C_2_06" required={required}>
              <TextAreaReadOnly onlyRead={!editable} />
            </RowSpan>
          )
        }}
      </Form.Item>

      <div className={styles.titleRow}>担保人经营情况</div>
      <RowSpan
        subTitle={'担保人本期是否出现变更股东、注册资本、经营范围、法定代表人等情况'}
        name="P_C_3_01"
      >
        <RadioReadOnly onlyRead={!editable} options={templateStatus} />
      </RowSpan>
      <RowSpan subTitle={'担保人本期是否出现住所、通讯地址、联系人、联系方式变更'} name="P_C_3_02">
        <RadioReadOnly onlyRead={!editable} options={templateStatus} />
      </RowSpan>
      <RowSpan subTitle={'担保人主要职能定位及经营业务是否发生重大变化'} name="P_C_3_03">
        <RadioReadOnly onlyRead={!editable} options={templateStatus} />
      </RowSpan>
      <RowSpan subTitle={'担保人融资渠道是否通畅'} name="P_C_3_04">
        <RadioReadOnly onlyRead={!editable} options={templateStatus} />
      </RowSpan>
      <RowSpan subTitle={'是否存在被关闭或划转兼并的明确安排'} name="P_C_3_05">
        <RadioReadOnly onlyRead={!editable} options={templateStatus} />
      </RowSpan>
      {isV2 && (
        <RowSpan subTitle={'担保人主要财务数据'} name="P_C_3_07" rules={[tableRequired()]}>
          <FormTable onlyRead={!editable} columns={columns} canAddDelete={false} />
        </RowSpan>
      )}
      <Form.Item dependencies={['P_C_3_01', 'P_C_3_02', 'P_C_3_03', 'P_C_3_04', 'P_C_3_05']}>
        {({ getFieldValue }) => {
          const required =
            getFieldValue('P_C_3_01') === '1' ||
            getFieldValue('P_C_3_02') === '1' ||
            getFieldValue('P_C_3_03') === '1' ||
            getFieldValue('P_C_3_04') === '0' ||
            getFieldValue('P_C_3_05') === '1'

          return (
            <RowSpan subTitle={'担保人经营情况补充说明'} name="P_C_3_06" required={required}>
              <TextAreaReadOnly onlyRead={!editable} placeholder="请填写补充说明" />
            </RowSpan>
          )
        }}
      </Form.Item>
      <div className={styles.titleRow}>客户经营情况分析</div>
      <CollectAnalysis {...baseProps}></CollectAnalysis>
      <div className={styles.titleRow}>租赁物</div>
      <RowSpan subTitle={'承租人是否将租赁物进行再次销售、转让'} name="P_C_4_01">
        <RadioReadOnly onlyRead={!editable} options={templateStatus} />
      </RowSpan>
      <RowSpan subTitle={'承租人是否将租赁物进行了转租'} name="P_C_4_02">
        <RadioReadOnly onlyRead={!editable} options={templateStatus} />
      </RowSpan>

      <RowSpan subTitle={'承租人是否将租赁物进行了抵押、质押'} name="P_C_4_03">
        <RadioReadOnly onlyRead={!editable} options={templateStatus} />
      </RowSpan>
      <RowSpan subTitle={'承租人是否将租赁物进行了投资入股、抵偿'} name="P_C_4_04">
        <RadioReadOnly onlyRead={!editable} options={templateStatus} />
      </RowSpan>
      <RowSpan
        subTitle={'承租人是否将租赁物进行诉讼担保、是否对租赁物进行了保全担保等处置行为'}
        name="P_C_4_05"
      >
        <RadioReadOnly onlyRead={!editable} options={templateStatus} />
      </RowSpan>

      <RowSpan
        subTitle={'承租人是否以其他任何方式进行了侵害出租人对租赁设备的所有权的行为'}
        name="P_C_4_06"
      >
        <RadioReadOnly onlyRead={!editable} options={templateStatus} />
      </RowSpan>
      <RowSpan subTitle={'租赁物是否能够正常使用'} name="P_C_4_07">
        <RadioReadOnly onlyRead={!editable} options={templateStatus} />
      </RowSpan>

      <RowSpan subTitle={'租赁物是否发生过升级换代、改造'} name="P_C_4_08">
        <RadioReadOnly onlyRead={!editable} options={templateStatus} />
      </RowSpan>

      <RowSpan subTitle={'租赁物的位置是否被移动'} name="P_C_4_09">
        <RadioReadOnly onlyRead={!editable} options={templateStatus} />
      </RowSpan>
      <RowSpan subTitle={'租赁物是否发生过重大停产停运、重大故障、维修情况'} name="P_C_4_10">
        <RadioReadOnly onlyRead={!editable} options={templateStatus} />
      </RowSpan>

      <RowSpan subTitle={'租赁物情况补充说明'} name="P_C_4_11">
        <TextAreaReadOnly onlyRead={!editable} />
      </RowSpan>
    </div>
  )
}

export default observer(Index)
