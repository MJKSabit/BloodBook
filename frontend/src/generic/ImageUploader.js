import { Avatar, CircularProgress, Grid } from "@material-ui/core"
import { useState } from "react"
import { uploadImage } from "../store/action"

export default function ImageUploader({onUpload}) {
  const [isUploading, setUploading] = useState(false)
  const [uploadLink, setUploadLink] = useState(null)
  
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
        <input type="file" onChange={ onFileSelected } accept="image/*"/> 
      </Grid>
    </Grid>
  )
}