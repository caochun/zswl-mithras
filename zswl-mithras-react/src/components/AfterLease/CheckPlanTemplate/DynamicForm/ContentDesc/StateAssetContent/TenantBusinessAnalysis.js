import { observer } from '@zswl/admin'
import { TextAreaReadOnly } from '@/components/Form/FormRead'
import { Row, Col } from 'antd'
import { Form } from '@zswl/components'
import FormTable from '@/components/Form/Table'
import styles from '../style.less'
import RowSpan from '../RowSpan'
import { tableRequired, columns2, columns } from '../Utils'

/**
 * 承租人经营情况分析组件
 * @param {Object} props - 组件属性
 * @param {boolean} props.editable - 是否可编辑
 * @param {boolean} props.required - 是否必填
 * @param {string} props.namePrefix - 表单字段名前缀，默认为 'BUS'
 */
const TenantBusinessAnalysis = observer(({ editable, required = true, namePrefix }) => {
  return (
    <>
      <div className={styles.titleRow}>承租人经营情况</div>
      <RowSpan
        subTitle={'承租人经营情况分析'}
        name={`${namePrefix}_C_1_01`}
        tooltip="对主营业务数据变化情况、人员稳定性及工资发放情况、投资计划及融资变化等进行分析（公交类项目补充对票款收入、财政补贴收入、车辆总数、线路数量等的说明和变动分析）"
      >
        <TextAreaReadOnly onlyRead={!editable} />
      </RowSpan>

      {/* 承租人主要财务数据表格 */}
      <Row className={styles.row}>
        <Col span={8} className={styles.subTitle}>
          承租人主要财务数据
        </Col>
        <Col span={16} className={styles.right}>
          <Form.Item
            label={''}
            name={`${namePrefix}_C_2_01`}
            className={styles.tableItem}
            required={required}
            rules={required && [tableRequired()]}
          >
            <FormTable onlyRead={!editable} columns={columns} canAddDelete={false} />
          </Form.Item>
        </Col>
      </Row>
    </>
  )
})

export default TenantBusinessAnalysis
