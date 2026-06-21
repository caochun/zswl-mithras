import { getFormItemProps } from '@/utils'
import { observer } from '@zswl/admin'
import { Modal, Form } from '@zswl/components'
import { useMemo } from 'react'
import moment from 'moment'
import ALL_COLUMNS from '../Column'
import styles from '../index.less'
import { FormAmount } from '@/components/Form'
import { DateColumn } from '@/components/Format'

const BudgetBusinessAgingDetailEditModal = ({ store }) => {
  const [form] = Form.useForm()
  const columns = useMemo(() => {
    const nameColumns = [
      { title: '核算组织名称', disabled: true },
      '期初款项原值',
      '本期增加额',
      '本期减少额',
      '期末款项原值（余额）',
      '币别',
      '科目名称',
      '款项内容',
      '客商编码（苍穹）',
      '业务日期',
      DateColumn({ title: '账龄截止日', dataIndex: 'agingDeadline' }),
    ]

    return [
      ...getFormItemProps(ALL_COLUMNS, nameColumns),
      <Form.Item dependencies={['businessDate']}>
        {({ getFieldValue, setFieldValue }) => {
          const businessDate = getFieldValue('businessDate')
          const agingDeadline = getFieldValue('agingDeadline')
          const businessAge = businessDate
            ? Math.floor(moment(agingDeadline).diff(moment(businessDate), 'day') / 30)
            : 0
          setFieldValue('businessAge', businessAge)
          return (
            <Form.Item label="账龄" name="businessAge">
              <FormAmount disabled initFormat={1} min={-Infinity} />
            </Form.Item>
          )
        }}
      </Form.Item>,
      ...getFormItemProps(ALL_COLUMNS, ['合同编号', '合同名称', '合同逾期日期']),
    ]
  }, [])
  return (
    <Modal
      title={`${store.isAddModal ? '新增' : '编辑'}账龄信息`}
      width={1000}
      store={store.addModal}
      destroyOnClose
    >
      <div className={styles.tips}> 新增“拨备”信息，请注意以下处理：</div>
      {[
        '*期初款项原值：可为负数，请于上次报送的“期末款项原值（余额）”保持一致',
        '*本期增加额：可为0',
        '*本期减少额：可为0',
        '*期末款项原值（余额）：可为负数，需满足"期末款项原值（余额）=期初款项原值+本期增加额-本期减少额"',
        '客户：系统会默认带出客商编码为fbzdy999999,名称为“其他"，若为其他客户请填写苍穹系统中的“客商编码”；',
        '业务日期：为合同起租日期，填写拨备时需与此前填写保持一致；',
      ].map((item, index) => (
        <div className={styles.tips}>
          {index + 1}.{item}
        </div>
      ))}
      <Form column={4} layout="vertical" items={columns} style={{ marginTop: 12 }}></Form>
    </Modal>
  )
}

export default observer(BudgetBusinessAgingDetailEditModal)
