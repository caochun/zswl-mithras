import { http, observer } from '@zswl/admin'
import { DescStore, Descriptions } from '@zswl/components'
import { useMemo, useState } from 'react'
import DataFileList from './DataFileList'

const labelStyle = {
  // color: 'red',
  background: '#F5F6FA',
  width: 180,
}
const contentStyle = {
  minWidth: 230,
  maxWidth: 320,
}

const Reason = ({ isFormApproval, processInstanceId }) => {
  if (!isFormApproval) return null
  const desc = useMemo(() => {
    return new DescStore({
      request: async () => {
        const res = await http.post('/cr/batch/reason', { processInstanceId })
        return res
      },
    })
  }, [])
  const batchNo = desc.getData()?.batchNo
  return (
    <div>
      <Descriptions
        store={desc}
        contentStyle={contentStyle}
        labelStyle={labelStyle}
        items={[
          {
            title: '报送说明',
            dataIndex: 'reportDescription',
          },
        ]}
      ></Descriptions>
      {batchNo && <DataFileList batchNo={batchNo}></DataFileList>}
    </div>
  )
}

export default observer(Reason)
