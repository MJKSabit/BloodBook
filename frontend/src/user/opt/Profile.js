import Posts from "./Posts"
import UserCard from "./UserCard"
import store from "../../store"
import { Box, CircularProgress, Paper } from "@material-ui/core"

const Profile = props => {
  if (!store.getState().profile)
  return <Box display='flex' style={{alignItems: 'center', justifyContent: 'center'}} my={5}> <CircularProgress /></Box>

  const username = store.getState().profile.user.username
  return (<>
    <UserCard user={store.getState().profile} />
    <Posts url={`/user/posts/${username}`} label='My Posts' />
  </>)
}

export default Profile