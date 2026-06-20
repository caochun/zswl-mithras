import { hasValue } from '@/utils'

const FormItemContent = ({ formContent, value, showValue, isChange, formDataShow = false }) => {
  if (showValue) {
    return (
      <>
        <span style={{ color: isChange ? 'red' : undefined }}>{hasValue(value) ? value : '-'}</span>
        {formDataShow && <span style={{ display: 'none' }}>{formContent}</span>}
      </>
    )
  } else {
    return formContent
  }
}

export default FormItemContent
