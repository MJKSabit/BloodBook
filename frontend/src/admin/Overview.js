import { Box, Card, CardContent, CircularProgress, Grid, Paper, Typography } from "@material-ui/core"
import { useEffect } from "react"
import { useState } from "react"
import { getAdminStats } from "../store/action"

const CountCard = ({countOf, count}) => {
  return <Card variant='outlined'>
    <CardContent>
      <Typography color='textSecondary' component='p' align='right' style={{marginBottom: '10px'}}>
        {countOf}
      </Typography>
      <Typography variant='h3' align='center'>
        {count}
      </Typography>
    </CardContent>
  </Card>
}

const Overview = props => {
  const [data, setData] = useState(null)

  useEffect(() => {
    getAdminStats().then(obj => {
      const arr = []
      for (let key in obj)
        arr.push({header: key, count: obj[key]})
      setData(arr)
    })
  }, [])

  if (data === null) 
    return (<Box display='flex' style={{alignItems: 'center', justifyContent: 'center'}} my={5}>
              <CircularProgress />
            </Box>)
  
  return <Paper>
  <Box p={3} mt={2} mb={2}>
    <Typography variant='body1' style={{marginBottom: '20px'}}>Overview</Typography>
    <Grid container spacing={3}>
      { data.map(value => (
        <Grid item md={4} xs={6}>
          <CountCard countOf={value.header} count={value.count} />
        </Grid>
      ))}
    </Grid>
  </Box>
</Paper>
  
}

export default Overview