
import { Table } from '@zswl/components'

export default function DashboardTableSummary({
  columns,
  title = '总合计',
  sumData = {},
  startIndex = 1,
  align = 'right',
  style = {},
  initFormat = 1,
}) {
  return (
    <Table.Summary fixed>
      <Table.Summary.Row>
        <Table.Summary.Cell index={0}>{title}</Table.Summary.Cell>
        {flattenData(columns)
          .slice(startIndex)
          .map((item, index) => (
            <Table.Summary.Cell index={index + 1} key={item.dataIndex}>
              {item._columnType === 'amount' ? (
                <div style={{ textAlign: align, ...style }}>
                  <FormAmount.Format
                    value={sumData?.[`${item.dataIndex}`]}
                    initFormat={initFormat}
                  />
                </div>
              ) : (
                '-'
              )}
            </Table.Summary.Cell>
          ))}
      </Table.Summary.Row>
    </Table.Summary>
  )
}

function flattenData(arr) {
  let flattenedData = []

  arr.forEach((item) => {
    if (item.children) {
      item.children.forEach((child) => {
        let newItem = { ...item, ...child }
        delete newItem.children
        flattenedData.push(newItem)
      })
    } else {
      flattenedData.push(item)
    }
  })

  return flattenedData
}
