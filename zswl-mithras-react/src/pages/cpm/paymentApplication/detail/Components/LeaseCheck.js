import EditDescription from '@/components/Table/EditDescription'
import { observer } from '@zswl/admin'
import { useEffect, useState } from 'react'
import Api from '@/pages/cpm/paymentApplication/detail/api'

function Index({ id }) {
  const [detail, setDetail] = useState({})

  const getData = async () => {
    const res = await Api.postLeaseitemCheckrepeat({ id })
    setDetail({
      repeatDate: res?.repeatDate,
    })
  }
  useEffect(() => {
    getData()
  }, [id])

  return (
    <EditDescription
      title="租赁物查重"
      detail={detail}
      canEdit={false}
      columns={[
        {
          title: '中登网查重日期',
          dataIndex: 'repeatDate',
          render: (value) => {
            return value ?? '-'
          },
        },
      ]}
    />
  )
}

export default observer(Index)
