import { FounderSelect, ClientSelect, OrgSelect, ProvinceSelect } from '@/components/Select'
import { FiledFormat, InputColumn } from './index'

export const orgSelect = (props = {}, functionCode = 'dashboardWorkbenchSelectOrgs') => {
  const { mode, ...rest } = props
  return InputColumn({
    title: '业务部门',
    dataIndex: 'bizDeptId',
    search: {
      element: <OrgSelect mode={mode} functionCode={functionCode} />,
    },
    render: (value, { bizDeptName }) => <FiledFormat title={bizDeptName}></FiledFormat>,
    ...rest,
  })
}

export const clientSelect = (props) => {
  return InputColumn({
    title: '客户名称',
    dataIndex: 'clientId',
    search: {
      element: <ClientSelect canJump={false} />,
      functionCode: 'dashboardWorkbenchClientList',
    },
    render: (value, { clientName }) => <FiledFormat title={clientName}></FiledFormat>,
    ...props,
  })
}

export const founderSelect = (props = {}) => {
  const { params, ...rest } = props
  return InputColumn({
    title: '项目主办',
    dataIndex: 'projSponsorUserId',
    search: {
      element: <FounderSelect params={params} />,
      functionCode: 'dashboardWorkbenchSelectFounder',
    },
    render: (value, { projSponsorUserName }) => (
      <FiledFormat title={projSponsorUserName}></FiledFormat>
    ),
    ...rest,
  })
}

export const provinceSelect = (props) => {
  return InputColumn({
    title: '省份',
    dataIndex: 'provinceCode',
    search: {
      element: <ProvinceSelect />,
    },
    render: (value, { provinceDisplay }) => <FiledFormat title={provinceDisplay}></FiledFormat>,
    ...props,
  })
}
