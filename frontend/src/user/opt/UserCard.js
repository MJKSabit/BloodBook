import React from 'react'
import Paper from "@material-ui/core/Paper";
import {Avatar, Box, Grid, useMediaQuery} from "@material-ui/core";
import './profile.css'
import MailIcon from '@material-ui/icons/Mail';
import { Facebook, InvertColors, LocationOn, WatchLater } from '@material-ui/icons';
import { Link } from '@material-ui/core';

export function getMapLink(lat, lng) {
  return "https://www.google.com/maps/search/?api=1&query="+lat+","+lng;
}

const UserCard = props => {

  const {user} = props
  const matches = useMediaQuery('(min-width:960px)')

  if (!user)
    return null
  
  const ownProfile = user.user.email !== undefined

  return(
    <div style={{marginTop: '30px'}}>
      <Paper style={{padding:'30px'}}>
        <Grid container spacing={2}>
          <Grid item xs={4}>
            <center>
              <Avatar 
                style={{position:'relative', width:'100%', height:'100%'}} 
                src={user.imageURL}/>
            </center>
          </Grid>
          <Grid item xs={8}>
            <div className={'profile-header'}>
              <div className={'profile-name'}>
                {user.name}
              </div>
              { matches && <div className={'profile-bg'}>
                  {user.bloodGroup}
              </div>}
            </div>
            <div className={'profile-body'}>
              {user.about}
            </div>
            {!matches && <Box display="flex" flexDirection="row-reverse">
                <Box>
                <div className={'profile-bg'}>
                    {user.bloodGroup}
                </div>
                </Box>
              </Box>}
          </Grid>
        </Grid>
      </Paper>
      { ownProfile && <Paper style={{padding:'30px',marginTop:'20px'}}>
        <Grid container spacing={2}>
            <Grid item xs={12} md={6}>
                <div className={'profile-entry-container'} title='Email'>
                    <MailIcon style={{marginRight:'8px'}}/>
                    {user.user.email}
                </div>
                <div className={'profile-entry-container'} title='Facebook'>
                    <Facebook style={{marginRight:'8px'}}/>
                    {user.facebook ? 'Connected' : 'Not Connected'}
                </div>
            </Grid>
            <Grid item xs={12} md={6}>
                <div className={'profile-entry-container'}>
                    <InvertColors style={{marginRight:'8px'}}/>
                    {user.isActiveDonor ? "Active Donor" : "Inactive"}
                </div>
                <div className={'profile-entry-container'}>
                    <WatchLater style={{marginRight:'8px'}}/>
                    {`Last Donation: ${new Date(user.lastDonation).toLocaleDateString()}`}
                </div>
                <div className={'profile-entry-container'}>
                    <LocationOn style={{marginRight:'8px'}}/>
                    <Link 
                      href={getMapLink(user.user.location.latitude, user.user.location.longitude)}
                      rel="noopener noreferrer" 
                      target="_blank">
                      {`${user.user.location.latitude}, ${user.user.location.longitude} ↗️`}
                    </Link>
                </div>
            </Grid>
        </Grid>
      </Paper>}
    </div>
  )
}


export default UserCard
