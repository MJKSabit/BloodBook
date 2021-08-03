import { Box, Paper, Typography } from "@material-ui/core"
import { useEffect } from "react"
import { useState } from "react"
import { exploreBanks } from "../../store/action"
import { BankList } from "./PostDetails"

const Explore = (props) => {
  const [banks, setBanks] = useState(null)

  useEffect(() => {
    exploreBanks().then(
      data => setBanks(data),
      err => console.log(err)
    )
  }, [])

  if (banks === null)
    return null

  return <><Box mt={5} mb={5} pl={3}>
    <Typography color='textSecondary' variant='h5'>
      BloodBanks near you:
    </Typography>
  </Box>
  <BankList banks={banks} />
  </>
}

export default Explore