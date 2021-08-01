import Posts from "./Posts"
import UserCard from "./UserCard"
import store from "../../store"

const Profile = props => {
  return (<>
    <UserCard user={store.getState().profile} />
    <Posts />
  </>)
}

export default Profile