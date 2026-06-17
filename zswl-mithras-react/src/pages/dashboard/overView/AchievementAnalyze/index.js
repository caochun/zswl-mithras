import { Row, Col } from 'antd'
import Title from '@/components/Dashboard/OverviewTitle'
import LaunchComplete from '@/components/Dashboard/OperationView/LaunchComplete'
import ProjectStage from '@/components/Dashboard/ProjectView/ProjectStage'
import { initYearQueryDate } from '@/utils/dashboardOperation'
import DeptIncomeByMonthly from './DeptIncomeByMonthly'
import ThrowIncomeRate from './ThrowIncomeRate'

const Index = ({ title }) => {
  return (
    <>
      <Title title={title}></Title>
      <LaunchComplete
        innerModule={true}
        initialQuery={{ queryDate: initYearQueryDate }}
      ></LaunchComplete>
      {/* <DeptIncomeByMonthly></DeptIncomeByMonthly> */}
      <ThrowIncomeRate></ThrowIncomeRate>
      <ProjectStage innerModule={true} title="业务各阶段信息卡片"></ProjectStage>
    </>
  )
}
export default Index
