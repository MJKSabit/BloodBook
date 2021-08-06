import Posts from "./Posts"
import UserCard from "./UserCard"
import store from "../../store"
import { Box, CircularProgress } from "@material-ui/core"
import { useState } from "react"

const Profile = props => {
  const [createdPost, setCreatedPost] = useState(null)

  if (!store.getState().profile)
  return <Box display='flex' style={{alignItems: 'center', justifyContent: 'center'}} my={5}> <CircularProgress /></Box>

  const username = store.getState().profile.user.username
  return (<>
    <UserCard user={store.getState().profile} onPosted={setCreatedPost}/>
    <Posts url={`/user/posts/${username}`} label='My Posts' createdPost={createdPost}/>
  </>)
}

export default Profile