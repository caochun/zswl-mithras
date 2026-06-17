import { observer } from '@zswl/admin'
import { App, Table, TableStore } from '@zswl/components'
import { TextAreaColumn } from '@/components/Format'
import { Collapse, Radio, Row, Tooltip } from 'antd'
import { options } from '@/utils'
import { useEffect, useMemo, useState } from 'react'
import { DynamicFormItem } from '@/components/Customer/RatingForm'
import { saveServer } from '@/utils'

const { approvalStatus } = options

const { Panel } = Collapse

const Index = ({ info, isFormApproval, title, auth }) => {
  const columns = [
    { title: `${title}名称`, dataIndex: 'fieldComment' },
    {
      title: '所选档位',
      dataIndex: 'value',
      render: (value, record) => {
        const title = record.enumList ? App.matchOption(record.enumList, value)?.label : value
        return <Tooltip title={title}>{title}</Tooltip>
      },
    },
    isFormApproval && {
      title: '审批意见',
      dataIndex: 'approveStatus',
      editable: () => {
        return <Radio.Group options={approvalStatus} />
      },
    },
    isFormApproval &&
      TextAreaColumn({
        title: '审批说明',
        dataIndex: 'approveStatus',
        editable: true,
      }),
  ].filter(Boolean)
  const [infoList, setInfoList] = useState([])
  const [activeKey, setActiveKey] = useState([])
  const formatInfo = (info) => {
    const result = []
    Object.entries(info).forEach(([key, value]) => {
      const newList = value.filter((v) => ['number'].includes(v.dataType))
      const table = value.filter((v) => !['number'].includes(v.dataType))

      result.push({ groupName: key, list: newList, table })
    })
    setActiveKey(result.map(({ groupName }) => groupName))
    setInfoList(result)
  }
  useEffect(() => {
    info && formatInfo(info)
  }, [JSON.stringify(info)])

  return (
    <>
      <div style={{ fontWeight: 800, fontSize: 16, padding: '12px 0' }}>{title}</div>
      <Collapse activeKey={activeKey} onChange={setActiveKey}>
        {infoList.map(({ groupName, list, table }, index) => (
          <Panel header={groupName} key={groupName}>
            <Row gutter={12}>
              {list.map((item) => (
                <DynamicFormItem record={item} disabled={!auth} key={item.id} />
              ))}
            </Row>
            {!!table.length && <Table         columnsFilter={'debtRat_detail_ScoreReport'}
                    onFilter={(key,val) => saveServer('debtRat_detail_ScoreReport',val)} columns={columns} dataSource={table} pagination={false} />}
          </Panel>
        ))}
      </Collapse>
    </>
  )
}

export default observer(Index)
