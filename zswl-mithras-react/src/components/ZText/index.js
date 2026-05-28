import { formatPercent, amountFormat } from '@/utils'
const Index = ({ value, type = 'number', style = null, ...rest }) => {
  return (
    <text style={style}>
        {type === 'plain' ? value === '1' ? '回收' : value === '0' ? '不回收' : '' : amountFormat(formatPercent(value))}
    </text>
  )
}

export default Index
