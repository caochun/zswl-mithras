import { observer } from '@zswl/admin'
import Title from '../../Title'
import ProjectInfo from '../../ProjectView/ProjectInfo/DashboardProjectInfo'
import ProjectStage from '../../ProjectView/ProjectStage/DashboardProjectStage'
import ProjectView2 from '../ProjectView2'
import { getUserInfo } from '@/utils'
import AfterLease from './AfterLease'
import { isBaseinessDept } from '@/utils'

const Index = ({ title, iconType }) => {
  const is_wujie = getUserInfo().id === 49

  return (
    <div>
      <Title title={title} iconType={iconType}></Title>
      <ProjectStage />
      <div style={{ height: 20 }}></div>
      <ProjectInfo />
      <div style={{ height: 20 }}></div>
      {isBaseinessDept() ? <AfterLease /> : null}
      <div style={{ height: 20 }}></div>
      {is_wujie ? <ProjectView2 /> : null}
    </div>
  )
}

export default observer(Index)
