import { Box, Container, Typography } from "@material-ui/core"
import { useEffect } from "react"
import { useHistory, useParams } from "react-router-dom"
import store from "../store"
import { activateAccount, notifyUser } from "../store/action"

const ActivateAccount = props => {
  const {jwt} = useParams()
  const history = useHistory()

  useEffect( () => {
    activateAccount(jwt).then(
      () => history.push('/'),
      () => store.dispatch(notifyUser('Invalid Token!'))
    )
  }, [])

  return <Container maxWidth='sm'>
    <Box marginY={5}>
      <Typography variant='h4' align='center'>
        Verifying...
      </Typography>
    </Box>
  </Container>

  
}

export default ActivateAccount