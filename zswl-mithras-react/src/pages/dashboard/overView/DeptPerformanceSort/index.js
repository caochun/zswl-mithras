import { DashboardOverviewTitle as Title } from '@/components/Dashboard/DashboardEntries'
import { MyAchievementDepartment as ProjectStage } from '@/components/Dashboard/MyAchievementEntries'

const Index = ({ title }) => {
  return (
    <>
      <Title title={title}></Title>
      <div style={{ padding: 20 }}>
        <ProjectStage></ProjectStage>
      </div>
    </>
  )
}
export default Index
