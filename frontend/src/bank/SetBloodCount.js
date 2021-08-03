import { Box, Button, Grid, Paper, TextField, Typography } from "@material-ui/core"
import { useEffect } from "react"
import { useState } from "react"
import { useHistory } from "react-router-dom"
import { getBankBloodCount, setBloodCount } from "../store/action"

const SetBloodCount = props => {
  const history = useHistory()
  const [blood, setBlood] = useState(null)

  useEffect( () => {
    getBankBloodCount().then(
      count => {
        const data = {}
        count.forEach(value => (data[value.bloodGroup] = value.inStock))
        setBlood(data)
      }
    )
  }, [])

  if (blood === null)
    return null

  const inputViews = []

  for (let bg in blood) {
    inputViews.push(
      <Grid item md={3} sm={4} xs={6} key={bg}>
        <TextField value={blood[bg]} label={bg} variant='outlined'
        onChange={ e => {
          if (e.target.value === '')
            e.target.value = '0'
          setBlood({...blood, [bg]: Number.parseInt(e.target.value)})
        }} />
      </Grid>
    )
  }

  return <Paper>
    <Box p={3} mt={5} mb={5}>
      <Box mb={3}><Typography variant='body1'>Set Blood Count</Typography></Box>
      <Grid container spacing={3} >
        {inputViews}
        <Grid item xs={12}>
          <Box sx={{display: 'flex', flexDirection: 'row-reverse'}} >
            <Button variant='contained' color='primary'
             onClick={
               () => setBloodCount(blood).then(
                 () => {history.push('/bloodbank/profile')})
              }>Submit</Button>
          </Box>
        </Grid>
      </Grid>
    </Box>
  </Paper>
}

export default SetBloodCount