import { Box, Button, ButtonGroup } from "@material-ui/core"
import { useState } from "react"


const eventUrl = '/user/events'
const options = ['Events For Me', 'All Events']
const requestMapping = ['my', 'all']


const EventSelection = props => {
  const [selected, setSelected] = useState(0)

  const url = `${eventUrl}?for=${requestMapping[selected]}`
  return <>
    <Box mt={5} display='flex' flexDirection="row-reverse">
      <ButtonGroup color='primary' size='large'>
        <Button onClick={ () => {selected !== 0 && setSelected(0)}} variant={selected === 0 && 'contained'}>
          {options[0]}
        </Button>
        <Button onClick={ () => {selected !== 1 && setSelected(1)}} variant={selected === 1 && 'contained'}>
          {options[1]}
        </Button> 
      </ButtonGroup>
    </Box>
    {url}
  </>
}

export default EventSelection