import { Avatar, Box, Button, IconButton, Link, Menu, MenuItem, Paper, Typography } from "@material-ui/core"
import { Info, LocationOn, MoreVert, QueryBuilder } from "@material-ui/icons";
import { useEffect, useState } from "react"
import { Link as RouterLink } from "react-router-dom";
import store from "../store"
import { deleteEvent, getEvents, notifyUser } from "../store/action";
import '../user/opt/posts.css'
import '../user/opt/profile.css'
import { getMapLink } from "../user/opt/UserCard";

const Events = props => {
  let {url, label, userType} = props
  if (!userType) userType = 'bloodbank'

  const [events, setEvents] = useState([])
  const [hasMore, setHasMore] = useState(true)
  const [page, setPage] = useState(0)
  const [loading, setLoading] = useState(true)

  const fetchData = () => {
    if (url && hasMore) {
      setLoading(true)
      getEvents(url, page).then(response => {        
        setEvents(events.concat(response.content))
        setHasMore(response.hasNext)
        setPage(response.page+1)
        setLoading(false)
      })
    }
  }

  useEffect( () => {
    setEvents([])
    getEvents(url, 0).then(response => {
      setEvents(response.content)
      setHasMore(response.hasNext)
      setPage(response.page+1)
    })
  }, [url])

  return(
    <div>
      {label && <div>
        <Box mt={5} mb={5} pl={3}>
          <Typography color='textSecondary' variant='h5'>
            {label}
          </Typography>
        </Box>
      </div>}
      <div className={'posts-list-container'}>
          {events.map( event => (<Event event={event} userType={userType} key={event.id}/>))}
      </div>
      <div className={'post-list-container'} style={{visibility: hasMore ? 'visible': 'hidden'}}>
        <Button onClick={() => fetchData()} disabled={loading} fullWidth={true}>
          Load More
        </Button>
      </div>
    </div>
  )
}

export const Event = props => {
  const {userType} = props
  const [anchorEl, setAnchorEl] = useState(null);
  const [event, setEvent] = useState(props.event)

  if (event === null)
    return null

  const editAccess = event.user.user.username === store.getState().profile.user.username

  const handleClick = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const bankLink = (userType === 'bloodbank' ?
    `/bloodbank/profile/${event.user.user.username}`:
    `/user/bloodbank/${event.user.user.username}`)

  const eventLink = (userType === 'bloodbank' ?
  `/bloodbank/event/${event.id}`:
  `/user/event/${event.id}`)

  return(
    <Paper>
        <div className={'post-container'}>
            <div className={'post-left'}>
                <Avatar style={{width:'60px',height:'60px'}} src={event.user.imageURL}/>
            </div>
            <div className={'post-mid'}>
                <div className={'post-name'}>
                  <Link component={RouterLink} to={
                    bankLink
                  } variant='body1'> {event.user.name} </Link>
                </div>
                <div className={'post-date'}>
                <Link component={RouterLink} to={eventLink} variant='body2'>
                  {new Date(event.posted).toLocaleDateString()}
                </Link>
                </div>
                <div className={'post-info-container'}>
                    <div className={'profile-entry-container'}>
                        <QueryBuilder style={{marginRight:'8px'}}/>
                        At: {new Date(event.eventDate).toLocaleDateString()}
                    </div>
                    <div className={'profile-entry-container'}>
                        <LocationOn style={{marginRight:'8px'}}/>
                        <Link 
                          href={getMapLink(event.location.latitude, event.location.longitude)}
                          rel="noopener noreferrer" 
                          target="_blank">
                          {`${event.location.latitude}, ${event.location.longitude} ↗️`}
                        </Link>
                    </div>
                    <div className={'profile-entry-container'}>
                        <Info style={{marginRight:'8px'}}/>
                        {event.info}
                    </div>
                </div>
            </div>
            <div className={'post-menu'}>
                <IconButton onClick={handleClick}><MoreVert/> </IconButton>
                <Menu
                  id="simple-menu"
                  anchorEl={anchorEl}
                  keepMounted
                  open={Boolean(anchorEl)}
                  onClose={handleClose}
                >
                  <MenuItem onClick={(e) => {
                    handleClose()
                    navigator.clipboard.writeText(`${window.location.origin}${eventLink}`).then(
                      () => store.dispatch(notifyUser('Copied!')),
                      () => store.dispatch(notifyUser('Can not copy!'))
                    )
                  }}>Copy Link</MenuItem>
                  { editAccess && 
                    <MenuItem onClick={(e) => {
                      handleClose(e)
                      deleteEvent(event.id).then(
                        () => setEvent(null),
                        err => console.log(err)
                      )
                    }}>Delete</MenuItem>
                  }
                </Menu>
            </div>
        </div>
    </Paper>
  )  
}

export default Events