import { PureAmountFormat } from '@/components/Format'
import { observer } from '@zswl/admin'

const renderTax = ({ label, value, unit = '' }) => {
  return (
    <div style={{ display: 'inline-block', marginRight: 20 }} key={label}>
      <span style={{ color: '#ff4d4f', marginRight: 4, 'font-family': 'SimSun, sans-serif' }}>
        *
      </span>
      <span>{label}：</span>
      <span>{!isNaN(value) ? `${value}${unit}` : value}</span>
    </div>
  )
}

const Index = ({ store, currentTableData ,showTips}) => {
  const { getActualTaxByReceiptId } = store


  const column = [
    {
      label: '税率',
      value: PureAmountFormat(getActualTaxByReceiptId(currentTableData.receiptId, 'taxRate')),
      unit: '%',
    },
    {
      label: '不含税利息',
      value: PureAmountFormat(
        getActualTaxByReceiptId(currentTableData.receiptId, 'excludingInterestTax')
      ),
    },
    {
      label: '税额',
      value: PureAmountFormat(getActualTaxByReceiptId(currentTableData.receiptId, 'tax')),
    },
    {
      label: '不含税租金',
      value: PureAmountFormat(
        getActualTaxByReceiptId(currentTableData.receiptId, 'rentExcludingTax')
      ),
    },
    // 保理：无需显示印花税字段
    currentTableData.bizType !== 'BL' && {
      label: '印花税',
      value: PureAmountFormat(getActualTaxByReceiptId(currentTableData.receiptId, 'stampDuty')),
    },
  ].filter(Boolean)

  return (
    <>
   {showTips && <div style={{ color: '#ff4d4f', marginBottom: 12 }}>合同加权平均IRR低于最低IRR要求，请注意！</div>}
      {column.map((item) => {
        return renderTax(item)
      })}
    </>
  )
}

export default observer(Index)
