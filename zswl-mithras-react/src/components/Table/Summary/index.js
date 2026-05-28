import FormAmount from '@/components/Form/FormAmount'
import { Table } from '@zswl/components'

export default function Index({ columns, sumData = {}, startIndex = 1, initFormat = 10000 }) {
  const format = (columns) => {
    if (!columns?.length) return []
    const result = []
    const flatten = (cols) => {
      cols.forEach((col) => {
        if (col.children) {
          flatten(col.children)
        } else {
          result.push(col)
        }
      })
    }
    flatten(columns)
    return result
  }
  const newColumns = format(columns).slice(startIndex)
  return (
    <Table.Summary fixed>
      <Table.Summary.Row>
        <Table.Summary.Cell index={0}>总计</Table.Summary.Cell>
        {newColumns.map((item, index) => (
          <Table.Summary.Cell index={index + 1} key={item.dataIndex}>
            {item._columnType === 'amount' ? (
              <div style={{ textAlign: 'right' }}>
                <FormAmount.Format
                  value={sumData?.[`${item.dataIndex}`]}
                  initFormat={initFormat}
                  suffix={item.suffix}
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
