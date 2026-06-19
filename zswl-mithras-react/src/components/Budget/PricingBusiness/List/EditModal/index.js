import { observer } from '@zswl/admin'
import { Form, Modal, Select } from '@zswl/components'
import { DatePicker } from 'antd'
import styles from './index.less'

const { Item } = Form
function EditModal({ store }) {
  return (
    <Modal title={'创建'} store={store} okText={'确定'} destroyOnClose>
      <Form labelCol={{ span: 6 }} preserve={true}>
        <Item
          label={'定价频率'}
          name={'pricingFrequency'}
          rules={[{ required: true, message: '请选择定价频率' }]}
        >
          <Select options={'pricingFrequencyEnum'}></Select>
        </Item>

        <Item dependencies={['pricingFrequency']} noStyle>
          {({ getFieldValue }) => {
            const isQuarter = getFieldValue('pricingFrequency') !== 'MONTHLY'
            return (
              <Item
                label={'时间'}
                name={'date'}
                rules={[{ required: true, message: '请选择时间' }]}
              >
                {isQuarter ? <DatePicker picker="quarter" /> : <DatePicker.MonthPicker />}
              </Item>
            )
          }}
        </Item>
        <div>参数影响面说明</div>
        <div className={styles.content}>
          <div>1.将影响“十年期国债收益率”、“一年期shibor利率”、“LPR”波动水平的计算规则：</div>
          <div className={styles.ml4}>
            1)如定价频率=月度，则波动计算式为：指标<span className={styles.prominent}>上月度</span>
            值-指标<span className={styles.prominent}>上上月度</span>值
          </div>
          <div className={styles.ml4}>
            2)如定价频率=季度，则波动计算式为：指标<span className={styles.prominent}>上季度</span>
            值-指标<span className={styles.prominent}>上上季度</span>值
          </div>
          <div> 2.将影响“融资成本趋势”的计算规则：</div>
          <div className={styles.ml4}>
            1)如定价频率=月度，则波动计算式为：指标<span className={styles.prominent}>上月度</span>
            值-指标<span className={styles.prominent}>当年平均</span>值
          </div>
          <div className={styles.ml4}>
            2)如定价频率=季度，则波动计算式为：指标<span className={styles.prominent}>上季度</span>
            值-指标<span className={styles.prominent}>当年平均</span>值
          </div>
          <div> 3.将影响创建FTP弹窗中的日历选项： </div>
          <div className={styles.ml4}>
            a.如定价频率=月度，则日历中可选择<span className={styles.prominent}>月份</span>
          </div>
          <div className={styles.ml4}>
            b.如定价频率=季度，则日历中可选择<span className={styles.prominent}>季度</span>
          </div>
        </div>
      </Form>
    </Modal>
  )
}

export default observer(EditModal)
