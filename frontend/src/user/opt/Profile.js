import Posts from "./Posts"
import UserCard from "./UserCard"
import store from "../../store"
import { Paper } from "@material-ui/core"

const Profile = props => {
  if (!store.getState().profile)
    return <Paper style={{padding: '20px'}}>Still Loading...</Paper>

  const username = store.getState().profile.user.username
  return (<>
    <UserCard user={store.getState().profile} />
    <Posts url={`/user/posts/${username}`} label='My Posts' />
  </>)
}

export default Profile