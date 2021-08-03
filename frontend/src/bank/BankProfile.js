import { Avatar, Box, Card, CardContent, Grid, Link, Paper, Typography } from "@material-ui/core"
import { Email, LocationOn } from "@material-ui/icons"
import { useEffect } from "react"
import { useState } from "react"
import store from "../store"
import { getBankBloodCount } from "../store/action"
import { getMapLink } from "../user/opt/UserCard"
import "./styles.css"

const ProfileCard = props => {
  const {user} = props

  if (!user)
    return null

  return <Box mt={5}>
    <Paper>
      <Box p={6} >
        <Grid container spacing={4}>
          <Grid item xs={12} sm={4}>
            <Box className='square'>
              <Avatar src={user.imageURL} 
              style={{position:'absolute', width:'100%', height:'100%'}} 
              />
            </Box>
          </Grid>
          <Grid item xs={12} sm={8}>
            <Grid
              container
              direction="column"
              justifyContent="flex-start"
              alignItems="flex-start"
              spacing={2}
            >
              <Grid item>
                <Typography variant='h4'>
                  {user.name}
                </Typography>
              </Grid>
              <Grid item>
                <Grid
                  container
                  alignItems="center"
                  spacing={1}
                >
                  <Grid item>
                    <Email />
                  </Grid>
                  <Grid item>
                    {user.user.email}
                  </Grid>
                </Grid>
              </Grid>
              <Grid item>
                <Grid
                  container
                  alignItems="center"
                  spacing={1}
                >
                  <Grid item>
                    <LocationOn/>
                  </Grid>
                  <Grid item>
                    <Link 
                      href={getMapLink(user.user.location.latitude, user.user.location.longitude)}
                      rel="noopener noreferrer" 
                      target="_blank"
                    >
                      {`${user.user.location.latitude}, ${user.user.location.longitude} ↗️`}
                    </Link>
                  </Grid>
                </Grid>
              </Grid>
              <Grid item>
                <Typography variant='subtitle2'>
                  {user.about}
                </Typography>
              </Grid>
            </Grid>
          </Grid>
        </Grid>
      </Box>
    </Paper>
  </Box>
}

const BGCountCard = ({bloodGroup, inStock}) => {
  return <Card variant='outlined'>
    <CardContent>
      <Typography color='textSecondary' component='p'>
        {bloodGroup}
      </Typography>
      <Typography variant='h3'>
        {inStock}
      </Typography>
    </CardContent>
  </Card>
}

const CountCard = (props) => {
  const {username} = props
  const [data, setData] = useState(null)

  useEffect(() => {
    getBankBloodCount(username).then(
      dt => setData(dt),
      err => console.log(err)
    )
  }, [])

  if (data === null) 
    return null

  return <Paper>
    <Box p={3} mt={5}>
      <Typography variant='body1' style={{marginBottom: '20px'}}>Blood in Stock</Typography>
      <Grid container spacing={3}>
        { data.map(value => (
          <Grid item md={3} sm={4} xs={6}>
            <BGCountCard bloodGroup={value.bloodGroup} inStock={value.inStock} />
          </Grid>
        ))}
      </Grid>
    </Box>
  </Paper>
}

const BankProfile = (props) => {
  const [profile, setProfile] = useState(store.getState().profile)

  useEffect(() => store.subscribe( () => setProfile(store.getState().profile)), [])

  if (profile === null)
    return null

  const username = profile.user.username

  return <>
    <ProfileCard user={profile}/>
    <CountCard username={username} />
  </>
}

export default BankProfile