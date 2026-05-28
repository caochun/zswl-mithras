import { FiledFormat } from '@/components/Format'
import { Input } from 'antd'

const TextAreaEditable = ({ isEdit, ...props }) => {
  return !isEdit ? (
    <FiledFormat {...props} needWrap />
  ) : (
    <Input.TextArea placeholder="请输入" autoSize={{ minRows: 4, maxRows: 20 }} {...props} />
  )
}
export default TextAreaEditable
