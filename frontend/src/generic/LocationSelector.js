import { mapboxGeoCoding } from '../store/action';
import React from 'react';
import TextField from '@material-ui/core/TextField';
import { Box, Grid, IconButton, Typography } from '@material-ui/core';
import { LocationOnOutlined, Search } from '@material-ui/icons';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemAvatar from '@material-ui/core/ListItemAvatar';
import ListItemText from '@material-ui/core/ListItemText';
import DialogTitle from '@material-ui/core/DialogTitle';
import Dialog from '@material-ui/core/Dialog';
import LocationViewer from './LocationViewer';
import { useEffect } from 'react';
import axios from 'axios';
import { useState } from 'react';

function SimpleDialog(props) {
  const { onClose, open, options } = props;

  const handleClose = () => {
    onClose(null, null);
  };

  const handleListItemClick = (lat, long) => {
    onClose(lat, long);
  };

  return (
    <Dialog onClose={handleClose} aria-labelledby="simple-dialog-title" open={open}>
      <DialogTitle id="simple-dialog-title">Select Location</DialogTitle>
      <List>
        {options.map((option) => (
          <ListItem button onClick={() => handleListItemClick(option.lat, option.long)} key={option.lat+option.long}>
            <ListItemAvatar>
              <LocationOnOutlined />
            </ListItemAvatar>
            <ListItemText primary={option.name} />
          </ListItem>
        ))}
      </List>
    </Dialog>
  );
}

export default function LocationSelector({lat, long, onSelected}) {
  const [open, setOpen] = React.useState(false);
  const [location, setLocation] = React.useState({lat: lat || 0, long: long || 0})
  const [options, setOptions] = React.useState([]);
  const [typed, setTyped] = useState('')

  useEffect(() => {
    axios.get('https://extreme-ip-lookup.com/json/').then(
      res => { if (res.data.status==='success' && !(location.lat || location.long)) {
        setLocation({lat: res.data.lat, long: res.data.lon})
        onSelected && onSelected(res.data.lat, res.data.lon)
      }}
    )
  }, [])

  const search = (query) => {
    mapboxGeoCoding(query).then(locations => {
      setOptions(locations)
      setOpen(true)
    })
  }

  const handleClose = (lat, long) => {
    setOpen(false);
    setLocation({lat, long})
    onSelected && onSelected(lat, long)
  };

  return (
    <Box pt={2} pb={2}>
      <Grid container spacing={1}>
        <Grid item xs={12}>
          <TextField 
            variant='outlined'
            label='Press ENTER to search' 
            style={{'width': '100%'}}
            onKeyDown={
              e => e.key === 'Enter' && search(e.target.value)
            }
            onChange={
              e => setTyped(e.target.value)
            }
            value={typed}
            InputProps={{
              endAdornment: (
                <IconButton onClick={e => search(typed)}>
                  <Search color='inherit' size={20} />
                </IconButton>
              ),
            }}
          />
          <SimpleDialog open={open} onClose={handleClose} options={options} />
        </Grid>
        <Grid item xs={6} style={{paddingTop: '20px', paddingBottom: '10px'}}>
          <TextField label='Latitude' value={location.lat || ''} variant='outlined' style={{'width': '100%'}}
            onBlur={ e => (onSelected && onSelected(location.lat, location.long))} onChange={ e => setLocation({lat: e.target.value, long: location.long})} />
        </Grid>
        <Grid item xs={6} style={{paddingTop: '20px', paddingBottom: '10px'}}>
          <TextField label='Longitude' value={location.long || ''} variant='outlined' style={{'width': '100%'}} 
          onBlur={ e => (onSelected && onSelected(location.lat, location.long))} onChange={ e => setLocation({long: e.target.value, lat: location.lat})} />
        </Grid>
        <Grid item xs={12}>
          <Typography variant='body2' color='textSecondary' style={{paddingBottom: '10px'}}>
            Selected: <LocationViewer location={{latitude: location.lat, longitude: location.long}} />
          </Typography>
        </Grid>
      </Grid>
    </Box>
  );
}