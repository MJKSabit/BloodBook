import React from 'react'
import './posts.css'
import './profile.css'
import {Avatar, Box, Button, CircularProgress, Dialog, DialogActions, DialogContent, DialogTitle, IconButton, Menu, MenuItem, Tooltip, Typography} from "@material-ui/core";
import Paper from "@material-ui/core/Paper";
import MoreVertIcon from '@material-ui/icons/MoreVert';
import QueryBuilderIcon from '@material-ui/icons/QueryBuilder';
import InfoIcon from '@material-ui/icons/Info';
import { useState } from 'react';
import { useEffect } from 'react';
import { changePostManaged, deletePost, getPosts, notifyUser } from '../../store/action';
import { Link } from '@material-ui/core';
import { CheckCircle, CheckCircleOutlined, DeleteOutlined, FileCopyOutlined, LocationOn, VisibilityOutlined } from '@material-ui/icons';
import { Link as RouterLink, useHistory } from "react-router-dom";
import store from '../../store';
import LocationViewer from '../../generic/LocationViewer';

const Posts = props => {
  const {url, label, createdPost} = props
  const [posts, setPosts] = useState([])
  const [hasMore, setHasMore] = useState(true)
  const [page, setPage] = useState(0)
  const [loading, setLoading] = useState(false)

  useEffect( () => createdPost && setPosts([createdPost, ...posts])
  , [createdPost])

  const fetchData = (posts, more, page) => {
    if (url && more) {
      setLoading(true)
      getPosts(url, page).then(response => {
        setPosts(posts.concat(response.content))
        setHasMore(response.hasNext)
        setPage(response.page+1)
        setLoading(false)
      })
    }
  }

  useEffect( () => {
    setPosts([])
    setHasMore(true)
    setPage(0)
    fetchData([], true, 0)
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
              {posts.map( post => (<Post post={post} />))}
          </div>
          <div className={'post-list-container'} style={{visibility: hasMore ? 'visible': 'hidden'}}>
            <Button onClick={() => fetchData(posts, hasMore, page)} disabled={loading} fullWidth={true}>
              <CircularProgress style={{visibility: loading ? 'visible' : 'hidden', marginRight: '20px'}} /> Load More
            </Button>
          </div>
      </div>
  )
}

export const Post = props => {
  const [anchorEl, setAnchorEl] = React.useState(null);
  const [post, setPost] = useState(props.post)
  const [open, setOpen] = useState(false)

  const history = useHistory()

  if (post === null)
    return null

  const editAccess = post.user.user.username === store.getState().profile.user.username

  const handleClick = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

    return(
        <Paper>
            <div className={'post-container'}>
                <div className={'post-left'}>
                    <Avatar style={{width:'60px',height:'60px'}} src={post.user.imageURL}/>
                </div>
                <div className={'post-mid'}>
                    <div className={'post-name'}>
                      <Link component={RouterLink} to={`/user/profile/${post.user.user.username}`} variant='body1'> {post.user.name} </Link>
                    </div>
                    <div className={'post-date'}>
                      <Tooltip title={new Date(post.posted).toLocaleString('en-GB')} placement='right'>
                        <Typography to={`/user/post/${post.id}`} variant='body2'>
                          {new Date(post.posted).toLocaleDateString('en-GB')}
                        </Typography>
                      </Tooltip>
                    </div>
                    <div className={'post-info-container'}>
                        <div className={'profile-entry-container'}>
                            <CheckCircle style={{marginRight:'8px'}}/>
                            Status: {`${post.managed?'':'Not '}Managed` }
                        </div>
                        <div className={'profile-entry-container'}>
                            <QueryBuilderIcon style={{marginRight:'8px'}}/>
                            Needed at: {new Date(post.needed).toLocaleDateString('en-GB')}
                        </div>
                        <div className={'profile-entry-container'}>
                            <LocationOn style={{marginRight:'8px'}}/>
                            <LocationViewer location={post.location} />
                        </div>
                        <div className={'profile-entry-container'}>
                            <InfoIcon style={{marginRight:'8px'}}/>
                            <span style={{whiteSpace: "pre-wrap"}}>{post.info}</span>
                        </div>
                    </div>
                </div>
                <div className={'post-menu'}>
                    <IconButton onClick={handleClick}><MoreVertIcon/> </IconButton>
                    <Menu
                      id="simple-menu"
                      anchorEl={anchorEl}
                      keepMounted
                      open={Boolean(anchorEl)}
                      onClose={handleClose}
                    >
                      <MenuItem onClick={(e) => {
                        handleClose()
                        navigator.clipboard.writeText(`${window.location.origin}/user/post/${post.id}`).then(
                          () => store.dispatch(notifyUser('Copied!')),
                          () => store.dispatch(notifyUser('Can not copy!'))
                        )
                      }}><FileCopyOutlined style={{paddingRight: '8px'}}/> Copy Link</MenuItem>
                      <MenuItem onClick={(e) => {
                        handleClose()
                        history.push(`/user/post/${post.id}`)
                      }}><VisibilityOutlined style={{paddingRight: '8px'}}/> View Post</MenuItem>
                      { editAccess && [
                        <MenuItem onClick={(e) => {
                          handleClose(e)
                          changePostManaged(post.id).then (
                            data => setPost(data),
                            err => console.log(err)
                          )
                        }} key={1}><CheckCircleOutlined style={{paddingRight: '8px'}}/>Change Managed</MenuItem>,
                        <MenuItem onClick={(e) => {
                          handleClose(e)
                          setOpen(true)
                        }} key={2}><DeleteOutlined style={{paddingRight: '8px'}}/>Delete</MenuItem>
                      ]}
                    </Menu>
                    <Dialog open={open} onClose={e => setOpen(false)}>
                      <DialogTitle >
                        Confirm Delete
                      </DialogTitle>
                      <DialogContent>
                        Do you really want to delete this?
                      </DialogContent>
                      <DialogActions>
                        <Button onClick={e => setOpen(false)} color="primary">
                          No
                        </Button>
                        <Button onClick={e => {
                          store.dispatch(notifyUser('Deleting...'))
                          setOpen(false)
                          deletePost(post.id).then(
                            () => setPost(null),
                            err => console.log(err)
                          )
                        }} color="primary" autoFocus>
                          Yes
                        </Button>
                      </DialogActions>
                    </Dialog>
                </div>
                <div className={'post-right profile-bg'}>
                    {post.bloodGroup}
                </div>
            </div>
        </Paper>
    )
}

export default Posts
