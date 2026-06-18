import { useEffect, useMemo } from 'react'
import { DatePicker, Tabs, Space } from 'antd'
import { observer } from '@zswl/admin'
import DataUpload from '@/components/DataUpload'
import { DownloadTemplateAction as DownloadTemplate } from '@/components/Actions'
import { Page, Button } from '@zswl/components'
import { EditDescription } from '@/components/Table'
import { MatchOptionColumn } from '@/components/Format'
import CompanyGoal from './CompanyGoal'
import DepartGoal from './DepartGoal'
import PersonGoal from './PersonGoal'
import Store from './Store'

const Index = ({ params: { id }, path }) => {
  const store = useMemo(() => {
    return new Store()
  }, [])

  useEffect(() => {
    store.getManageList()
  }, [])

  const detail = store.page.getData()
  return (
    <Page store={store} params={{ id }}>
      <EditDescription
        saveData={store.saveData}
        detail={detail}
        canEdit={true}
        columns={[
          {
            title: '年度',
            dataIndex: 'year',
            requiredMark: true,
            editable: false,
            // editable: (val) => {
            //   return {
            //     initialValue: (detail.year && moment(detail.year)) || undefined,
            //     element: <DatePicker picker="year"></DatePicker>,
            //     required: true,
            //     rules: [{ required: true, message: '请选择' }],
            //     transform: (date) => ({
            //       year: date && moment(date).format('yyyy'),
            //     }),
            //   }
            // },
          },
          MatchOptionColumn({
            title: '状态',
            dataIndex: 'status',
            editable: true,
            requiredMark: true,
            matchOption: 'earlyWarningState',
          }),
        ]}
      />
      <h3 style={{ marginTop: 20 }}>业绩目标</h3>
      <Tabs
        tabBarExtraContent={
          <Space>
            <DownloadTemplate
              params={{
                templateName: 'BUSINESS_TARGET_DATA',
                moduleType: 'KPI_PARAMETER_CONFIG',
              }}
            />
            <Button onClick={store.handleExport}>Excel导出</Button>
            <DataUpload accept=".xlsx" maxCount={1} onChange={store.handleImport}>
              <Button type="primary">Excel导入</Button>
            </DataUpload>
          </Space>
        }
        items={[
          {
            label: `公司整体目标`,
            key: '1',
            children: <CompanyGoal store={store} />,
          },
          {
            label: `各部门业绩目标`,
            key: '2',
            children: <DepartGoal store={store} />,
          },
          {
            label: `业务人员业绩目标`,
            key: '3',
            children: <PersonGoal store={store} />,
          },
        ]}
      ></Tabs>
    </Page>
  )
}

export default observer(Index)
