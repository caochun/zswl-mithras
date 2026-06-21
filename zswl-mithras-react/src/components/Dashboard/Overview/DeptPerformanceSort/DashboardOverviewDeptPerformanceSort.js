import Title from '../../OverviewTitle'
import ProjectStage from '../../MyAchievement/Department/DashboardDepartmentAchievement'

const DashboardOverviewDeptPerformanceSort = ({ title }) => {
  return (
    <>
      <Title title={title}></Title>
      <div style={{ padding: 20 }}>
        <ProjectStage></ProjectStage>
      </div>
    </>
  )
}
export default DashboardOverviewDeptPerformanceSort
