import { Avatar, Box, Button, CircularProgress, IconButton, Link, Menu, MenuItem, Paper, Tooltip, Typography } from "@material-ui/core"
import { FileCopyOutlined, Info, LocationOn, MoreVert, QueryBuilder, VisibilityOutlined } from "@material-ui/icons";
import { useEffect, useState } from "react"
import { Link as RouterLink, useHistory } from "react-router-dom";
import LocationViewer from "../generic/LocationViewer";
import store from "../store"
import { deleteEvent, getEvents, notifyUser } from "../store/action";
import '../user/opt/posts.css'
import '../user/opt/profile.css'

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
    setHasMore(true)
    setEvents([])
    setLoading(true)
    getEvents(url, 0).then(response => {
      setEvents(response.content)
      setHasMore(response.hasNext)
      setPage(response.page+1)
      setLoading(false)
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
          <CircularProgress style={{visibility: loading ? 'visible' : 'hidden', marginRight: '20px'}} /> Load More
        </Button>
      </div>
    </div>
  )
}

export const Event = props => {
  const {userType} = props
  const [anchorEl, setAnchorEl] = useState(null);
  const [event, setEvent] = useState(props.event)

  const history = useHistory()

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
                  <Tooltip title={new Date(event.posted).toLocaleString('en-GB')}  placement='right'>
                    <Typography variant='body2'>
                      {new Date(event.posted).toLocaleDateString('en-GB')}
                    </Typography>
                  </Tooltip>
                </div>
                <div className={'post-info-container'}>
                    <div className={'profile-entry-container'}>
                        <QueryBuilder style={{marginRight:'8px'}}/>
                        At: {new Date(event.eventDate).toLocaleDateString('en-GB')}
                    </div>
                    <div className={'profile-entry-container'}>
                        <LocationOn style={{marginRight:'8px'}}/>
                          <LocationViewer location={event.location} />
                    </div>
                    <div className={'profile-entry-container'}>
                        <Info style={{marginRight:'8px'}}/>
                        <div style={{whiteSpace: "pre-wrap"}}>{event.info}</div>
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
                  }}><FileCopyOutlined style={{paddingRight: '8px'}}/> Copy Link</MenuItem>
                  <MenuItem onClick={(e) => {
                    handleClose()
                    history.push(eventLink)
                  }}><VisibilityOutlined style={{paddingRight: '8px'}}/> View Event</MenuItem>
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