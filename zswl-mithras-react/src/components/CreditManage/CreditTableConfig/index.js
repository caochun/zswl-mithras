import { formatAmountWan } from '@/components/Format'

export const CREATETABLE_PARAMS = 'CREATETABLE_PARAMS'

export const AmountFormat = (value) => {
  return formatAmountWan(value)
}

export const getHeaderWithFunctionCode = ({ channel, humpPath }) => {
  const functionCode = ['PROC', 'EDIT'].includes(channel) ? humpPath : humpPath + 'Effect'
  return {
    headers: {
      functionCode,
    },
  }
}
