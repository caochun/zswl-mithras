import { observer } from '@zswl/admin'
import { App, Table } from '@zswl/components'
import moment from 'moment'
import { useEffect, useState } from 'react'
import { saveServer } from '@/utils'

const getCurrentDate = () => moment().format('YYYY-MM-DD HH:mm:ss')
export const Date = () => {
  const [date, setDate] = useState(getCurrentDate)
  useEffect(() => {
    const timer = setInterval(() => {
      setDate(getCurrentDate)
    }, 1000)
    return () => {
      clearInterval(timer)
    }
  }, [])
  return date
}
const BlackGrayLoginInfo = ({ showTitle = true }) => {
  const { userName, orgRolesName } = App.useData().user

  return (
    <>
      {showTitle && <div className="z-sub-title">操作人信息</div>}
      <Table
        columnsFilter={'blackListManage_components_LoginInfo'}
                onFilter={(key,val) => saveServer('blackListManage_components_LoginInfo',val)}
        
        pagination={false}
        rowKey={'userName'}
        dataSource={[{ userName, org: orgRolesName?.map((item) => item.orgName).join(',') }]}
        columns={[
          { title: '操作人', dataIndex: 'userName' },
          {
            title: '操作时间',
            dataIndex: 'date',
            render() {
              return <Date />
            },
          },
          { title: '所属机构', dataIndex: 'org' },
        ]}
      />
    </>
  )
}

export default observer(BlackGrayLoginInfo)
