import React from 'react'
import './posts.css'
import './profile.css'
import {Avatar} from "@material-ui/core";
import Paper from "@material-ui/core/Paper";
import MoreVertIcon from '@material-ui/icons/MoreVert';
import MailIcon from "@material-ui/icons/Mail";

const Posts = props => {
    return(
        <div>
            <div className={'posts-title'}>
                Posts
            </div>
            <div className={'posts-list-container'}>
                <Post/>
                <Post/>
                <Post/>
            </div>
        </div>
    )
}

const Post = props => {
    return(
        <Paper>
            <div className={'post-container'}>
                <div className={'post-left'}>
                    <Avatar style={{width:'60px',height:'60px'}}/>
                </div>
                <div className={'post-mid'}>
                    <div className={'post-name'}>
                        John Doe
                    </div>
                    <div className={'post-date'}>
                        29 February, 2020
                    </div>
                    <div className={'post-info-container'}>
                        <div className={'profile-entry-container'}>
                            <MailIcon style={{marginRight:'8px'}}/>
                            johndoe@example.com
                        </div>
                        <div className={'profile-entry-container'}>
                            <MailIcon style={{marginRight:'8px'}}/>
                            johndoe@example.com
                        </div>
                        <div className={'profile-entry-container'}>
                            <MailIcon style={{marginRight:'8px'}}/>
                            johndoe@example.com
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
