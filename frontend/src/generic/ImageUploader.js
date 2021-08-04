import { Avatar, Button, CircularProgress, Grid } from "@material-ui/core"
import { CloudUploadOutlined } from "@material-ui/icons"
import { useState } from "react"
import { uploadImage } from "../store/action"

export default function ImageUploader({onUpload, init=null}) {
  const [isUploading, setUploading] = useState(false)
  const [uploadLink, setUploadLink] = useState(init)
  
  const onFileSelected = (e) => {
    const file = e.target.files[0]
    if (!file) return
    setUploading(true)
    uploadImage(file).then(link => {
      onUpload && onUpload(link)
      setUploadLink(link)
      setUploading(false)
    })
  }

  return (
    <Grid container direction="row"
      alignItems="center" spacing={3} style={{width: '100%'}} justifyContent="center">
      <Grid item>
        {isUploading ? 
          <CircularProgress /> :
          <Avatar src={uploadLink} style={{ height: '100px', width: '100px' }}>
            Img
          </Avatar>
        }
      </Grid>
      <Grid item>
        <input type="file" onChange={ onFileSelected } accept="image/*" multiple={false}
          style={{ display: 'none' }} id="raised-button-file"/>
        <label htmlFor="raised-button-file">
          <Button variant="outlined" component="span">
            <CloudUploadOutlined style={{marginRight: '8px'}}/> Upload
          </Button>
        </label> 
      </Grid>
    </Grid>
  )
}