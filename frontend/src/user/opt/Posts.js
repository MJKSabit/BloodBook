import React from 'react'
import './posts.css'
import './profile.css'
import {Avatar, Box, Button} from "@material-ui/core";
import Paper from "@material-ui/core/Paper";
import MoreVertIcon from '@material-ui/icons/MoreVert';
import QueryBuilderIcon from '@material-ui/icons/QueryBuilder';
import InfoIcon from '@material-ui/icons/Info';
import { useState } from 'react';
import { useEffect } from 'react';
import { getPosts } from '../../store/action';
import { Link } from '@material-ui/core';
import { CheckCircle, LocationOn } from '@material-ui/icons';
import { getMapLink } from './UserCard';
import { Link as RouterLink } from "react-router-dom";

const Posts = props => {
  const {url, label} = props
  const [posts, setPosts] = useState([])
  const [hasMore, setHasMore] = useState(true)
  const [page, setPage] = useState(0)

  const fetchData = () => {
    if (url && hasMore) {
      setHasMore(false)
      getPosts(url, page).then(response => {
        console.log(response)
        setPosts(posts.concat(response.content))
        setHasMore(response.hasNext)
        setPage(response.page+1)
      })
    }
  }

  useEffect( () => {
    if (url && hasMore) {
      setHasMore(false)
      getPosts(url, page).then(response => {
        console.log(response)
        setPosts(posts.concat(response.content))
        setHasMore(response.hasNext)
        setPage(response.page+1)
      })
    }
  }, [])

  return(
      <div>
          <div className={'posts-title'}>
            <Box pt={4}>
              {label || 'Posts'}
            </Box>
          </div>
          <div className={'posts-list-container'}>
              {posts.map( post => (<Post post={post}></Post>))}
          </div>
          <div className={'post-list-container'} style={{visibility: hasMore ? 'visible': 'hidden'}}>
            <Button onClick={() => fetchData()} disabled={!hasMore} fullWidth='true'>
              Load More
            </Button>
          </div>
      </div>
  )
}

const Post = props => {
  const {post} = props
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
                    <MoreVertIcon/>
                </div>
                <div className={'post-right profile-bg'}>
                    A+
                </div>
            </div>
        </Paper>
    )
}

export default Posts
