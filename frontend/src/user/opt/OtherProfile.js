import Posts from "./Posts"
import UserCard from "./UserCard"
import store from "../../store"
import { useParams } from "react-router-dom"
import { useState } from "react"
import { useEffect } from "react"
import { getUserProfile } from "../../store/action"
import { Paper } from "@material-ui/core"

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
    return <Paper style={{padding: '20px'}}>Still Loading...</Paper>

  return (<>
    <UserCard user={profile} />
    <Posts url={`/user/posts/${username}`} label='Posts'/>
  </>)
}

export default OtherProfile