import { Backdrop, Box, CircularProgress, Container, Divider, IconButton, InputBase, makeStyles, Menu, Paper, Typography } from "@material-ui/core"
import zIndex from "@material-ui/core/styles/zIndex"
import { Directions, Lock, Search, VpnKey } from "@material-ui/icons"
import { useState } from "react"
import { useEffect } from "react"
import { useHistory, useParams } from "react-router-dom"
import store from "../store"
import { activateAccount, notifyUser } from "../store/action"

const useStyles = makeStyles((theme) => ({
  root: {
    padding: '2px 4px',
    display: 'flex',
    alignItems: 'center',
    width: 400,
  },
  input: {
    marginLeft: theme.spacing(1),
    flex: 1,
  },
  iconButton: {
    padding: 10,
  },
  divider: {
    height: 28,
    margin: 4,
  },
}));

const ActivateAccount = props => {
  const {jwt} = useParams()
  const history = useHistory()
  const [jwtIn, setJwtIn] = useState(jwt || '')
  const [loading, setLoading] = useState(false)

  const classes = useStyles();

  return <Container maxWidth='sm'>
    <Box marginY={5}>
      <Paper component="form" className={classes.root}>
          <Lock color='disabled' style={{padding: '10px'}}/>
        <InputBase
          className={classes.input}
          placeholder="Enter Token Here"
          value={jwtIn}
          onChange={ (e) => setJwtIn(e.target.value)}
        />
        <Divider className={classes.divider} orientation="vertical" />
        <IconButton color="primary" className={classes.iconButton} aria-label="directions"
          onClick={ () => {
            setLoading(true)
            activateAccount(jwtIn).then(
                  () => {setLoading(false); store.dispatch(notifyUser('Done!')); history.push('/')},
                  () => {setLoading(false); store.dispatch(notifyUser('Invalid Token!'))}
                )
          }} disabled={jwtIn === ''}>
          <VpnKey />
        </IconButton>
        <Backdrop open={loading} onClick={() => store.dispatch(notifyUser('Still Verifying!'))} 
          style={{zIndex: '9999'}}>
          <CircularProgress />
        </Backdrop>
      </Paper>
    </Box>
  </Container>

  
}

export default ActivateAccount