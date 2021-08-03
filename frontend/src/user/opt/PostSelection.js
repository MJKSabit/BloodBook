import { Box, Button, ButtonGroup } from "@material-ui/core"
import { useState } from "react"
import Posts from "./Posts"


const eventUrl = '/user/posts'
const options = ['Posts For Me', 'All Posts']
const requestMapping = ['my', 'all']


const PostSelection = props => {
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
    <Posts url={url} />
  </>
}

export default PostSelection