import { mapboxGeoCoding } from '../store/action';
import React from 'react';
import TextField from '@material-ui/core/TextField';
import { Box, Grid, Typography } from '@material-ui/core';
import { LocationOnOutlined, Search } from '@material-ui/icons';
import { makeStyles } from '@material-ui/core/styles';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemAvatar from '@material-ui/core/ListItemAvatar';
import ListItemText from '@material-ui/core/ListItemText';
import DialogTitle from '@material-ui/core/DialogTitle';
import Dialog from '@material-ui/core/Dialog';
import { blue } from '@material-ui/core/colors';

const useStyles = makeStyles({
  avatar: {
    backgroundColor: blue[100],
    color: blue[600],
  },
});

function SimpleDialog(props) {
  const classes = useStyles();
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
  const [location, setLocation] = React.useState({lat: lat, long: long})
  const [options, setOptions] = React.useState([]);

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
            label='Location' 
            style={{'width': '100%'}}
            onKeyDown={
              e => e.key === 'Enter' && search(e.target.value)
            }
            InputProps={{
              endAdornment: (
                <Search color='inherit' size={20} />
              ),
            }}
          />
          <SimpleDialog open={open} onClose={handleClose} options={options} />
        </Grid>
        <Grid item xs={6}>
          <TextField label='Latitude' value={location.lat || ''} variant='outlined' style={{'width': '100%'}} />
        </Grid>
        <Grid item xs={6}>
          <TextField label='Longitude' value={location.long || ''} variant='outlined' style={{'width': '100%'}} />
        </Grid>
      </Grid>
    </Box>
  );
}