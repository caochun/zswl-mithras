import Title from '@/components/Dashboard/OverviewTitle'
import ProjectStage from '@/components/Dashboard/MyAchievement/Department'

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
