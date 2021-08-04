import { Box, Paper, Typography } from "@material-ui/core"
import { useEffect } from "react"
import { useState } from "react"
import { exploreBanks } from "../../store/action"
import { BankList } from "./PostDetails"

const Explore = (props) => {
  const [banks, setBanks] = useState(null)
  const userType = props.userType || 'user'

  useEffect(() => {
    exploreBanks().then(
      data => setBanks(data),
      err => console.log(err)
    )
  }, [])

  if (banks === null)
    return <Paper style={{padding: '20px'}}>Still Loading...</Paper>

  return <><Box mt={5} mb={5} pl={3}>
    <Typography color='textSecondary' variant='h5'>
      BloodBanks near you:
    </Typography>
  </Box>
  { banks.length !== 0 ?
    <BankList banks={banks} userType={userType} /> :
    <Typography color='textSecondary' variant='subtitle2' style={{padding: '10px'}}>
      No bloodbank near you!
    </Typography>
  }
  </>
}

export default Explore