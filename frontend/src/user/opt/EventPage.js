import { Box, CircularProgress, Paper } from "@material-ui/core"
import { useEffect } from "react"
import { useState } from "react"
import { useParams } from "react-router-dom"
import { Event } from "../../bank/Events"
import { getEvent } from "../../store/action"

const EventPage = props => {
  const {id} = useParams()
  const userType = props.userType || 'user'

  const [event, setEvent] = useState(null)

  useEffect( () => {
    getEvent(id).then(
      data => setEvent(data),
      err => console.log(err)
    )
  }, [])

  if (event === null) 
  return <Box display='flex' style={{alignItems: 'center', justifyContent: 'center'}} my={5}> <CircularProgress /></Box>

  return <Event event={event} userType={userType}/>
}

export default EventPage