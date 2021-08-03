import React from 'react'
import './posts.css'
import './profile.css'
import {Avatar, Box, Button, IconButton, Menu, MenuItem, Typography} from "@material-ui/core";
import Paper from "@material-ui/core/Paper";
import MoreVertIcon from '@material-ui/icons/MoreVert';
import QueryBuilderIcon from '@material-ui/icons/QueryBuilder';
import InfoIcon from '@material-ui/icons/Info';
import { useState } from 'react';
import { useEffect } from 'react';
import { changePostManaged, deletePost, getPosts, notifyUser } from '../../store/action';
import { Link } from '@material-ui/core';
import { CheckCircle, LocationOn } from '@material-ui/icons';
import { getMapLink } from './UserCard';
import { Link as RouterLink } from "react-router-dom";
import store from '../../store';

const Posts = props => {
  const {url, label} = props
  const [posts, setPosts] = useState([])
  const [hasMore, setHasMore] = useState(true)
  const [page, setPage] = useState(0)
  const [loading, setLoading] = useState(false)

  const fetchData = (posts, more, page) => {
    if (url && more) {
      setLoading(true)
      getPosts(url, page).then(response => {
        console.log(response)
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
              Load More
            </Button>
          </div>
      </div>
  )
}

export const Post = props => {
  const [anchorEl, setAnchorEl] = React.useState(null);
  const [post, setPost] = useState(props.post)

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
                    <Link component={RouterLink} to={`/user/post/${post.id}`} variant='body2'>
                      {new Date(post.posted).toLocaleDateString()}
                    </Link>
                    </div>
                    <div className={'post-info-container'}>
                        <div className={'profile-entry-container'}>
                            <CheckCircle style={{marginRight:'8px'}}/>
                            Status: {`${post.managed?'':'Not '}Managed` }
                        </div>
                        <div className={'profile-entry-container'}>
                            <QueryBuilderIcon style={{marginRight:'8px'}}/>
                            Needed at: {new Date(post.needed).toLocaleDateString()}
                        </div>
                        <div className={'profile-entry-container'}>
                            <LocationOn style={{marginRight:'8px'}}/>
                            <Link 
                              href={getMapLink(post.location.latitude, post.location.longitude)}
                              rel="noopener noreferrer" 
                              target="_blank">
                              {`${post.location.latitude}, ${post.location.longitude} ↗️`}
                            </Link>
                        </div>
                        <div className={'profile-entry-container'}>
                            <InfoIcon style={{marginRight:'8px'}}/>
                            {post.info}
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
                      }}>Copy Link</MenuItem>
                      { editAccess && <>
                        <MenuItem onClick={(e) => {
                          handleClose(e)
                          changePostManaged(post.id).then (
                            data => setPost(data),
                            err => console.log(err)
                          )
                        }}>Change Managed</MenuItem>
                        <MenuItem onClick={(e) => {
                          handleClose(e)
                          deletePost(post.id).then(
                            () => setPost(null),
                            err => console.log(err)
                          )
                        }}>Delete</MenuItem>
                      </>}
                    </Menu>
                </div>
                <div className={'post-right profile-bg'}>
                    {post.bloodGroup}
                </div>
            </div>
        </Paper>
    )
}

export default Posts
