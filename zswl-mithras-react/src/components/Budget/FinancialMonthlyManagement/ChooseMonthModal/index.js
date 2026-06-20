import { observer } from '@zswl/admin'
import { Modal, Form } from '@zswl/components'
import { DatePicker } from 'antd'
import { rules } from '@/utils'
const { Item } = Form

const Index = ({ store }) => {
  const disabledDate = (current) => {
    const currentDate = new Date()
    const currentMonth = currentDate.getMonth()
    const currentYear = currentDate.getFullYear()
    // 获取待比较日期的月份
    const targetMonth = current.month()
    // 获取待比较日期的年份
    const targetYear = current.year()
    // 如果待比较日期的年份大于当前年份，可选择
    if (targetYear > currentYear) {
      return false
    }
    // 如果待比较日期的年份等于当前年份，但月份大于等于当前月份，可选择
    if (targetYear === currentYear && targetMonth >= currentMonth) {
      return false
    }
    // 其他情况不可选择
    return true
  }
  return (
    <Modal title="创建月结" store={store.chooseMonthModal} width={400} destroyOnClose>
      <Form preserve={false}>
        <Item name="month" label="月份" rules={[rules.required('请选择')]}>
          <DatePicker picker="month" disabledDate={false}></DatePicker>
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(Index)
