import DateFnsUtils from "@date-io/date-fns";
import { Backdrop, Box, Button, Checkbox, CircularProgress, Dialog, DialogContent, DialogTitle, FormControl, FormControlLabel, Grid, InputLabel, MenuItem, Select, TextField, Typography } from "@material-ui/core";
import { KeyboardDatePicker, MuiPickersUtilsProvider } from "@material-ui/pickers";
import { useState } from "react";
import LocationSelector from "../../generic/LocationSelector";
import store from "../../store";
import { createNewPost, notifyUser } from "../../store/action";

export default function CreatePost(props) {
  const location = store.getState().profile.user.location

  const [open, setOpen] = useState(false)
  const [loading, setLoading] = useState(false)
  const [bloodGroup, setBloodgroup] = useState('A+')
  const [selectedDate, setSelectedDate] = useState(new Date())
  const [notify, setNotify] = useState(true)
  const [info, setInfo] = useState('');
  const [longitude, setLongitude] = useState(location.longitude)
  const [latitude, setLatitude] = useState(location.latitude)

  return (
    <>
      <Box pt={5}>
        <Box>
          <Button fullWidth 
            onClick={() => setOpen(true)} variant="contained" color="primary" mt='10'>
            Create New Post
          </Button>
        </Box>
      </Box>
      <Backdrop open={loading} onClick={ () => store.dispatch(notifyUser('Still Loading!'))} style={{zIndex: '99999'}}>
        <CircularProgress color="inherit" />
      </Backdrop>
      <Dialog fullWidth maxWidth='sm' open={open} onClose={() => setOpen(false)}>
        <DialogTitle>Create BloodBook Post</DialogTitle>
        <DialogContent>
          <Grid container>
            <Grid item xs={12}>
              <FormControl fullWidth>
                <TextField
                  label="Blood Group *"
                  select
                  variant='outlined'
                  value={bloodGroup}
                  onChange={ (e) => setBloodgroup(e.target.value) }
                >
                  <MenuItem value={'A+'}>A+</MenuItem>
                  <MenuItem value={'B+'}>B+</MenuItem>
                  <MenuItem value={'AB+'}>AB+</MenuItem>
                  <MenuItem value={'O+'}>O+</MenuItem>
                  <MenuItem value={'A-'}>A-</MenuItem>
                  <MenuItem value={'B-'}>B-</MenuItem>
                  <MenuItem value={'AB-'}>AB-</MenuItem>
                  <MenuItem value={'O-'}>O-</MenuItem>
                </TextField>
              </FormControl>
            </Grid>
            <Grid item xs={12}>
              <MuiPickersUtilsProvider utils={DateFnsUtils} fullWidth>
                <KeyboardDatePicker
                  inputVariant="outlined"
                  fullWidth
                  disableToolbar
                  variant="inline"
                  format="dd/MM/yyyy"
                  margin="normal"
                  id="date-picker-inline"
                  label="Needed in"
                  value={selectedDate} 
                  onChange={ (e) => setSelectedDate(e)}
                  KeyboardButtonProps={{
                    'aria-label': 'change date',
                  }}
                />
              </MuiPickersUtilsProvider>
            </Grid>
            <Grid item xs={4}>
              <Typography variant='body2'>
                Location
              </Typography>
            </Grid>
            <Grid item xs={8}>
              <LocationSelector onSelected={
                (lat, long) => {setLatitude(lat); setLongitude(long)}
              } />
            </Grid>
            <Grid item xs={12}>
              <TextField
                label="Information"
                multiline
                rows={4}
                value={info}
                onChange={ e => setInfo(e.target.value) }
                variant="outlined"
                fullWidth
              />
            </Grid>
            <Grid item xs={12}>
              <FormControlLabel
                control={<Checkbox checked={notify} onChange={ (e) => setNotify(e.target.checked)} />}
                label="Notify BloodBook Users"
              />
            </Grid>
            <Grid item xs={12}>
              <Grid container spacing={5}>
                <Grid item xs={6}>
                  <Button fullWidth variant="outlined" color='secondary' onClick={() => setOpen(false)}>Cancel</Button>
                </Grid>
                <Grid item xs={6}>
                  <Button fullWidth variant="contained" color="primary" onClick={
                    () => {
                      setLoading(true)
                      createNewPost({
                        info, 
                        bloodGroup, 
                        managed: false, 
                        needed: selectedDate.getTime(),
                        posted: new Date().getTime(),
                        location: {
                          latitude,
                          longitude
                        }
                      }, notify).then(
                        data => {setOpen(false); setLoading(false)}, 
                        err => {console.log(err); setLoading(false)})
                    }
                  }>Post</Button>
                </Grid>
              </Grid>
            </Grid>
          </Grid>
        </DialogContent>
      </Dialog>
    </>
  )
}