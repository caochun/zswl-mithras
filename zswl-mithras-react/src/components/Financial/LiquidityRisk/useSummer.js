import { FormAmount } from '@/components/Form'
import { Table } from '@zswl/components'
import { Typography } from 'antd'
import { useCallback } from 'react'

const { Text } = Typography

export default function useSummer({ columns, sumData, pageSum }) {
  const summary = useCallback(
    (pageData) => {
      return (
        <>
          <Table.Summary.Row>
            <Table.Summary.Cell index={0}>小计</Table.Summary.Cell>
            {columns.slice(1).map((item, index) => (
              <Table.Summary.Cell index={index + 1} key={item.dataIndex}>
                <div style={{ textAlign: 'right' }}>
                  {item._columnType === 'amount' ? (
                    <FormAmount.Format
                      value={pageSum[`${item.dataIndex}`]}
                      initFormat={10000 * 10000}
                    />
                  ) : (
                    '-'
                  )}
                </div>
              </Table.Summary.Cell>
            ))}
          </Table.Summary.Row>
          <Table.Summary.Row>
            <Table.Summary.Cell index={0}>总计</Table.Summary.Cell>
            {columns.slice(1).map((item, index) => (
              <Table.Summary.Cell index={index + 1} key={item.dataIndex}>
                <div style={{ textAlign: 'right' }}>
                  {item._columnType === 'amount' ? (
                    <FormAmount.Format
                      value={sumData[`${item.dataIndex}`]}
                      initFormat={10000 * 10000}
                    />
                  ) : (
                    '-'
                  )}
                </div>
              </Table.Summary.Cell>
            ))}
          </Table.Summary.Row>
        </>
      )
    },
    [columns, sumData, pageSum]
  )
  return summary
}
