import Posts from "./Posts"
import UserCard from "./UserCard"
import store from "../../store"
import { useParams } from "react-router-dom"
import { useState } from "react"
import { useEffect } from "react"
import { getUserProfile } from "../../store/action"

const OtherProfile = props => {
  let {username} = useParams()
  const [profile, setProfile] = useState(null)

  useEffect( () => {
    getUserProfile(username).then(
      data => setProfile(data),
      err => console.log(err)
    )
  }, [])

  if (profile === null) 
    return null

  return (<>
    <UserCard user={profile} />
    <Posts url={`/user/posts/${username}`} />
  </>)
}

export default OtherProfile