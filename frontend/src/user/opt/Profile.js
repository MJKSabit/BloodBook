import Posts from "./Posts"
import UserCard from "./UserCard"
import store from "../../store"

const Profile = props => {
  if (!store.getState().profile)
    return null

  const username = store.getState().profile.user.username
  return (<>
    <UserCard user={store.getState().profile} />
    <Posts url={`/user/posts/${username}`} label='My Posts' />
  </>)
}

export default Profile