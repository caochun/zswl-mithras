import Title from '@/pages/dashboard/overView/components/Title'
import ProjectStage from '@/pages/dashboard/workbench/MyAchievement/Department'

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
